package com.ramadan.sabil23;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationRefreshReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationRefresh";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Refreshing notifications for the new day");
        AdhanNotificationManager.scheduleNotifications(context);
    }
}