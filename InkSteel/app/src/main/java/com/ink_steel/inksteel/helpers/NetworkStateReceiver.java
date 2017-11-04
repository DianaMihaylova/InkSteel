package com.ink_steel.inksteel.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;


public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                            DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(0);
                            showNotification(context, snapshot.toObject(ChatRoom.class));
                        }
                    });
        }
    }

    private static final int mNotificationId = 666;

    private void showNotification(Context context, ChatRoom message) {
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_add)
                        .setContentTitle("New ink steel message from " + message.getUserName())
                        .setContentText(message.getLastMessage() + " " + message.getLastMessageTime());
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mBuilder.build());

    }
}
