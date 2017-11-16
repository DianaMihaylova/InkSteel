package com.ink_steel.inksteel.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;

import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.receivers.NotificationReplyReceiver;

import static com.ink_steel.inksteel.helpers.NotificationHelper.CHAT_ID_KEY;
import static com.ink_steel.inksteel.helpers.NotificationHelper.NOTIFICATION_ID_KEY;
import static com.ink_steel.inksteel.helpers.NotificationHelper.RECEIVER_EMAIL_KEY;
import static com.ink_steel.inksteel.helpers.NotificationHelper.REPLY_ACTION_RESULT_KEY;

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
            if (remoteInput != null) {
                CharSequence answer = remoteInput.getCharSequence(REPLY_ACTION_RESULT_KEY);
                if (answer != null) {
                    String message = answer.toString();
                    String chatId = intent.getStringExtra(CHAT_ID_KEY);
                    String otherUserEmail = intent.getStringExtra(RECEIVER_EMAIL_KEY);
                    int notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, 0);
                    if (!chatId.isEmpty() && !chatId.isEmpty() && !message.isEmpty()) {
                        DatabaseManager.NotificationsManager.sendMessage(chatId, message,
                                otherUserEmail);
                        sendBroadcast(notificationId, chatId);
                    }
                }
            }
        }
    }

    private void sendBroadcast(int notificationId, String chatId) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NotificationReplyReceiver.ACTION_REPLY);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(NOTIFICATION_ID_KEY, notificationId);
        broadcastIntent.putExtra(CHAT_ID_KEY, chatId);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
