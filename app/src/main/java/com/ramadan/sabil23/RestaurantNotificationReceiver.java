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

import com.google.android.gms.maps.model.LatLng;
import com.ramadan.sabil23.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "RestaurantNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        int radiusKm = intent.getIntExtra("radius_km", 1);
        int notificationId = intent.getIntExtra("notification_id", 2001);
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        Log.d(TAG, "Received restaurant notification request");

        // Create dummy restaurant data (in a real app, you would fetch this from an API)
        List<Restaurant> restaurants = getDummyRestaurants(latitude, longitude);

        // Show notification with restaurant info
        showRestaurantNotification(context, restaurants, notificationId, latitude, longitude);
    }

    private void showRestaurantNotification(Context context, List<Restaurant> restaurants,
                                            int notificationId, double latitude, double longitude) {

        // Create an intent to open the map when notification is tapped
        Intent mapIntent = new Intent(context, RestaurantMapActivity.class);
        mapIntent.putExtra("latitude", latitude);
        mapIntent.putExtra("longitude", longitude);
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, mapIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create an intent to open settings
        Intent settingsIntent = new Intent(context, AdhanNotificationActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                context, 1, settingsIntent, PendingIntent.FLAG_IMMUTABLE);

        // Get notification sound
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create notification text
        String title = "Nearby Restaurants for Iftar";
        String content = restaurants.size() > 0
                ? restaurants.size() + " restaurants found nearby for Iftar"
                : "Find restaurants nearby before Maghrib prayer";

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AdhanNotificationManager.CHANNEL_RESTAURANT)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification, "Settings", settingsPendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Restaurant notification shown");
        } catch (SecurityException e) {
            // Handle missing notification permission
            Log.e(TAG, "No permission to show notification", e);
        }
    }

    // Create dummy restaurant data for demonstration
    private List<Restaurant> getDummyRestaurants(double latitude, double longitude) {
        List<Restaurant> restaurants = new ArrayList<>();

        // Add some dummy restaurants
        restaurants.add(createDummyRestaurant(
                "1", "Restaurant A", "123 Main Street", 4.5,
                latitude + 0.005, longitude + 0.002, true));

        restaurants.add(createDummyRestaurant(
                "2", "Restaurant B", "456 Oak Avenue", 4.2,
                latitude - 0.003, longitude + 0.004, true));

        restaurants.add(createDummyRestaurant(
                "3", "Restaurant C", "789 Pine Road", 3.8,
                latitude + 0.002, longitude - 0.003, true));

        return restaurants;
    }

    private Restaurant createDummyRestaurant(String id, String name, String vicinity,
                                             double rating, double lat, double lng, boolean isOpen) {

        LatLng position = new LatLng(lat, lng);
        return new Restaurant(id, name, vicinity, rating, "", isOpen, "$$$", position, id);
    }
}