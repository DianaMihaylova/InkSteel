package com.ink_steel.inksteel.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.Message;
import com.ink_steel.inksteel.receivers.NotificationReplyReceiver;

import java.util.Date;

import static com.ink_steel.inksteel.helpers.NotificationUtil.CHAT_ID;
import static com.ink_steel.inksteel.helpers.NotificationUtil.MESSAGE;
import static com.ink_steel.inksteel.helpers.NotificationUtil.NOTIFICATION_ID;
import static com.ink_steel.inksteel.helpers.NotificationUtil.OTHER_USER_EMAIL;

public class NotificationReplyService extends IntentService {

    private NotificationReplyReceiver mReceiver;

    public NotificationReplyService() {
        super("NotificationReplyService");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(NotificationReplyReceiver.ACTION_REPLY);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mReceiver = new NotificationReplyReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            CharSequence answer = remoteInput.getCharSequence(MESSAGE);
            if (answer != null) {
                String message = answer.toString();
                String chatId = intent.getStringExtra(CHAT_ID);
                String otherUserEmail = intent.getStringExtra(OTHER_USER_EMAIL);
                int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
                if (!chatId.isEmpty() && !chatId.isEmpty() && !message.isEmpty()) {
                    sendMessage(chatId, message, otherUserEmail, notificationId);
                }
            }
        }
    }

    private void sendMessage(String chatId, String message, String otherUserEmail,
                             int notificationId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            DatabaseManager manager = DatabaseManager.getInstance();
            manager.addMessage(new Message(user.getEmail(),
                    message, new Date().getTime()), chatId, otherUserEmail);
            Log.d("noti", otherUserEmail);
            sendBroadcast(notificationId, chatId);
        }
    }

    private void sendBroadcast(int notificationId, String chatId) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NotificationReplyReceiver.ACTION_REPLY);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(NOTIFICATION_ID, notificationId);
        broadcastIntent.putExtra(CHAT_ID, chatId);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
