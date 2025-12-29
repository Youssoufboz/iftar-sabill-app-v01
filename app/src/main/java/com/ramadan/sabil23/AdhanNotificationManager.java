package com.ramadan.sabil23;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ramadan.sabil23.location.LocationManager;

import java.util.Calendar;
import java.util.Date;

public class AdhanNotificationManager {
    private static final String TAG = "AdhanNotificationManager";

    // Notification channel IDs
    public static final String CHANNEL_ADHAN = "adhan_notifications";
    public static final String CHANNEL_RESTAURANT = "restaurant_notifications";

    // Notification IDs
    public static final int NOTIFICATION_ID_FAJR = 1001;
    public static final int NOTIFICATION_ID_DHUHR = 1002;
    public static final int NOTIFICATION_ID_ASR = 1003;
    public static final int NOTIFICATION_ID_MAGHRIB = 1004;
    public static final int NOTIFICATION_ID_ISHA = 1005;
    public static final int NOTIFICATION_ID_RESTAURANT = 2001;

    // Request codes for pending intents
    private static final int REQUEST_CODE_FAJR = 101;
    private static final int REQUEST_CODE_DHUHR = 102;
    private static final int REQUEST_CODE_ASR = 103;
    private static final int REQUEST_CODE_MAGHRIB = 104;
    private static final int REQUEST_CODE_ISHA = 105;
    private static final int REQUEST_CODE_RESTAURANT = 201;
    private static final int REQUEST_CODE_REFRESH = 999;

    // Shared preferences
    private static final String PREFS_NAME = "AdhanPrefs";

    /**
     * Create notification channels for Android 8.0+
     */
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);

            // Create Adhan notification channel
            NotificationChannel adhanChannel = new NotificationChannel(
                    CHANNEL_ADHAN,
                    "Prayer Time Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            adhanChannel.setDescription("Notifications for prayer times");

            // Create Restaurant notification channel
            NotificationChannel restaurantChannel = new NotificationChannel(
                    CHANNEL_RESTAURANT,
                    "Restaurant Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            restaurantChannel.setDescription("Notifications for nearby restaurants before Maghrib");

            notificationManager.createNotificationChannel(adhanChannel);
            notificationManager.createNotificationChannel(restaurantChannel);
        }
    }

    /**
     * Schedule notifications based on settings
     */
    public static void scheduleNotifications(Context context) {
        // Create notification channels
        createNotificationChannels(context);

        // Cancel existing notifications
        cancelAllNotifications(context);

        // Get preferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);

        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications are disabled");
            return;
        }

        // Get notification settings
        boolean notifyFajr = prefs.getBoolean("notify_fajr", true);
        boolean notifyDhuhr = prefs.getBoolean("notify_dhuhr", true);
        boolean notifyAsr = prefs.getBoolean("notify_asr", true);
        boolean notifyMaghrib = prefs.getBoolean("notify_maghrib", true);
        boolean notifyIsha = prefs.getBoolean("notify_isha", true);
        boolean notifyRestaurants = prefs.getBoolean("restaurant_notifications_enabled", true);

        int notificationMinutesBefore = prefs.getInt("notification_timing", 15);
        int restaurantRadiusKm = prefs.getInt("restaurant_radius", 1);

        // Get current location
        LocationManager locationManager = new LocationManager(context);
        locationManager.getLastLocation(new LocationManager.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(Location location) {
                // Get prayer times for today
                Calendar today = Calendar.getInstance();
                PrayerTimesCalculator calculator = PrayerTimesCalculator.fromPreferences(
                        context, location.getLatitude(), location.getLongitude());

                Calendar[] prayerTimes = calculator.getPrayerTimes(today);

                // Schedule notifications for each prayer if enabled
                if (notifyFajr) {
                    schedulePrayerNotification(context, "Fajr", prayerTimes[0],
                            notificationMinutesBefore, NOTIFICATION_ID_FAJR, REQUEST_CODE_FAJR);
                }

                if (notifyDhuhr) {
                    schedulePrayerNotification(context, "Dhuhr", prayerTimes[2],
                            notificationMinutesBefore, NOTIFICATION_ID_DHUHR, REQUEST_CODE_DHUHR);
                }

                if (notifyAsr) {
                    schedulePrayerNotification(context, "Asr", prayerTimes[3],
                            notificationMinutesBefore, NOTIFICATION_ID_ASR, REQUEST_CODE_ASR);
                }

                if (notifyMaghrib) {
                    schedulePrayerNotification(context, "Maghrib", prayerTimes[4],
                            notificationMinutesBefore, NOTIFICATION_ID_MAGHRIB, REQUEST_CODE_MAGHRIB);

                    // Schedule restaurant notification before Maghrib if enabled
                    if (notifyRestaurants) {
                        scheduleRestaurantNotification(context, prayerTimes[4],
                                notificationMinutesBefore, restaurantRadiusKm, location);
                    }
                }

                if (notifyIsha) {
                    schedulePrayerNotification(context, "Isha", prayerTimes[5],
                            notificationMinutesBefore, NOTIFICATION_ID_ISHA, REQUEST_CODE_ISHA);
                }

                // Schedule for tomorrow as well
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_MONTH, 1);
                tomorrow.set(Calendar.HOUR_OF_DAY, 0);
                tomorrow.set(Calendar.MINUTE, 0);
                tomorrow.set(Calendar.SECOND, 0);

                // Schedule a refresh for midnight to set up tomorrow's notifications
                Intent refreshIntent = new Intent(context, NotificationRefreshReceiver.class);
                PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                        context, REQUEST_CODE_REFRESH, refreshIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                tomorrow.getTimeInMillis(),
                                refreshPendingIntent);
                    } else {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                tomorrow.getTimeInMillis(),
                                refreshPendingIntent);
                    }

                    Log.d(TAG, "Scheduled notification refresh for midnight");
                }
            }

            @Override
            public void onLocationError(String error) {
                Log.e(TAG, "Failed to get location for scheduling notifications: " + error);
            }
        });
    }

    /**
     * Schedule a notification for a specific prayer time
     */
    private static void schedulePrayerNotification(Context context, String prayerName,
                                                   Calendar prayerTime, int minutesBefore, int notificationId, int requestCode) {

        if (prayerTime == null) {
            Log.e(TAG, "Prayer time is null for " + prayerName);
            return;
        }

        // Calculate notification time (prayer time - minutes before)
        Calendar notificationTime = (Calendar) prayerTime.clone();
        notificationTime.add(Calendar.MINUTE, -minutesBefore);

        // Skip if the time has already passed
        if (notificationTime.getTimeInMillis() <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping " + prayerName + " notification as time has passed");
            return;
        }

        // Create intent for notification
        Intent intent = new Intent(context, AdhanNotificationReceiver.class);
        intent.putExtra("prayer_name", prayerName);
        intent.putExtra("notification_id", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule alarm
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

            Log.d(TAG, "Scheduled " + prayerName + " notification for " +
                    notificationTime.getTime() + " (" + minutesBefore + " minutes before)");
        }
    }

    /**
     * Schedule a notification for nearby restaurants before Maghrib
     */
    private static void scheduleRestaurantNotification(Context context, Calendar maghribTime,
                                                       int minutesBefore, int radiusKm, Location location) {

        if (maghribTime == null) {
            Log.e(TAG, "Maghrib time is null");
            return;
        }

        // Calculate notification time (maghrib time - minutes before)
        Calendar notificationTime = (Calendar) maghribTime.clone();
        notificationTime.add(Calendar.MINUTE, -minutesBefore);

        // Skip if the time has already passed
        if (notificationTime.getTimeInMillis() <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping restaurant notification as time has passed");
            return;
        }

        // Create intent for notification
        Intent intent = new Intent(context, RestaurantNotificationReceiver.class);
        intent.putExtra("radius_km", radiusKm);
        intent.putExtra("notification_id", NOTIFICATION_ID_RESTAURANT);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RESTAURANT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule alarm
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

            Log.d(TAG, "Scheduled restaurant notification for " + notificationTime.getTime() +
                    " (" + minutesBefore + " minutes before Maghrib)");
        }
    }

    /**
     * Cancel all scheduled notifications
     */
    public static void cancelAllNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // Cancel prayer notifications
        cancelNotification(context, REQUEST_CODE_FAJR);
        cancelNotification(context, REQUEST_CODE_DHUHR);
        cancelNotification(context, REQUEST_CODE_ASR);
        cancelNotification(context, REQUEST_CODE_MAGHRIB);
        cancelNotification(context, REQUEST_CODE_ISHA);

        // Cancel restaurant notification
        cancelNotification(context, REQUEST_CODE_RESTAURANT);

        // Cancel refresh notification
        cancelNotification(context, REQUEST_CODE_REFRESH);

        Log.d(TAG, "Cancelled all notifications");
    }

    /**
     * Cancel a specific notification
     */
    private static void cancelNotification(Context context, int requestCode) {
        Intent intent = new Intent(context, AdhanNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}