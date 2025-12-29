package com.ramadan.sabil23;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Manager class for handling search and place suggestions
 */
public class SearchManager {
    private static final String TAG = "SearchManager";

    // API constants
    private static final String PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String AUTOCOMPLETE_ENDPOINT = "autocomplete/json?";
    private static final String PLACE_DETAILS_ENDPOINT = "details/json?";

    // Search parameters
    private static final int DEFAULT_RADIUS = 50000; // 50km
    private static final String DEFAULT_LANGUAGE = "en";

    // API key
    private String apiKey;

    // HTTP client
    private OkHttpClient httpClient;

    // Places client
    private PlacesClient placesClient;

    // Autocomplete session token
    private AutocompleteSessionToken sessionToken;

    // Recent searches cache
    private List<PlaceSuggestion> recentSearches = new ArrayList<>();

    // Singleton instance
    private static SearchManager instance;

    /**
     * Gets the singleton instance
     */
    public static synchronized SearchManager getInstance(Context context, PlacesClient placesClient) {
        if (instance == null) {
            instance = new SearchManager(context, placesClient);
        }
        return instance;
    }

    /**
     * Private constructor
     */
    private SearchManager(Context context, PlacesClient placesClient) {
        this.apiKey = context.getString(R.string.google_maps_key);
        this.placesClient = placesClient;
        this.sessionToken = AutocompleteSessionToken.newInstance();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Place suggestion data model
     */
    public static class PlaceSuggestion {
        private String placeId;
        private String primaryText;
        private String secondaryText;
        private String fullText;
        private LatLng location;
        private String distanceText;
        private String travelTimeText;
        private boolean isRecentSearch;

        public PlaceSuggestion(String placeId, String primaryText, String secondaryText) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
            this.fullText = primaryText + ", " + secondaryText;
        }

        // Getters and setters
        public String getPlaceId() { return placeId; }
        public String getPrimaryText() { return primaryText; }
        public String getSecondaryText() { return secondaryText; }
        public String getFullText() { return fullText; }
        public LatLng getLocation() { return location; }
        public void setLocation(LatLng location) { this.location = location; }
        public String getDistanceText() { return distanceText; }
        public void setDistanceText(String distanceText) { this.distanceText = distanceText; }
        public String getTravelTimeText() { return travelTimeText; }
        public void setTravelTimeText(String travelTimeText) { this.travelTimeText = travelTimeText; }
        public boolean isRecentSearch() { return isRecentSearch; }
        public void setRecentSearch(boolean recentSearch) { isRecentSearch = recentSearch; }
    }

    /**
     * Interface for place search callbacks
     */
    public interface PlaceSearchCallback {
        void onPlaceSuggestionsFound(List<PlaceSuggestion> suggestions);
        void onSearchFailed(String errorMessage);
    }

    /**
     * Interface for place details callbacks
     */
    public interface PlaceDetailsCallback {
        void onPlaceDetailsLoaded(PlaceSuggestion place);
        void onDetailsFailed(String errorMessage);
    }

    /**
     * Gets autocomplete suggestions for a query
     */
    public void getAutocompleteSuggestions(String query, LatLng location,
                                           final PlaceSearchCallback callback) {
        if (query == null || query.trim().isEmpty()) {
            // Return recent searches if query is empty
            callback.onPlaceSuggestionsFound(recentSearches);
            return;
        }

        // Use the Places SDK for autocomplete
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(sessionToken)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    List<PlaceSuggestion> suggestions = new ArrayList<>();

                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        String primaryText = prediction.getPrimaryText(null).toString();
                        String secondaryText = prediction.getSecondaryText(null).toString();

                        PlaceSuggestion suggestion = new PlaceSuggestion(
                                prediction.getPlaceId(),
                                primaryText,
                                secondaryText);

                        suggestions.add(suggestion);
                    }

                    callback.onPlaceSuggestionsFound(suggestions);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Autocomplete prediction failed", exception);
                    callback.onSearchFailed("Error getting suggestions: " + exception.getMessage());
                });
    }

    /**
     * Gets details for a place
     */
    public void getPlaceDetails(String placeId, final PlaceDetailsCallback callback) {
        // Build the URL
        String url = PLACES_API_BASE_URL + PLACE_DETAILS_ENDPOINT +
                "place_id=" + placeId +
                "&fields=name,formatted_address,geometry" +
                "&key=" + apiKey;

        // Create request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute request
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Place details request failed", e);
                callback.onDetailsFailed("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onDetailsFailed("API error: " + response.code());
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);

                    // Check status
                    String status = jsonResponse.getString("status");
                    if (!status.equals("OK")) {
                        callback.onDetailsFailed("API error: " + status);
                        return;
                    }

                    // Parse result
                    JSONObject result = jsonResponse.getJSONObject("result");

                    String name = result.getString("name");
                    String address = result.getString("formatted_address");

                    PlaceSuggestion place = new PlaceSuggestion(placeId, name, address);

                    // Set location
                    JSONObject locationObj = result.getJSONObject("geometry").getJSONObject("location");
                    place.setLocation(new LatLng(
                            locationObj.getDouble("lat"),
                            locationObj.getDouble("lng")));

                    // Add to recent searches if not already there
                    boolean alreadyInRecent = false;
                    for (PlaceSuggestion recent : recentSearches) {
                        if (recent.getPlaceId().equals(placeId)) {
                            alreadyInRecent = true;
                            break;
                        }
                    }

                    if (!alreadyInRecent) {
                        place.setRecentSearch(true);
                        recentSearches.add(0, place);

                        // Limit recent searches to 5
                        if (recentSearches.size() > 5) {
                            recentSearches.remove(recentSearches.size() - 1);
                        }
                    }

                    // Return result - no need to wrap in runOnUiThread here
                    // The callback implementation should handle threading
                    callback.onPlaceDetailsLoaded(place);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing place details", e);
                    callback.onDetailsFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Gets travel time between two locations
     */
    public void getTravelTime(LatLng origin, LatLng destination, String mode,
                              final PlaceDetailsCallback callback) {
        // Use NavigationManager to get directions
        NavigationManager navigationManager = NavigationManager.getInstance(null);
        navigationManager.getDirections(origin, destination, mode, false, null, null, null,
                new NavigationManager.RouteSearchCallback() {
                    @Override
                    public void onRoutesFound(List<NavigationManager.Route> routes) {
                        if (routes.size() > 0) {
                            NavigationManager.Route route = routes.get(0);

                            // Create a place suggestion with travel time
                            PlaceSuggestion suggestion = new PlaceSuggestion(
                                    "custom_" + destination.latitude + "_" + destination.longitude,
                                    "Custom Location",
                                    "");
                            suggestion.setLocation(destination);
                            suggestion.setDistanceText(route.getDistance());
                            suggestion.setTravelTimeText(route.getDuration());

                            callback.onPlaceDetailsLoaded(suggestion);
                        } else {
                            callback.onDetailsFailed("No routes found");
                        }
                    }

                    @Override
                    public void onSearchFailed(String errorMessage) {
                        callback.onDetailsFailed(errorMessage);
                    }
                });
    }

    /**
     * Gets Ramadan-specific suggestions near a location
     */
    public void getRamadanSuggestions(LatLng location, final PlaceSearchCallback callback) {
        // Keywords related to Ramadan
        String[] ramadanKeywords = {
                "iftar", "halal", "mosque", "islamic", "muslim", "ramadan"
        };

        // Build the URL with the first keyword
        StringBuilder urlBuilder = new StringBuilder(PLACES_API_BASE_URL)
                .append("textsearch/json?")
                .append("location=").append(location.latitude).append(",").append(location.longitude)
                .append("&radius=").append(DEFAULT_RADIUS)
                .append("&query=").append(ramadanKeywords[0])
                .append("&key=").append(apiKey);

        // Create request
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();

        // Execute request
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ramadan suggestions request failed", e);
                callback.onSearchFailed("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onSearchFailed("API error: " + response.code());
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);

                    // Check status
                    String status = jsonResponse.getString("status");
                    if (!status.equals("OK") && !status.equals("ZERO_RESULTS")) {
                        callback.onSearchFailed("API error: " + status);
                        return;
                    }

                    // Parse results
                    List<PlaceSuggestion> suggestions = new ArrayList<>();

                    if (status.equals("OK")) {
                        JSONArray results = jsonResponse.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);

                            String placeId = result.getString("place_id");
                            String name = result.getString("name");
                            String address = result.getString("formatted_address");

                            PlaceSuggestion suggestion = new PlaceSuggestion(placeId, name, address);

                            // Set location
                            JSONObject locationObj = result.getJSONObject("geometry").getJSONObject("location");
                            suggestion.setLocation(new LatLng(
                                    locationObj.getDouble("lat"),
                                    locationObj.getDouble("lng")));

                            // Calculate distance
                            float[] results1 = new float[1];
                            Location.distanceBetween(
                                    location.latitude, location.longitude,
                                    suggestion.getLocation().latitude, suggestion.getLocation().longitude,
                                    results1);

                            // Format distance
                            if (results1[0] < 1000) {
                                suggestion.setDistanceText(Math.round(results1[0]) + " m");
                            } else {
                                suggestion.setDistanceText(String.format("%.1f km", results1[0] / 1000));
                            }

                            suggestions.add(suggestion);
                        }
                    }

                    // Return results
                    callback.onPlaceSuggestionsFound(suggestions);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing Ramadan suggestions", e);
                    callback.onSearchFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Clears recent searches
     */
    public void clearRecentSearches() {
        recentSearches.clear();
    }

    /**
     * Gets recent searches
     */
    public List<PlaceSuggestion> getRecentSearches() {
        return recentSearches;
    }

    /**
     * Adds a place to recent searches
     */
    public void addToRecentSearches(PlaceSuggestion place) {
        // Remove if already exists
        for (int i = 0; i < recentSearches.size(); i++) {
            if (recentSearches.get(i).getPlaceId().equals(place.getPlaceId())) {
                recentSearches.remove(i);
                break;
            }
        }

        // Add to beginning
        place.setRecentSearch(true);
        recentSearches.add(0, place);

        // Limit to 5 recent searches
        if (recentSearches.size() > 5) {
            recentSearches.remove(recentSearches.size() - 1);
        }
    }
}

