package com.ramadan.sabil23;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ramadan.sabil23.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "RestaurantMapActivity";

    private GoogleMap mMap;
    private double latitude;
    private double longitude;

    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView restaurantList;
    private RestaurantAdapter adapter;

    private List<Restaurant> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nearby Restaurants");

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        restaurantList = findViewById(R.id.restaurantList);

        // Set up RecyclerView
        restaurantList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this, restaurants, new RestaurantAdapter.OnRestaurantClickListener() {
            @Override
            public void onNavigateClick(Restaurant restaurant) {
                navigateToRestaurant(restaurant);
            }

            @Override
            public void onCallClick(Restaurant restaurant) {
                // This would be implemented if we had phone numbers
                Toast.makeText(RestaurantMapActivity.this, "Call feature not available", Toast.LENGTH_SHORT).show();
            }
        });
        restaurantList.setAdapter(adapter);

        // Get location from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Settings button
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(RestaurantMapActivity.this, AdhanNotificationActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the current location
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

        // Find nearby restaurants
        findNearbyRestaurants();
    }

    private void findNearbyRestaurants() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        // Create a dummy list of restaurants for demonstration
        // In a real app, you would use the Places API to find actual restaurants
        restaurants.clear();

        // Add some dummy restaurants
        restaurants.add(new Restaurant(
                "1",
                "Restaurant A",
                "123 Main Street",
                4.5,
                "",
                true,
                "$$$",
                new LatLng(latitude + 0.005, longitude + 0.002),
                "place_id_1"
        ));

        restaurants.add(new Restaurant(
                "2",
                "Restaurant B",
                "456 Oak Avenue",
                4.2,
                "",
                true,
                "$$",
                new LatLng(latitude - 0.003, longitude + 0.004),
                "place_id_2"
        ));

        restaurants.add(new Restaurant(
                "3",
                "Restaurant C",
                "789 Pine Road",
                3.8,
                "",
                false,
                "$",
                new LatLng(latitude + 0.002, longitude - 0.003),
                "place_id_3"
        ));

        // Add markers for each restaurant
        for (Restaurant restaurant : restaurants) {
            mMap.addMarker(new MarkerOptions()
                    .position(restaurant.getPosition())
                    .title(restaurant.getName()));
        }

        // Update the adapter
        adapter.notifyDataSetChanged();

        // Hide progress bar
        progressBar.setVisibility(View.GONE);

        // Show empty view if no restaurants found
        if (restaurants.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToRestaurant(Restaurant restaurant) {
        // Open Google Maps for navigation
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                restaurant.getPosition().latitude + "," + restaurant.getPosition().longitude +
                "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}