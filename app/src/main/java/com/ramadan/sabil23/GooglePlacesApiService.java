package com.ramadan.sabil23;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Service class for handling Google Places API requests
 */
public class GooglePlacesApiService {
    private static final String TAG = "GooglePlacesApiService";

    // API constants
    private static final String PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String AUTOCOMPLETE_ENDPOINT = "autocomplete/json?";
    private static final String NEARBY_SEARCH_ENDPOINT = "nearbysearch/json?";
    private static final String PLACE_DETAILS_ENDPOINT = "details/json?";

    // Search parameters
    private static final int DEFAULT_RADIUS = 5000; // 5km
    private static final String DEFAULT_LANGUAGE = "en";

    // Context
    private Context context;

    // API key
    private String apiKey;

    // HTTP client
    private OkHttpClient httpClient;

    // Places client
    private PlacesClient placesClient;

    // Autocomplete session token
    private AutocompleteSessionToken sessionToken;

    /**
     * Constructor
     */
    public GooglePlacesApiService(Context context) {
        this.context = context;
        this.apiKey = context.getString(R.string.google_maps_key);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.sessionToken = AutocompleteSessionToken.newInstance();
    }

    /**
     * Sets the Places client
     */
    public void setPlacesClient(PlacesClient placesClient) {
        this.placesClient = placesClient;
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
            callback.onPlaceSuggestionsFound(new ArrayList<>());
            return;
        }

        if (placesClient == null) {
            callback.onSearchFailed("Places client not initialized");
            return;
        }

        try {
            // Create a bias toward the user's location
            RectangularBounds bounds = null;
            if (location != null) {
                // Create bounds that are roughly within 50km of the location
                double latDelta = 0.5; // Roughly 50km
                double lngDelta = 0.5; // Roughly 50km
                bounds = RectangularBounds.newInstance(
                        new LatLng(location.latitude - latDelta, location.longitude - lngDelta),
                        new LatLng(location.latitude + latDelta, location.longitude + lngDelta));
            }

            // Use the Places SDK for autocomplete
            FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest.builder()
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setSessionToken(sessionToken)
                    .setQuery(query);

            if (bounds != null) {
                requestBuilder.setLocationBias(bounds);
            }

            FindAutocompletePredictionsRequest request = requestBuilder.build();

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
                        callback.onSearchFailed("Error code: " + exception.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAutocompleteSuggestions", e);
            callback.onSearchFailed("Error: " + e.getMessage());
        }
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

                    // Return result
                    callback.onPlaceDetailsLoaded(place);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing place details", e);
                    callback.onDetailsFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Searches for nearby places
     */
    public void searchNearbyPlaces(LatLng location, int radius, String type,
                                   boolean openNow, final PlaceSearchCallback callback) {
        // Build the URL
        StringBuilder urlBuilder = new StringBuilder(PLACES_API_BASE_URL)
                .append(NEARBY_SEARCH_ENDPOINT)
                .append("location=").append(location.latitude).append(",").append(location.longitude)
                .append("&radius=").append(radius > 0 ? radius : DEFAULT_RADIUS)
                .append("&type=").append(type != null ? type : "restaurant")
                .append("&key=").append(apiKey);

        // Add optional parameters
        if (openNow) {
            urlBuilder.append("&opennow=true");
        }

        // Create request
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();

        // Execute request
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Nearby places search failed", e);
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
                    List<PlaceSuggestion> places = new ArrayList<>();

                    if (status.equals("OK")) {
                        JSONArray results = jsonResponse.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);

                            String placeId = result.getString("place_id");
                            String name = result.getString("name");
                            String vicinity = result.getString("vicinity");

                            PlaceSuggestion place = new PlaceSuggestion(placeId, name, vicinity);

                            // Set location
                            JSONObject locationObj = result.getJSONObject("geometry").getJSONObject("location");
                            place.setLocation(new LatLng(
                                    locationObj.getDouble("lat"),
                                    locationObj.getDouble("lng")));

                            places.add(place);
                        }
                    }

                    // Return results
                    callback.onPlaceSuggestionsFound(places);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing nearby places data", e);
                    callback.onSearchFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }
}

