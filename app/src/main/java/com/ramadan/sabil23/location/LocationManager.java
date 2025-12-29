package com.ramadan.sabil23.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;

public class LocationManager {
    private static final String TAG = "LocationManager";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long UPDATE_INTERVAL = 10000; // 10 seconds
    private static final long FASTEST_INTERVAL = 5000; // 5 seconds

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationUpdateListener listener;

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
        void onLocationError(String error);
    }

    public LocationManager(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.listener = listener;
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();
    }

    public void startLocationUpdates() {
        if (!hasLocationPermission()) {
            if (listener != null) {
                listener.onLocationError("Location permission not granted");
            }
            return;
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null && listener != null) {
                        listener.onLocationUpdate(location);
                    }
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception: " + e.getMessage());
            if (listener != null) {
                listener.onLocationError("Security exception: " + e.getMessage());
            }
        }
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void getLastLocation(final LocationUpdateListener callback) {
        if (!hasLocationPermission()) {
            callback.onLocationError("Location permission not granted");
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            callback.onLocationUpdate(location);
                        } else {
                            callback.onLocationError("Last location is null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting last location", e);
                        callback.onLocationError("Error getting last location: " + e.getMessage());
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception: " + e.getMessage());
            callback.onLocationError("Security exception: " + e.getMessage());
        }
    }

    public static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}

