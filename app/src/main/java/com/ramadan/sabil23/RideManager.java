package com.ramadan.sabil23;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to manage ride operations
 */
public class RideManager {

    /**
     * Save a completed ride to history
     * @param context Application context
     * @param destination The destination of the ride
     * @return true if saving was successful
     */
    public static boolean saveRideToHistory(Context context, String destination) {
        try {
            // Get current date and time in readable format
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault());
            String dateTime = sdf.format(new Date());

            // Save to database
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            long result = dbHelper.saveRide(destination, dateTime);

            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}