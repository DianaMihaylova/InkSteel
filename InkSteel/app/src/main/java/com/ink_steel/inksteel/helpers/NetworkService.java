package com.ink_steel.inksteel.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;

import java.util.Date;
import java.util.HashMap;

public class NetworkService extends Service {

    private static final int mNotificationId = 666;
    public static final String NOTIFICATION_DIRECT_REPLY_ACTION
            = "com.ink_steel.inksteel.DIRECT_REPLY";
    private NotificationManager mNotificationManager;
    private Context mContext;
    private HashMap<String, ChatRoom> mStringChatRooms;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mStringChatRooms = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (NOTIFICATION_DIRECT_REPLY_ACTION.equals(intent.getAction())) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null && remoteInput.containsKey("reply")) {
                String message = remoteInput.getCharSequence("reply").toString();
                sendMessage(intent.getStringExtra("chatId"), message);
            }
        } else {
            listenForMessages();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage(String id, String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            DatabaseManager manager = DatabaseManager.getInstance();
            manager.addMessage(new Message(user.getEmail(),
                    message, new Date().getTime()), mStringChatRooms.get(id));
            mNotificationManager.cancel(mNotificationId);
        }
    }

    private void listenForMessages() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getEmail())
                    .collection("chatRooms").whereEqualTo("seen", false)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null)
                                return;
                            for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                                ChatRoom chatRoom = snapshot.toObject(ChatRoom.class);
                                mStringChatRooms.put(chatRoom.getChatId(), chatRoom);
                                if (!chatRoom.getLastMessageSender().equals(user.getEmail()))
                                    showNotification(snapshot.toObject(ChatRoom.class));
                            }
                        }
                    });
        }
    }

    private void showNotification(ChatRoom chatRoom) {
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_add)
                        .setContentTitle("New ink steel message from " + chatRoom.getUserName())
                        .setContentText(chatRoom.getLastMessage() + " " + chatRoom.getLastMessageTime())
                        .addAction(createMessageAction(chatRoom.getChatId()))
                        .setStyle(buildNotificationStyle(chatRoom));
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    private NotificationCompat.Action createMessageAction(String chatId) {

        String replyLabel = "Reply on the notification";
        RemoteInput remoteInput = new RemoteInput.Builder("reply")
                .setLabel(replyLabel).build();

        return new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Reply",
                getSendMessageIntent(chatId))
                .addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build();
    }

    private PendingIntent getSendMessageIntent(String chatId) {
        Intent intent = new Intent(mContext, NetworkService.class);
        intent.setAction(NOTIFICATION_DIRECT_REPLY_ACTION);
        intent.putExtra("chatId", chatId);
        return PendingIntent.getService(getApplicationContext(), mNotificationId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.MessagingStyle buildNotificationStyle(ChatRoom chatRoom) {
        return new NotificationCompat
                .MessagingStyle(chatRoom.getUserName())
                .addMessage(
                        chatRoom.getLastMessage(),
                        chatRoom.getLastMessageTime(),
                        chatRoom.getLastMessageSender());
    }

}
