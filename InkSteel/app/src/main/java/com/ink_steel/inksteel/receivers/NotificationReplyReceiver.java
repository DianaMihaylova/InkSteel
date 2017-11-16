package com.ink_steel.inksteel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ink_steel.inksteel.helpers.NotificationHelper;

import static com.ink_steel.inksteel.helpers.NotificationHelper.NOTIFICATION_ID_KEY;

public class NotificationReplyReceiver extends BroadcastReceiver {
    public static final String ACTION_REPLY = "com.ink_steel.inksteel.DIRECT_REPLY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.hasExtra(NOTIFICATION_ID_KEY)) {
            int intExtra = intent.getIntExtra(NOTIFICATION_ID_KEY, 0);
            NotificationHelper.removeNotification(intExtra);
        }
    }
}
