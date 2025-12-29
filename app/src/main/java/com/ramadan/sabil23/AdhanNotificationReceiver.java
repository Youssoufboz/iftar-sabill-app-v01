package com.ramadan.sabil23;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AdhanNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "AdhanNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        int notificationId = intent.getIntExtra("notification_id", 1001);

        Log.d(TAG, "Received notification for " + prayerName);

        // Create an intent to open the settings when notification is tapped
        Intent settingsIntent = new Intent(context, AdhanNotificationActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, settingsIntent, PendingIntent.FLAG_IMMUTABLE);

        // Get notification sound
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AdhanNotificationManager.CHANNEL_ADHAN)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(prayerName + " Prayer Time")
                .setContentText(prayerName + " prayer time is approaching")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification shown for " + prayerName);
        } catch (SecurityException e) {
            // Handle missing notification permission
            Log.e(TAG, "No permission to show notification", e);
        }
    }
}