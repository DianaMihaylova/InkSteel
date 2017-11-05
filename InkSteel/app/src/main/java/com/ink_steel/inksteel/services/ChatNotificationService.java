package com.ink_steel.inksteel.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.helpers.NotificationUtil;
import com.ink_steel.inksteel.model.ChatRoom;

public class ChatNotificationService extends Service {
    private int notificationId = 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            listenForMessages();
        return START_NOT_STICKY;
    }

    private void listenForMessages() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getEmail())
                    .collection("chatRooms")
                    .whereEqualTo("seen", false)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            for (DocumentSnapshot snapshot : documentSnapshots) {
                                ChatRoom chatRoom = snapshot.toObject(ChatRoom.class);
                                if (!chatRoom.getLastMessageSender().equals(user.getEmail())) {
                                    NotificationUtil
                                            .showNotification(ChatNotificationService.this,
                                                    chatRoom, notificationId++);
                                }
                            }

                        }
                    });
        }
    }



}
