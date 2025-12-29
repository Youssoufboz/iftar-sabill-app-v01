package com.ramadan.sabil23;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL_ID = "iftar_notification_channel";
    private static final String CHANNEL_NAME = "Iftar Notifications";
    private static final String PREF_NAME = "iftar_notification_prefs";

    // Prayer time notification types
    public static final String NOTIFICATION_TYPE_IFTAR = "iftar";
    public static final String NOTIFICATION_TYPE_SUHOOR = "suhoor";
    public static final String NOTIFICATION_TYPE_PRAYER = "prayer";

    // Notification timing constants
    private static final int DEFAULT_REMINDER_MINUTES = 15;

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationType = intent.getStringExtra("notification_type");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        Log.d(TAG, "Received notification request: " + notificationType);

        // Create and show the notification
        showNotification(context, title, message, notificationType);
    }

    /**
     * Shows a notification with the given title and message
     */
    private void showNotification(Context context, String title, String message, String notificationType) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        createNotificationChannel(context, notificationManager);

        // Intent to open the app when notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from_notification", true);
        intent.putExtra("notification_type", notificationType);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Get notification sound based on type
        Uri soundUri = getNotificationSound(context, notificationType);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(soundUri);

        // Show the notification
        int notificationId = getNotificationId(notificationType);
        notificationManager.notify(notificationId, builder.build());

        Log.d(TAG, "Notification shown: " + title);
    }

    /**
     * Creates a notification channel for Android O and above
     */
    private void createNotificationChannel(Context context, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);

            // Configure channel settings
            channel.setDescription("Notifications for Iftar and prayer times");
            channel.enableVibration(true);

            // Set custom sound if available
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Returns a unique notification ID based on the notification type
     */
    private int getNotificationId(String notificationType) {
        switch (notificationType) {
            case NOTIFICATION_TYPE_IFTAR:
                return 1001;
            case NOTIFICATION_TYPE_SUHOOR:
                return 1002;
            case NOTIFICATION_TYPE_PRAYER:
                return 1003;
            default:
                return (int) System.currentTimeMillis();
        }
    }

    /**
     * Returns the notification sound URI based on user preferences
     */
    private Uri getNotificationSound(Context context, String notificationType) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String soundPath = prefs.getString("sound_" + notificationType, "");

        if (soundPath != null && !soundPath.isEmpty()) {
            return Uri.parse(soundPath);
        }

        // Default notification sound
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    /**
     * Schedules an Iftar notification based on the provided time
     */
    public static void scheduleIftarNotification(Context context, Calendar iftarTime, double latitude, double longitude) {
        // Get user preference for reminder time (minutes before Iftar)
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int reminderMinutes = prefs.getInt("reminder_minutes", DEFAULT_REMINDER_MINUTES);

        // Calculate notification time (Iftar time - reminder minutes)
        Calendar notificationTime = (Calendar) iftarTime.clone();
        notificationTime.add(Calendar.MINUTE, -reminderMinutes);

        // Only schedule if notification time is in the future
        if (notificationTime.after(Calendar.getInstance())) {
            // Format times for display
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String iftarTimeStr = timeFormat.format(iftarTime.getTime());

            // Create intent for the notification
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notification_type", NOTIFICATION_TYPE_IFTAR);
            intent.putExtra("title", "Iftar Time Approaching");
            intent.putExtra("message", "Iftar time is at " + iftarTimeStr + ". May Allah accept your fast.");

            // Create unique request code based on day of year
            int requestCode = iftarTime.get(Calendar.DAY_OF_YEAR) * 100 + 1;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Get alarm manager and schedule the notification
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime.getTimeInMillis(),
                            pendingIntent);
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime.getTimeInMillis(),
                            pendingIntent);
                }

                Log.d(TAG, "Scheduled Iftar notification for " + timeFormat.format(notificationTime.getTime()) +
                        " (Iftar at " + iftarTimeStr + ")");
            }
        }
    }

    /**
     * Schedules a prayer time notification
     */
    public static void schedulePrayerNotification(Context context, String prayerName, Calendar prayerTime) {
        // Get user preference for reminder time
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int reminderMinutes = prefs.getInt("prayer_reminder_minutes", DEFAULT_REMINDER_MINUTES);
        boolean prayerNotificationsEnabled = prefs.getBoolean("prayer_notifications_enabled", true);

        // Only proceed if prayer notifications are enabled
        if (!prayerNotificationsEnabled) {
            return;
        }

        // Calculate notification time
        Calendar notificationTime = (Calendar) prayerTime.clone();
        notificationTime.add(Calendar.MINUTE, -reminderMinutes);

        // Only schedule if notification time is in the future
        if (notificationTime.after(Calendar.getInstance())) {
            // Format time for display
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String prayerTimeStr = timeFormat.format(prayerTime.getTime());

            // Create intent for the notification
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notification_type", NOTIFICATION_TYPE_PRAYER);
            intent.putExtra("title", prayerName + " Prayer Time");
            intent.putExtra("message", prayerName + " prayer time is at " + prayerTimeStr);

            // Create unique request code based on prayer name and day of year
            int requestCode = prayerTime.get(Calendar.DAY_OF_YEAR) * 100 + prayerName.hashCode() % 100;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Schedule the notification
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime.getTimeInMillis(),
                            pendingIntent);
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime.getTimeInMillis(),
                            pendingIntent);
                }

                Log.d(TAG, "Scheduled " + prayerName + " prayer notification for " +
                        timeFormat.format(notificationTime.getTime()));
            }
        }
    }

    /**
     * Cancels all scheduled notifications
     */
    public static void cancelAllNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);

        // Cancel Iftar notifications
        PendingIntent iftarPendingIntent = PendingIntent.getBroadcast(
                context, 1001, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (iftarPendingIntent != null && alarmManager != null) {
            alarmManager.cancel(iftarPendingIntent);
            iftarPendingIntent.cancel();
        }

        // Cancel Suhoor notifications
        PendingIntent suhoorPendingIntent = PendingIntent.getBroadcast(
                context, 1002, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (suhoorPendingIntent != null && alarmManager != null) {
            alarmManager.cancel(suhoorPendingIntent);
            suhoorPendingIntent.cancel();
        }

        // Cancel Prayer notifications
        PendingIntent prayerPendingIntent = PendingIntent.getBroadcast(
                context, 1003, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (prayerPendingIntent != null && alarmManager != null) {
            alarmManager.cancel(prayerPendingIntent);
            prayerPendingIntent.cancel();
        }

        Log.d(TAG, "Cancelled all scheduled notifications");
    }
}

