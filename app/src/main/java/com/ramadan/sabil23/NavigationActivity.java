package com.ramadan.sabil23;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "NavigationActivity";
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ROUTE_PADDING = 100; // Padding for route bounds in pixels

    // UI elements
    private GoogleMap mMap;
    private FloatingActionButton backButton;
    private FloatingActionButton directionButton;
    private Button confirmButton;
    private TextView placeNameText;
    private TextView placeAddressText;

    // Location data
    private LatLng originLocation;
    private LatLng destinationLocation;
    private String destinationName;
    private String destinationAddress;

    // Navigation data
    private Polyline routePolyline;
    private Marker originMarker;
    private Marker destinationMarker;
    private List<NavigationManager.Route> routes;
    private NavigationManager navigationManager;

    // Track if route is currently displayed
    private boolean isRouteDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Initialize UI elements
        backButton = findViewById(R.id.backButton);
        directionButton = findViewById(R.id.directionButton);
        confirmButton = findViewById(R.id.confirmButton);
        placeNameText = findViewById(R.id.placeNameText);
        placeAddressText = findViewById(R.id.placeAddressText);

        // Get location data from intent
        if (getIntent().hasExtra("origin_lat") && getIntent().hasExtra("origin_lng") &&
                getIntent().hasExtra("destination_lat") && getIntent().hasExtra("destination_lng")) {

            double originLat = getIntent().getDoubleExtra("origin_lat", 0);
            double originLng = getIntent().getDoubleExtra("origin_lng", 0);
            double destLat = getIntent().getDoubleExtra("destination_lat", 0);
            double destLng = getIntent().getDoubleExtra("destination_lng", 0);

            originLocation = new LatLng(originLat, originLng);
            destinationLocation = new LatLng(destLat, destLng);
            destinationName = getIntent().getStringExtra("destination_name");
            destinationAddress = getIntent().getStringExtra("destination_address");

            // Set destination name and address
            if (destinationName != null && !destinationName.isEmpty()) {
                placeNameText.setText(destinationName);
            } else {
                placeNameText.setText("Selected Location");
            }

            if (destinationAddress != null && !destinationAddress.isEmpty()) {
                placeAddressText.setText(destinationAddress);
            } else {
                placeAddressText.setText(String.format("%.6f, %.6f", destLat, destLng));
            }

            // Initialize navigation manager
            navigationManager = NavigationManager.getInstance(this);
        } else {
            Toast.makeText(this, "Missing location data", Toast.LENGTH_SHORT).show();
            finish();
            return;
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
    // Add this method to your NavigationActivity class
    private void launchGoogleMapsNavigation() {
        // Get the destination name
        String destinationName = placeNameText.getText().toString();

        // First save the ride to history
        boolean saved = RideManager.saveRideToHistory(this, destinationName);

        if (saved) {
            Log.d("NavigationActivity", "Ride saved to history: " + destinationName);
        } else {
            Log.e("NavigationActivity", "Failed to save ride to history");
        }

        // Then launch navigation as you normally would
        // Your existing navigation code here...

        // Example:
        String destination = placeAddressText.getText().toString();
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(destination));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    // Make sure this method is called when your confirm/navigate button is clicked
//
    /**
     * Sets up click listeners for UI elements
     */
    private void setupClickListeners() {
        // Back button click
        backButton.setOnClickListener(v -> finish());

        // Direction button click - toggle route display
        directionButton.setOnClickListener(v -> {
            if (isRouteDisplayed) {
                // If route is displayed, hide it
                hideRoute();
            } else {
                // If route is not displayed, show it
                showRoute();
            }
        });

        // Confirm button click
        confirmButton.setOnClickListener(this::launchGoogleMapsNavigation);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configure map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Add markers for destination
        addDestinationMarker();

        // Center map on destination
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, DEFAULT_ZOOM));
    }

    /**
     * Adds marker for destination with custom pink circle icon
     */
    private void addDestinationMarker() {
        // Clear existing marker if any
        if (destinationMarker != null) destinationMarker.remove();

        // Create custom pink marker
        BitmapDescriptor pinkCircleIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);

        // Add destination marker
        MarkerOptions destOptions = new MarkerOptions()
                .position(destinationLocation)
                .title(destinationName != null ? destinationName : "Destination")
                .icon(pinkCircleIcon);
        destinationMarker = mMap.addMarker(destOptions);
    }

    /**
     * Shows the route between origin and destination
     */
    private void showRoute() {
        Toast.makeText(this, "Loading directions...", Toast.LENGTH_SHORT).show();

        // Get directions
        navigationManager.getDirections(
                originLocation,
                destinationLocation,
                NavigationManager.MODE_DRIVING,
                false,
                null,
                null,
                null,
                new NavigationManager.RouteSearchCallback() {
                    @Override
                    public void onRoutesFound(List<NavigationManager.Route> routeList) {
                        runOnUiThread(() -> {
                            routes = routeList;
                            if (!routes.isEmpty()) {
                                drawRoute(routes.get(0));
                                isRouteDisplayed = true;

                                // Add origin marker now that we're showing the route
                                if (originMarker != null) originMarker.remove();
                                MarkerOptions originOptions = new MarkerOptions()
                                        .position(originLocation)
                                        .title("Start");
                                originMarker = mMap.addMarker(originOptions);

                                // Zoom to show the entire route
                                zoomToShowRoute();
                            }
                        });
                    }

                    @Override
                    public void onSearchFailed(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(NavigationActivity.this,
                                    "Error getting directions: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    /**
     * Hides the route
     */
    private void hideRoute() {
        if (routePolyline != null) {
            routePolyline.remove();
            routePolyline = null;
        }

        // Remove origin marker
        if (originMarker != null) {
            originMarker.remove();
            originMarker = null;
        }

        isRouteDisplayed = false;

        // Zoom back to destination
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, DEFAULT_ZOOM));
    }

    /**
     * Draws the route on the map
     */
    private void drawRoute(NavigationManager.Route route) {
        // Remove existing polyline if any
        if (routePolyline != null) {
            routePolyline.remove();
        }

        // Decode polyline and draw route
        List<LatLng> points = NavigationManager.decodePolyline(route.getPolyline());
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(points)
                .width(12)
                .color(getResources().getColor(android.R.color.holo_purple))
                .geodesic(true);

        routePolyline = mMap.addPolyline(polylineOptions);
    }

    /**
     * Zooms the map to show the entire route
     */
    private void zoomToShowRoute() {
        if (originLocation != null && destinationLocation != null) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(originLocation);
            boundsBuilder.include(destinationLocation);
            LatLngBounds bounds = boundsBuilder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, ROUTE_PADDING));
        }
    }

    /**
     * Launches Google Maps for turn-by-turn navigation
     */
    public void launchGoogleMapsNavigation(View view) {
        navigationManager.launchGoogleMapsNavigation(
                this,
                originLocation,
                destinationLocation,
                NavigationManager.MODE_DRIVING);
    }
}