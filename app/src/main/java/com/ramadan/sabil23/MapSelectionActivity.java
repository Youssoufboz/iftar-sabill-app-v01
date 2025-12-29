package com.ramadan.sabil23;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapSelectionActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapSelectionActivity";
    private static final float DEFAULT_ZOOM = 15f;

    // UI elements
    private GoogleMap mMap;
    private Button confirmButton;
    private TextView instructionsText;
    private ImageButton backButton;

    // Selected location
    private LatLng selectedLocation;
    private Marker locationMarker;

    // Initial location (if provided)
    private LatLng initialLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);

        Log.d(TAG, "onCreate: MapSelectionActivity started");

        // Initialize UI elements
        confirmButton = findViewById(R.id.confirmButton);
        instructionsText = findViewById(R.id.instructionsText);
        backButton = findViewById(R.id.backButton);

        // Get initial location from intent if available
        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            double latitude = getIntent().getDoubleExtra("latitude", 0);
            double longitude = getIntent().getDoubleExtra("longitude", 0);
            initialLocation = new LatLng(latitude, longitude);
            Log.d(TAG, "Initial location set: " + latitude + ", " + longitude);
        }

        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Sets up click listeners for UI elements
     */
    private void setupClickListeners() {
        // Back button click
        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked");
            finish();
        });

        // Confirm button click
        confirmButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                Log.d(TAG, "Confirm button clicked with location: " +
                        selectedLocation.latitude + ", " + selectedLocation.longitude);

                // Return selected location to calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedLocation.latitude);
                resultIntent.putExtra("longitude", selectedLocation.longitude);
                setResult(RESULT_OK, resultIntent);

                Log.d(TAG, "Setting result and finishing activity");
                finish();
            } else {
                Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady called");

        // Configure map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Move camera to initial location if available
        if (initialLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, DEFAULT_ZOOM));
            // Add marker at initial location
            addMarkerAtLocation(initialLocation);
            Log.d(TAG, "Added marker at initial location");
        }

        // Set up map click listener
        mMap.setOnMapClickListener(latLng -> {
            Log.d(TAG, "Map clicked at: " + latLng.latitude + ", " + latLng.longitude);
            addMarkerAtLocation(latLng);
        });

        // Set up marker drag listener
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                // Not needed
            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                // Not needed
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                selectedLocation = marker.getPosition();
                Log.d(TAG, "Marker dragged to: " + selectedLocation.latitude + ", " + selectedLocation.longitude);
                confirmButton.setEnabled(true);
                instructionsText.setText("Location selected. Tap confirm to continue.");
            }
        });
    }

    /**
     * Adds a marker at the specified location
     */
    private void addMarkerAtLocation(LatLng location) {
        // Remove existing marker if any
        if (locationMarker != null) {
            locationMarker.remove();
        }

        // Add new marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .draggable(true)
                .title("Selected Location");

        locationMarker = mMap.addMarker(markerOptions);
        selectedLocation = location;

        // Enable confirm button
        confirmButton.setEnabled(true);
        instructionsText.setText("Location selected. Drag the pin to adjust or tap confirm to continue.");
    }
}

