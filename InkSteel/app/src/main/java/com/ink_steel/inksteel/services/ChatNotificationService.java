package com.ink_steel.inksteel.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.NotificationHelper;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;

import java.util.List;

public class ChatNotificationService extends Service implements
        DatabaseManager.NotificationManagerListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseManager.NotificationsManager.listenForNewMessages(this);
        return START_STICKY;
    }

    @Override
    public void onUnseenMessage(ChatRoom chatRoom, List<Message> messages) {
        NotificationHelper.displayNotification(this, chatRoom, messages);
    }
}
