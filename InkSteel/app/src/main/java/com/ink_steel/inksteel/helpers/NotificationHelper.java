package com.ink_steel.inksteel.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;
import com.ink_steel.inksteel.services.NotificationReplyService;

import java.util.List;

public class NotificationHelper {
    public static final String REPLY_ACTION_RESULT_KEY = "message";
    public static final String CHAT_ID_KEY = "chat_id";
    public static final String NOTIFICATION_ID_KEY = "notification_id";
    public static final String RECEIVER_EMAIL_KEY = "receiver_email";

    private static NotificationManager mManager;

    public static void displayNotification(Context context, ChatRoom chatRoom, List<Message> messages) {
        if (mManager == null)
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "chat_channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                // required
                .setContentTitle(chatRoom.getLastMessageSender())
                .setSmallIcon(R.drawable.notification_icon)
                .setContentText(chatRoom.getLastMessage())
                // add reply action
                .addAction(createReplyAction(context, chatRoom))
                // add messaging style
                .setStyle(buildMessagingStyle(messages));
        mManager.notify(chatRoom.getChatId().hashCode(), builder.build());
    }


    private static NotificationCompat.Action createReplyAction(Context context, ChatRoom chatRoom) {
        String replyLabel = "Reply";
        RemoteInput remoteInput = new RemoteInput.Builder(REPLY_ACTION_RESULT_KEY)
                .setLabel(replyLabel)
                .build();
        return new NotificationCompat.Action.Builder(
                R.drawable.logo,
                "Reply",
                getReplyPendingIntent(context, chatRoom))
                .addRemoteInput(remoteInput)
                .build();
    }


    private static PendingIntent getReplyPendingIntent(Context context, ChatRoom chatRoom) {
        int notificationId = chatRoom.getChatId().hashCode();
        Log.d("reply", " create " + notificationId);

        Intent intent = new Intent(context, NotificationReplyService.class);
        intent.putExtra(CHAT_ID_KEY, chatRoom.getChatId());
        intent.putExtra(NOTIFICATION_ID_KEY, notificationId);
        intent.putExtra(RECEIVER_EMAIL_KEY, chatRoom.getEmail());

        return PendingIntent.getService(context, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationCompat.Style buildMessagingStyle(List<Message> messages) {
        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle(messages.get(0).getUserEmail());

        for (Message message : messages) {
            messagingStyle.addMessage(
                    message.getMessage(),
                    message.getTime(),
                    message.getUserEmail());
        }
        return messagingStyle;
    }

    public static void removeNotification(int notificationId) {
        mManager.cancel(notificationId);

    }
}
