package com.ink_steel.inksteel.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.services.NotificationReplyService;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PRIVATE;

public class NotificationUtil {

    public static final String CHAT_ID = "chatId";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String OTHER_USER_EMAIL = "otherUserEmail";
    public static final String MESSAGE = "message";
    private static NotificationManager mManager;

    public static void showNotification(Context context, ChatRoom chatRoom, int notificationId) {
        if (mManager == null)
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = context.getString(R.string.channel_id);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.new_message)+
                                chatRoom.getLastMessageSender())
                        .setContentText(chatRoom.getLastMessage())
                        .addAction(createMessageAction(context, chatRoom, notificationId))
                        .setSmallIcon(R.drawable.notification_icon)
                        .setColorized(true)
                        .setContentIntent(
                                PendingIntent.getActivity(context, 55,
                                new Intent(context, HomeActivity.class),
                                PendingIntent.FLAG_NO_CREATE))
                        .setVisibility(VISIBILITY_PRIVATE)
                        .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                        .setStyle(buildNotificationStyle(chatRoom));
        mManager.notify(notificationId, mBuilder.build());
    }

    public static void removeNotification(int notificationId) {
        mManager.cancel(notificationId);
    }

    private static NotificationCompat.Action createMessageAction(Context context,
                                                                 ChatRoom chatRoom,
                                                                 int notificationId) {

        String replyLabel = context.getString(R.string.reply_hint);
        RemoteInput remoteInput = new RemoteInput.Builder(MESSAGE)
                .setLabel(replyLabel).build();
        return new NotificationCompat.Action.Builder(R.drawable.logo,
                context.getString(R.string.reply),
                getSendMessageIntent(context, chatRoom, notificationId))
                .addRemoteInput(remoteInput).build();
    }

    private static PendingIntent getSendMessageIntent(Context context, ChatRoom chatRoom,
                                                      int notificationId) {
        Intent intent = new Intent(context, NotificationReplyService.class);
        intent.putExtra(CHAT_ID, chatRoom.getChatId());
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra(OTHER_USER_EMAIL, chatRoom.getEmail());
        return PendingIntent.getService(context, notificationId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationCompat.MessagingStyle buildNotificationStyle(ChatRoom chatRoom) {
        return new NotificationCompat
                .MessagingStyle(chatRoom.getUserName())
                .addMessage(
                        chatRoom.getLastMessage(),
                        chatRoom.getLastMessageTime(),
                        chatRoom.getLastMessageSender());
    }

}
