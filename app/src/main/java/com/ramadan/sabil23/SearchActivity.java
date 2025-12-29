package com.ramadan.sabil23;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int REQUEST_MAP_SELECTION = 1001;

    // UI elements
    private EditText searchInput;
    private ImageButton backButton;
    private ImageButton clearButton;
    private RecyclerView suggestionsRecyclerView;
    private TextView noResultsText;
    private LinearLayout currentPositionOption;
    private LinearLayout setOnMapOption;

    // Adapters
    private SearchSuggestionsAdapter suggestionsAdapter;

    // Location
    private LatLng currentLocation;

    // Services
    private GooglePlacesApiService placesApiService;
    private SearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI elements
        searchInput = findViewById(R.id.searchInput);
        backButton = findViewById(R.id.backButton);
        clearButton = findViewById(R.id.clearButton);
        suggestionsRecyclerView = findViewById(R.id.suggestionsRecyclerView);
        noResultsText = findViewById(R.id.noResultsText);
        currentPositionOption = findViewById(R.id.currentPositionOption);
        setOnMapOption = findViewById(R.id.setOnMapOption);

        // Get current location from intent
        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            double latitude = getIntent().getDoubleExtra("latitude", 0);
            double longitude = getIntent().getDoubleExtra("longitude", 0);
            currentLocation = new LatLng(latitude, longitude);
        }

        // Initialize Places API with proper error handling
        if (!Places.isInitialized()) {
            try {
                Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
                PlacesClient placesClient = Places.createClient(this);

                // Initialize services
                placesApiService = new GooglePlacesApiService(this);
                placesApiService.setPlacesClient(placesClient);

                searchManager = SearchManager.getInstance(this, placesClient);
            } catch (Exception e) {
                Toast.makeText(this, "Error initializing Places API: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Places API initialization error", e);
            }
        } else {
            PlacesClient placesClient = Places.createClient(this);

            // Initialize services
            placesApiService = new GooglePlacesApiService(this);
            placesApiService.setPlacesClient(placesClient);

            searchManager = SearchManager.getInstance(this, placesClient);
        }

        // Set up recycler view
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionsAdapter = new SearchSuggestionsAdapter(this, new ArrayList<>());
        suggestionsRecyclerView.setAdapter(suggestionsAdapter);

        // Set up click listeners
        setupClickListeners();

        // Set up text change listener
        setupTextChangeListener();

        // Show recent searches initially
        showRecentSearches();
    }

    /**
     * Sets up click listeners for UI elements
     */
    private void setupClickListeners() {
        // Back button click
        backButton.setOnClickListener(v -> finish());

        // Clear button click
        clearButton.setOnClickListener(v -> searchInput.setText(""));

        // Current position option click
        currentPositionOption.setOnClickListener(v -> {
            if (currentLocation != null) {
                // Return current location to calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("place_id", "current_location");
                resultIntent.putExtra("place_name", "Current Location");
                resultIntent.putExtra("place_address", "");
                resultIntent.putExtra("place_lat", currentLocation.latitude);
                resultIntent.putExtra("place_lng", currentLocation.longitude);

                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Set on map option click
        setOnMapOption.setOnClickListener(v -> {
            // Launch map selection activity
            Intent intent = new Intent(this, MapSelectionActivity.class);
            if (currentLocation != null) {
                intent.putExtra("latitude", currentLocation.latitude);
                intent.putExtra("longitude", currentLocation.longitude);
            }
            startActivityForResult(intent, REQUEST_MAP_SELECTION);
        });

        // Suggestion item click
        suggestionsAdapter.setOnItemClickListener(suggestion -> {
            // Get place details
            placesApiService.getPlaceDetails(suggestion.getPlaceId(),
                    new GooglePlacesApiService.PlaceDetailsCallback() {
                        @Override
                        public void onPlaceDetailsLoaded(GooglePlacesApiService.PlaceSuggestion place) {
                            // Add to recent searches
                            SearchManager.PlaceSuggestion searchSuggestion = new SearchManager.PlaceSuggestion(
                                    place.getPlaceId(),
                                    place.getPrimaryText(),
                                    place.getSecondaryText());
                            searchSuggestion.setLocation(place.getLocation());

                            searchManager.addToRecentSearches(searchSuggestion);

                            // Return to previous activity with selected place
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("place_id", place.getPlaceId());
                            resultIntent.putExtra("place_name", place.getPrimaryText());
                            resultIntent.putExtra("place_address", place.getSecondaryText());
                            resultIntent.putExtra("place_lat", place.getLocation().latitude);
                            resultIntent.putExtra("place_lng", place.getLocation().longitude);

                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }

                        @Override
                        public void onDetailsFailed(String errorMessage) {
                            runOnUiThread(() -> {
                                Toast.makeText(SearchActivity.this,
                                        "Error loading place details: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        });
    }

    /**
     * Sets up text change listener for search input
     */
    private void setupTextChangeListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                clearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Get autocomplete suggestions
                if (s.length() > 0) {
                    getAutocompleteSuggestions(s.toString());
                } else {
                    showRecentSearches();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    private void getAutocompleteSuggestions(String query) {
        try {
            if (placesApiService == null) {
                Toast.makeText(this, "Places API service not initialized", Toast.LENGTH_SHORT).show();
                return;
            }

            placesApiService.getAutocompleteSuggestions(query, currentLocation,
                    new GooglePlacesApiService.PlaceSearchCallback() {
                        @Override
                        public void onPlaceSuggestionsFound(List<GooglePlacesApiService.PlaceSuggestion> suggestions) {
                            runOnUiThread(() -> {
                                if (suggestions.isEmpty()) {
                                    suggestionsRecyclerView.setVisibility(View.GONE);
                                    noResultsText.setVisibility(View.VISIBLE);
                                } else {
                                    suggestionsRecyclerView.setVisibility(View.VISIBLE);
                                    noResultsText.setVisibility(View.GONE);

                                    // Convert to adapter items
                                    List<SearchSuggestionsAdapter.SuggestionItem> items = new ArrayList<>();
                                    for (GooglePlacesApiService.PlaceSuggestion suggestion : suggestions) {
                                        items.add(new SearchSuggestionsAdapter.SuggestionItem(
                                                suggestion.getPlaceId(),
                                                suggestion.getPrimaryText(),
                                                suggestion.getSecondaryText(),
                                                false));
                                    }

                                    suggestionsAdapter.updateSuggestions(items);
                                }
                            });
                        }

                        @Override
                        public void onSearchFailed(String errorMessage) {
                            runOnUiThread(() -> {
                                Log.e(TAG, "Search suggestion error: " + errorMessage);
                                Toast.makeText(SearchActivity.this,
                                        "Error getting suggestions: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();

                                suggestionsRecyclerView.setVisibility(View.GONE);
                                noResultsText.setVisibility(View.VISIBLE);
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAutocompleteSuggestions", e);
            Toast.makeText(this, "Search error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows recent searches
     */
    private void showRecentSearches() {
        List<SearchManager.PlaceSuggestion> recentSearches = searchManager.getRecentSearches();

        if (recentSearches.isEmpty()) {
            suggestionsRecyclerView.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
            noResultsText.setText("No recent searches");
        } else {
            suggestionsRecyclerView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);

            // Convert to adapter items
            List<SearchSuggestionsAdapter.SuggestionItem> items = new ArrayList<>();
            for (SearchManager.PlaceSuggestion suggestion : recentSearches) {
                items.add(new SearchSuggestionsAdapter.SuggestionItem(
                        suggestion.getPlaceId(),
                        suggestion.getPrimaryText(),
                        suggestion.getSecondaryText(),
                        true));
            }

            suggestionsAdapter.updateSuggestions(items);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP_SELECTION && resultCode == RESULT_OK && data != null) {
            // Get selected location from map
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            Log.d(TAG, "Map selection result received: lat=" + latitude + ", lng=" + longitude);

            // Return selected location to calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("place_id", "map_selection");
            resultIntent.putExtra("place_name", "Selected Location");
            resultIntent.putExtra("place_address", "");
            resultIntent.putExtra("place_lat", latitude);
            resultIntent.putExtra("place_lng", longitude);

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}

