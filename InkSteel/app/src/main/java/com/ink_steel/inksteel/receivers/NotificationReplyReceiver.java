package com.ink_steel.inksteel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ink_steel.inksteel.helpers.NotificationUtil;

import static com.ink_steel.inksteel.helpers.NotificationUtil.NOTIFICATION_ID;

public class NotificationReplyReceiver extends BroadcastReceiver {
    public static final String ACTION_REPLY = "com.ink_steel.inksteel.DIRECT_REPLY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.hasExtra(NOTIFICATION_ID)) {
            NotificationUtil.removeNotification(intent.getIntExtra(NOTIFICATION_ID, 0));
        }
    }
}
