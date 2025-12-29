package com.ramadan.sabil23;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "ride_history.db";
    private static final int DATABASE_VERSION = 2; // Increased version to force database recreation

    // Table name
    private static final String TABLE_RIDES = "rides";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_TIME = "time";
    private static final String KEY_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DatabaseHelper constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called - creating database tables");
        String CREATE_RIDES_TABLE = "CREATE TABLE " + TABLE_RIDES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DESTINATION + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_TIMESTAMP + " INTEGER"
                + ")";
        db.execSQL(CREATE_RIDES_TABLE);
        Log.d(TAG, "Table created: " + TABLE_RIDES);

        // Add some sample data for testing
        addSampleData(db);
    }

    private void addSampleData(SQLiteDatabase db) {
        Log.d(TAG, "Adding sample data to database");
        // Current time
        long now = System.currentTimeMillis();

        // Sample destinations
        String[] destinations = {
                "Home", "Work", "Shopping Mall", "Airport", "Restaurant"
        };

        // Add sample rides with different timestamps
        for (int i = 0; i < destinations.length; i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_DESTINATION, destinations[i]);

            // Create a timestamp between 1 day and 30 days ago
            long timestamp = now - ((1 + i * 6) * 24 * 60 * 60 * 1000L);

            // Format the time
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(new Date(timestamp));

            values.put(KEY_TIME, formattedTime);
            values.put(KEY_TIMESTAMP, timestamp);

            long id = db.insert(TABLE_RIDES, null, values);
            Log.d(TAG, "Added sample ride: " + destinations[i] + " with ID: " + id);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade called from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        onCreate(db);
    }

    // Save a ride to history - this is the main method to save rides
    public long saveRide(String destination, String dateTime) {
        Log.d(TAG, "saveRide called with destination: " + destination);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESTINATION, destination);
        values.put(KEY_TIME, dateTime);
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_RIDES, null, values);
        Log.d(TAG, "Ride saved with ID: " + newRowId);
        db.close();
        return newRowId;
    }

    // Get all rides
    public List<RideHistoryActivity.RideHistoryItem> getAllRides() {
        List<RideHistoryActivity.RideHistoryItem> rideList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RIDES + " ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d(TAG, "getAllRides found " + cursor.getCount() + " rides");

        if (cursor.moveToFirst()) {
            do {
                int destIndex = cursor.getColumnIndex(KEY_DESTINATION);
                int timeIndex = cursor.getColumnIndex(KEY_TIME);

                if (destIndex != -1 && timeIndex != -1) {
                    String destination = cursor.getString(destIndex);
                    String time = cursor.getString(timeIndex);

                    RideHistoryActivity.RideHistoryItem ride = new RideHistoryActivity.RideHistoryItem(destination, time);
                    rideList.add(ride);
                    Log.d(TAG, "Retrieved ride: " + destination + " at " + time);
                } else {
                    Log.e(TAG, "Column index not found: destIndex=" + destIndex + ", timeIndex=" + timeIndex);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rideList;
    }

    // Get rides from last week
    public List<RideHistoryActivity.RideHistoryItem> getRidesFromLastWeek() {
        List<RideHistoryActivity.RideHistoryItem> rideList = new ArrayList<>();

        // Calculate timestamp for 7 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long weekAgo = cal.getTimeInMillis();

        String selectQuery = "SELECT * FROM " + TABLE_RIDES +
                " WHERE " + KEY_TIMESTAMP + " >= " + weekAgo +
                " ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d(TAG, "getRidesFromLastWeek found " + cursor.getCount() + " rides");

        if (cursor.moveToFirst()) {
            do {
                int destIndex = cursor.getColumnIndex(KEY_DESTINATION);
                int timeIndex = cursor.getColumnIndex(KEY_TIME);

                if (destIndex != -1 && timeIndex != -1) {
                    String destination = cursor.getString(destIndex);
                    String time = cursor.getString(timeIndex);

                    RideHistoryActivity.RideHistoryItem ride = new RideHistoryActivity.RideHistoryItem(destination, time);
                    rideList.add(ride);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rideList;
    }

    // Get rides from last month
    public List<RideHistoryActivity.RideHistoryItem> getRidesFromLastMonth() {
        List<RideHistoryActivity.RideHistoryItem> rideList = new ArrayList<>();

        // Calculate timestamp for 30 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        long monthAgo = cal.getTimeInMillis();

        String selectQuery = "SELECT * FROM " + TABLE_RIDES +
                " WHERE " + KEY_TIMESTAMP + " >= " + monthAgo +
                " ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d(TAG, "getRidesFromLastMonth found " + cursor.getCount() + " rides");

        if (cursor.moveToFirst()) {
            do {
                int destIndex = cursor.getColumnIndex(KEY_DESTINATION);
                int timeIndex = cursor.getColumnIndex(KEY_TIME);

                if (destIndex != -1 && timeIndex != -1) {
                    String destination = cursor.getString(destIndex);
                    String time = cursor.getString(timeIndex);

                    RideHistoryActivity.RideHistoryItem ride = new RideHistoryActivity.RideHistoryItem(destination, time);
                    rideList.add(ride);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rideList;
    }

    // Clear all ride history
    public void clearRideHistory() {
        Log.d(TAG, "clearRideHistory called");
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_RIDES, null, null);
        Log.d(TAG, "Deleted " + count + " rides from history");
        db.close();
    }

    // Debug method to log all rides
    public void logAllRides() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RIDES, null, null, null, null, null, null);

        Log.d(TAG, "=== DATABASE CONTENTS ===");
        Log.d(TAG, "Total rides in database: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int destIndex = cursor.getColumnIndex(KEY_DESTINATION);
                int timeIndex = cursor.getColumnIndex(KEY_TIME);

                if (idIndex != -1 && destIndex != -1 && timeIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String destination = cursor.getString(destIndex);
                    String time = cursor.getString(timeIndex);

                    Log.d(TAG, "Ride ID: " + id + ", Destination: " + destination + ", Time: " + time);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    // Add a test ride for debugging
    public long addTestRide() {
        String testDest = "Test Destination " + System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String dateTime = sdf.format(new Date());

        return saveRide(testDest, dateTime);
    }
}