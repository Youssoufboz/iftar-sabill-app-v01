package com.ramadan.sabil23;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int DEFAULT_SEARCH_RADIUS = 5000; // 5km

    // UI elements
    private GoogleMap mMap;
    private EditText destinationInput;
    private TextView restaurantCountText;
    private FloatingActionButton refreshButton;
    private FloatingActionButton myLocationButton;
    private FrameLayout loadingIndicator;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    // Places
    private PlacesClient placesClient;

    // Managers
    private RestaurantManager restaurantManager;
    private NavigationManager navigationManager;
    private SearchManager searchManager;

    // Restaurant markers
    private Map<String, Marker> restaurantMarkers = new HashMap<>();
    private List<RestaurantManager.Restaurant> nearbyRestaurants = new ArrayList<>();

    // Prayer times
    private PrayerTimesCalculator prayerTimesCalculator;
    private Calendar iftarTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        destinationInput = findViewById(R.id.destinationInput);
        restaurantCountText = findViewById(R.id.restaurantCountText);
        refreshButton = findViewById(R.id.refreshButton);
        myLocationButton = findViewById(R.id.myLocationButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize managers
        restaurantManager = RestaurantManager.getInstance(this);
        navigationManager = NavigationManager.getInstance(this);
        searchManager = SearchManager.getInstance(this, placesClient);

        // Initialize notification channels and schedule notifications
        AdhanNotificationManager.createNotificationChannels(this);
        AdhanNotificationManager.scheduleNotifications(this);

        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up click listeners
        setupClickListeners();

        // Check for notification intent
        handleNotificationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationIntent(intent);
    }

    /**
     * Handles intents from notifications
     */
    private void handleNotificationIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("from_notification", false)) {
            String notificationType = intent.getStringExtra("notification_type");

            if (notificationType != null) {
                switch (notificationType) {
                    case NotificationReceiver.NOTIFICATION_TYPE_IFTAR:
                        // Show Iftar-specific restaurants
                        Toast.makeText(this, "Showing restaurants open for Iftar", Toast.LENGTH_LONG).show();
                        break;

                    case NotificationReceiver.NOTIFICATION_TYPE_PRAYER:
                        // Show nearby mosques
                        Toast.makeText(this, "Prayer time notification received", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    /**
     * Sets up click listeners for UI elements
     */
    private void setupClickListeners() {
        // Menu button click
        findViewById(R.id.menuButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SideMenuActivity.class);
            startActivity(intent);
        });

        // Destination input click
        destinationInput.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            if (currentLocation != null) {
                intent.putExtra("latitude", currentLocation.getLatitude());
                intent.putExtra("longitude", currentLocation.getLongitude());
            }
            // Use startActivityForResult instead of startActivity
            startActivityForResult(intent, 1002); // Use a unique request code
        });

        // Refresh button click
        refreshButton.setOnClickListener(v -> {
            if (currentLocation != null) {
                searchNearbyRestaurants();
            } else {
                Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                getLastLocation();
            }
        });

        // My location button click
        myLocationButton.setOnClickListener(v -> {
            if (currentLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                        DEFAULT_ZOOM));
            } else {
                Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                getLastLocation();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configure map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Set up info window adapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Use default window frame
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Get restaurant data from marker tag
                RestaurantManager.Restaurant restaurant = (RestaurantManager.Restaurant) marker.getTag();
                if (restaurant == null) return null;

                // Inflate custom info window layout
                View view = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.bottom_sheet_restaurant, null);

                // Set restaurant details
                TextView nameTextView = view.findViewById(R.id.restaurant_name);
                TextView statusTextView = view.findViewById(R.id.restaurant_status);
                TextView addressTextView = view.findViewById(R.id.restaurant_address);
                TextView ratingTextView = view.findViewById(R.id.restaurant_rating);

                nameTextView.setText(restaurant.getName());
                statusTextView.setText(restaurant.isOpenNow() ?
                        "Open Now" : "Closed");
                addressTextView.setText(restaurant.getAddress());

                if (restaurant.getRating() > 0) {
                    ratingTextView.setText(String.format("%.1f ★", restaurant.getRating()));
                    ratingTextView.setVisibility(View.VISIBLE);
                }

                return view;
            }
        });

        // Set marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            RestaurantManager.Restaurant restaurant = (RestaurantManager.Restaurant) marker.getTag();
            if (restaurant != null) {
                showRestaurantDetails(restaurant);
            }
            return false; // Allow default behavior (showing info window)
        });

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Gets the last known location
     */
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            loadingIndicator.setVisibility(View.VISIBLE);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        loadingIndicator.setVisibility(View.GONE);

                        if (location != null) {
                            currentLocation = location;

                            // Move camera to current location
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                            // Initialize prayer times calculator
                            initializePrayerTimes(location.getLatitude(), location.getLongitude());

                            // Search for nearby restaurants
                            searchNearbyRestaurants();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadingIndicator.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                                "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Initializes prayer times calculator and schedules notifications
     */
    private void initializePrayerTimes(double latitude, double longitude) {
        // Create prayer times calculator
        prayerTimesCalculator = PrayerTimesCalculator.fromPreferences(this, latitude, longitude);

        // Get today's Iftar time
        Calendar today = Calendar.getInstance();
        iftarTime = prayerTimesCalculator.getIftarTime(today);

        // Schedule Iftar notification
        NotificationReceiver.scheduleIftarNotification(this, iftarTime, latitude, longitude);

        // Get prayer times for today
        Calendar[] prayerTimes = prayerTimesCalculator.getPrayerTimes(today);

        // Schedule prayer notifications
        String[] prayerNames = {"Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"};
        for (int i = 0; i < prayerTimes.length; i++) {
            if (prayerTimes[i].after(today)) {
                NotificationReceiver.schedulePrayerNotification(this, prayerNames[i], prayerTimes[i]);
            }
        }
    }

    /**
     * Searches for nearby restaurants
     */
    private void searchNearbyRestaurants() {
        if (currentLocation == null) return;

        loadingIndicator.setVisibility(View.VISIBLE);
        restaurantCountText.setText("Searching for restaurants within 5km...");

        // Clear existing markers
        for (Marker marker : restaurantMarkers.values()) {
            marker.remove();
        }
        restaurantMarkers.clear();
        nearbyRestaurants.clear();

        // Get current location as LatLng
        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Search for restaurants open during Iftar time
        if (iftarTime != null && Calendar.getInstance().before(iftarTime)) {
            // If it's before Iftar time, search for restaurants open during Iftar
            restaurantManager.searchRestaurantsOpenDuringIftar(
                    location, iftarTime, DEFAULT_SEARCH_RADIUS,
                    RestaurantManager.TYPE_ALL, RestaurantManager.PRICE_ANY,
                    new RestaurantManager.RestaurantSearchCallback() {
                        @Override
                        public void onRestaurantsFound(List<RestaurantManager.Restaurant> restaurants) {
                            runOnUiThread(() -> {
                                loadingIndicator.setVisibility(View.GONE);
                                handleRestaurantSearchResults(restaurants);
                            });
                        }

                        @Override
                        public void onSearchFailed(String errorMessage) {
                            runOnUiThread(() -> {
                                loadingIndicator.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this,
                                        "Error searching restaurants: " + errorMessage, Toast.LENGTH_SHORT).show();
                                restaurantCountText.setText("Error searching restaurants");
                            });
                        }
                    });
        } else {
            // Otherwise, search for all open restaurants
            restaurantManager.searchNearbyRestaurants(
                    location, DEFAULT_SEARCH_RADIUS,
                    RestaurantManager.TYPE_ALL, RestaurantManager.PRICE_ANY, true,
                    new RestaurantManager.RestaurantSearchCallback() {
                        @Override
                        public void onRestaurantsFound(List<RestaurantManager.Restaurant> restaurants) {
                            runOnUiThread(() -> {
                                loadingIndicator.setVisibility(View.GONE);
                                handleRestaurantSearchResults(restaurants);
                            });
                        }

                        @Override
                        public void onSearchFailed(String errorMessage) {
                            runOnUiThread(() -> {
                                loadingIndicator.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this,
                                        "Error searching restaurants: " + errorMessage, Toast.LENGTH_SHORT).show();
                                restaurantCountText.setText("Error searching restaurants");
                            });
                        }
                    });
        }
    }

    /**
     * Handles restaurant search results
     */
    private void handleRestaurantSearchResults(List<RestaurantManager.Restaurant> restaurants) {
        nearbyRestaurants = restaurants;

        // Update restaurant count text
        if (restaurants.isEmpty()) {
            restaurantCountText.setText("No restaurants found nearby");
        } else {
            restaurantCountText.setText(restaurants.size() + " restaurants found nearby");
        }

        // Add markers for each restaurant
        for (RestaurantManager.Restaurant restaurant : restaurants) {
            // Create custom marker
            View markerView = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
            TextView nameTextView = markerView.findViewById(R.id.marker_name);
            TextView ratingTextView = markerView.findViewById(R.id.marker_rating);

            nameTextView.setText(restaurant.getName());

            if (restaurant.getRating() > 0) {
                ratingTextView.setText(String.format("%.1f", restaurant.getRating()));
            } else {
                ratingTextView.setVisibility(View.GONE);
            }

            // Convert view to bitmap
            Bitmap markerBitmap = createBitmapFromView(markerView);

            // Add marker to map
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(restaurant.getLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                    .anchor(0.5f, 1.0f);

            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(restaurant);

            restaurantMarkers.put(restaurant.getId(), marker);
        }
    }

    /**
     * Creates a bitmap from a view
     */
    private Bitmap createBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * Shows restaurant details in a bottom sheet
     */
    private void showRestaurantDetails(RestaurantManager.Restaurant restaurant) {
        // Get detailed information about the restaurant
        restaurantManager.getRestaurantDetails(restaurant.getId(),
                new RestaurantManager.RestaurantDetailsCallback() {
                    @Override
                    public void onRestaurantDetailsLoaded(RestaurantManager.Restaurant restaurant) {
                        runOnUiThread(() -> {
                            // Create and show bottom sheet dialog with restaurant details
                            RestaurantDetailsBottomSheet bottomSheet =
                                    new RestaurantDetailsBottomSheet(MainActivity.this, restaurant);
                            bottomSheet.show(getSupportFragmentManager(), "RestaurantDetails");
                        });
                    }

                    @Override
                    public void onDetailsFailed(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Error loading restaurant details: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        getLastLocation();
                    }
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Gets the current location
     * This method is used by RestaurantDetailsBottomSheet
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Handles the result from search activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // Get selected location data
            String placeId = data.getStringExtra("place_id");
            String placeName = data.getStringExtra("place_name");
            double latitude = data.getDoubleExtra("place_lat", 0);
            double longitude = data.getDoubleExtra("place_lng", 0);

            LatLng destination = new LatLng(latitude, longitude);

            // Check if we have current location
            if (currentLocation != null) {
                // Start navigation activity
                Intent navigationIntent = new Intent(this, NavigationActivity.class);
                navigationIntent.putExtra("origin_lat", currentLocation.getLatitude());
                navigationIntent.putExtra("origin_lng", currentLocation.getLongitude());
                navigationIntent.putExtra("destination_lat", destination.latitude);
                navigationIntent.putExtra("destination_lng", destination.longitude);
                navigationIntent.putExtra("destination_name", placeName);
                startActivity(navigationIntent);
            } else {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
                getLastLocation();
            }
        }
    }
}