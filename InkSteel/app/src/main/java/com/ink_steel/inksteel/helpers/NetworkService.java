package com.ink_steel.inksteel.helpers;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.ChatRoom;

public class NetworkService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listenForMessages();
        return super.onStartCommand(intent, flags, startId);
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
                                if (!chatRoom.getLastMessageSender().equals(user.getEmail()))
                                    showNotification(NetworkService.this,
                                            snapshot.toObject(ChatRoom.class));
                            }
                        }
                    });
        }
    }

    private static final int mNotificationId = 666;

    private void showNotification(Context context, ChatRoom chatRoom) {
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_add)
                        .setContentTitle("New ink steel message from " + chatRoom.getUserName())
                        .setContentText(chatRoom.getLastMessage() + " " + chatRoom.getLastMessageTime());
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mBuilder.build());
    }


}
