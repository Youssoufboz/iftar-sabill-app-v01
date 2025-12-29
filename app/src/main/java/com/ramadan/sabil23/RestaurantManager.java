package com.ramadan.sabil23;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Manager class for handling restaurant-related operations
 */
public class RestaurantManager {
    private static final String TAG = "RestaurantManager";

    // API constants
    private static final String PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String NEARBY_SEARCH_ENDPOINT = "nearbysearch/json?";
    private static final String PLACE_DETAILS_ENDPOINT = "details/json?";

    // Search parameters
    private static final int DEFAULT_RADIUS = 5000; // 5km
    private static final int MAX_RESULTS = 20;

    // Restaurant types
    public static final String TYPE_ALL = "restaurant";
    public static final String TYPE_HALAL = "halal";
    public static final String TYPE_IFTAR_SPECIAL = "iftar";

    // Price levels
    public static final int PRICE_ANY = 0;
    public static final int PRICE_INEXPENSIVE = 1;
    public static final int PRICE_MODERATE = 2;
    public static final int PRICE_EXPENSIVE = 3;
    public static final int PRICE_VERY_EXPENSIVE = 4;

    // Sort options
    public static final int SORT_DISTANCE = 0;
    public static final int SORT_RATING = 1;
    public static final int SORT_PRICE = 2;

    // Cache for restaurant data
    private Map<String, Restaurant> restaurantCache = new HashMap<>();

    // API key
    private String apiKey;

    // HTTP client
    private OkHttpClient httpClient;

    // Singleton instance
    private static RestaurantManager instance;

    /**
     * Gets the singleton instance
     */
    public static synchronized RestaurantManager getInstance(Context context) {
        if (instance == null) {
            instance = new RestaurantManager(context);
        }
        return instance;
    }

    /**
     * Private constructor
     */
    private RestaurantManager(Context context) {
        this.apiKey = context.getString(R.string.google_maps_key);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Restaurant data model
     */
    public static class Restaurant {
        private String id;
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private float rating;
        private int priceLevel;
        private boolean isOpenNow;
        private String photoUrl;
        private double distance;
        private boolean isHalal;
        private boolean hasIftarSpecial;
        private List<String> types;
        private Map<String, String> openingHours;
        private String phoneNumber;
        private String website;

        public Restaurant(String id, String name) {
            this.id = id;
            this.name = name;
            this.types = new ArrayList<>();
            this.openingHours = new HashMap<>();
        }

        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public float getRating() { return rating; }
        public void setRating(float rating) { this.rating = rating; }
        public int getPriceLevel() { return priceLevel; }
        public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }
        public boolean isOpenNow() { return isOpenNow; }
        public void setOpenNow(boolean openNow) { isOpenNow = openNow; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }
        public boolean isHalal() { return isHalal; }
        public void setHalal(boolean halal) { isHalal = halal; }
        public boolean hasIftarSpecial() { return hasIftarSpecial; }
        public void setHasIftarSpecial(boolean hasIftarSpecial) { this.hasIftarSpecial = hasIftarSpecial; }
        public List<String> getTypes() { return types; }
        public void addType(String type) { this.types.add(type); }
        public Map<String, String> getOpeningHours() { return openingHours; }
        public void addOpeningHours(String day, String hours) { this.openingHours.put(day, hours); }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }

        public LatLng getLatLng() {
            return new LatLng(latitude, longitude);
        }

        /**
         * Checks if the restaurant is likely to be halal based on its types and name
         */
        public boolean checkIfLikelyHalal() {
            // Check types for halal indicators
            for (String type : types) {
                if (type.toLowerCase().contains("halal") ||
                        type.toLowerCase().contains("muslim") ||
                        type.toLowerCase().contains("middle_eastern") ||
                        type.toLowerCase().contains("pakistani") ||
                        type.toLowerCase().contains("turkish") ||
                        type.toLowerCase().contains("arabic")) {
                    return true;
                }
            }

            // Check name for halal indicators
            String nameLower = name.toLowerCase();
            if (nameLower.contains("halal") ||
                    nameLower.contains("muslim") ||
                    nameLower.contains("islam") ||
                    nameLower.contains("mecca") ||
                    nameLower.contains("medina") ||
                    nameLower.contains("arabia")) {
                return true;
            }

            return false;
        }

        /**
         * Checks if the restaurant is likely to have Iftar specials based on its types and name
         */
        public boolean checkIfLikelyHasIftarSpecial() {
            // Check name for Iftar indicators
            String nameLower = name.toLowerCase();
            if (nameLower.contains("iftar") ||
                    nameLower.contains("ramadan") ||
                    nameLower.contains("buffet")) {
                return true;
            }

            // During Ramadan, many Middle Eastern, Pakistani, Turkish restaurants offer Iftar
            for (String type : types) {
                if (type.toLowerCase().contains("middle_eastern") ||
                        type.toLowerCase().contains("pakistani") ||
                        type.toLowerCase().contains("turkish") ||
                        type.toLowerCase().contains("arabic") ||
                        type.toLowerCase().contains("indian") ||
                        type.toLowerCase().contains("mediterranean")) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Interface for restaurant search callbacks
     */
    public interface RestaurantSearchCallback {
        void onRestaurantsFound(List<Restaurant> restaurants);
        void onSearchFailed(String errorMessage);
    }

    /**
     * Interface for restaurant details callbacks
     */
    public interface RestaurantDetailsCallback {
        void onRestaurantDetailsLoaded(Restaurant restaurant);
        void onDetailsFailed(String errorMessage);
    }

    /**
     * Searches for restaurants near a location
     */
    public void searchNearbyRestaurants(LatLng location, int radius, String type,
                                        int priceLevel, boolean openNow,
                                        final RestaurantSearchCallback callback) {
        // Build the URL
        StringBuilder urlBuilder = new StringBuilder(PLACES_API_BASE_URL)
                .append(NEARBY_SEARCH_ENDPOINT)
                .append("location=").append(location.latitude).append(",").append(location.longitude)
                .append("&radius=").append(radius > 0 ? radius : DEFAULT_RADIUS)
                .append("&type=restaurant")
                .append("&key=").append(apiKey);

        // Add optional parameters
        if (openNow) {
            urlBuilder.append("&opennow=true");
        }

        if (priceLevel > 0 && priceLevel <= 4) {
            urlBuilder.append("&maxprice=").append(priceLevel);
        }

        // Add keyword for specific types
        if (type.equals(TYPE_HALAL)) {
            urlBuilder.append("&keyword=halal");
        } else if (type.equals(TYPE_IFTAR_SPECIAL)) {
            urlBuilder.append("&keyword=iftar");
        }

        // Create request
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();

        // Execute request
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Restaurant search failed", e);
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
                    List<Restaurant> restaurants = new ArrayList<>();

                    if (status.equals("OK")) {
                        JSONArray results = jsonResponse.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);

                            // Create restaurant object
                            String placeId = result.getString("place_id");
                            String name = result.getString("name");

                            Restaurant restaurant = new Restaurant(placeId, name);

                            // Set location
                            JSONObject locationObj = result.getJSONObject("geometry").getJSONObject("location");
                            restaurant.setLatitude(locationObj.getDouble("lat"));
                            restaurant.setLongitude(locationObj.getDouble("lng"));

                            // Set address
                            if (result.has("vicinity")) {
                                restaurant.setAddress(result.getString("vicinity"));
                            }

                            // Set rating
                            if (result.has("rating")) {
                                restaurant.setRating((float) result.getDouble("rating"));
                            }

                            // Set price level
                            if (result.has("price_level")) {
                                restaurant.setPriceLevel(result.getInt("price_level"));
                            }

                            // Set open now status
                            if (result.has("opening_hours") && result.getJSONObject("opening_hours").has("open_now")) {
                                restaurant.setOpenNow(result.getJSONObject("opening_hours").getBoolean("open_now"));
                            }

                            // Set photo URL
                            if (result.has("photos") && result.getJSONArray("photos").length() > 0) {
                                JSONObject photo = result.getJSONArray("photos").getJSONObject(0);
                                String photoReference = photo.getString("photo_reference");
                                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo" +
                                        "?maxwidth=400" +
                                        "&photoreference=" + photoReference +
                                        "&key=" + apiKey;
                                restaurant.setPhotoUrl(photoUrl);
                            }

                            // Set types
                            if (result.has("types")) {
                                JSONArray types = result.getJSONArray("types");
                                for (int j = 0; j < types.length(); j++) {
                                    restaurant.addType(types.getString(j));
                                }
                            }

                            // Calculate distance
                            float[] results1 = new float[1];
                            Location.distanceBetween(
                                    location.latitude, location.longitude,
                                    restaurant.getLatitude(), restaurant.getLongitude(),
                                    results1);
                            restaurant.setDistance(results1[0]);

                            // Check if likely halal
                            restaurant.setHalal(restaurant.checkIfLikelyHalal());

                            // Check if likely has Iftar special
                            restaurant.setHasIftarSpecial(restaurant.checkIfLikelyHasIftarSpecial());

                            // Add to list
                            restaurants.add(restaurant);

                            // Add to cache
                            restaurantCache.put(placeId, restaurant);
                        }
                    }

                    // Return results - no need to wrap in runOnUiThread here
                    // The callback implementation should handle threading
                    callback.onRestaurantsFound(restaurants);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing restaurant data", e);
                    callback.onSearchFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Gets detailed information about a restaurant
     */
    public void getRestaurantDetails(String placeId, final RestaurantDetailsCallback callback) {
        // Check cache first
        if (restaurantCache.containsKey(placeId)) {
            Restaurant cachedRestaurant = restaurantCache.get(placeId);

            // If we already have detailed info, return it
            if (cachedRestaurant.getOpeningHours() != null && !cachedRestaurant.getOpeningHours().isEmpty()) {
                callback.onRestaurantDetailsLoaded(cachedRestaurant);
                return;
            }
        }

        // Build the URL
        String url = PLACES_API_BASE_URL + PLACE_DETAILS_ENDPOINT +
                "place_id=" + placeId +
                "&fields=name,formatted_address,geometry,formatted_phone_number,website,opening_hours,price_level,rating" +
                "&key=" + apiKey;

        // Create request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute request
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Restaurant details request failed", e);
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

                    // Get or create restaurant object
                    String placeId = result.getString("place_id");
                    Restaurant restaurant;

                    if (restaurantCache.containsKey(placeId)) {
                        restaurant = restaurantCache.get(placeId);
                    } else {
                        String name = result.getString("name");
                        restaurant = new Restaurant(placeId, name);

                        // Set location
                        JSONObject locationObj = result.getJSONObject("geometry").getJSONObject("location");
                        restaurant.setLatitude(locationObj.getDouble("lat"));
                        restaurant.setLongitude(locationObj.getDouble("lng"));
                    }

                    // Set address
                    if (result.has("formatted_address")) {
                        restaurant.setAddress(result.getString("formatted_address"));
                    }

                    // Set phone number
                    if (result.has("formatted_phone_number")) {
                        restaurant.setPhoneNumber(result.getString("formatted_phone_number"));
                    }

                    // Set website
                    if (result.has("website")) {
                        restaurant.setWebsite(result.getString("website"));
                    }

                    // Set rating
                    if (result.has("rating")) {
                        restaurant.setRating((float) result.getDouble("rating"));
                    }

                    // Set price level
                    if (result.has("price_level")) {
                        restaurant.setPriceLevel(result.getInt("price_level"));
                    }

                    // Set opening hours
                    if (result.has("opening_hours") && result.getJSONObject("opening_hours").has("weekday_text")) {
                        JSONArray weekdayText = result.getJSONObject("opening_hours").getJSONArray("weekday_text");
                        for (int i = 0; i < weekdayText.length(); i++) {
                            String dayHours = weekdayText.getString(i);
                            String[] parts = dayHours.split(": ", 2);
                            if (parts.length == 2) {
                                restaurant.addOpeningHours(parts[0], parts[1]);
                            }
                        }

                        // Set open now status
                        if (result.getJSONObject("opening_hours").has("open_now")) {
                            restaurant.setOpenNow(result.getJSONObject("opening_hours").getBoolean("open_now"));
                        }
                    }

                    // Update cache
                    restaurantCache.put(placeId, restaurant);

                    // Return result - no need to wrap in runOnUiThread here
                    // The callback implementation should handle threading
                    callback.onRestaurantDetailsLoaded(restaurant);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing restaurant details", e);
                    callback.onDetailsFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Searches for restaurants that are open during Iftar time
     */
    public void searchRestaurantsOpenDuringIftar(LatLng location, Calendar iftarTime,
                                                 int radius, String type, int priceLevel,
                                                 final RestaurantSearchCallback callback) {
        // First search for nearby restaurants
        searchNearbyRestaurants(location, radius, type, priceLevel, true, new RestaurantSearchCallback() {
            @Override
            public void onRestaurantsFound(List<Restaurant> restaurants) {
                // Filter restaurants that are likely to be open during Iftar
                List<Restaurant> filteredRestaurants = new ArrayList<>();

                for (Restaurant restaurant : restaurants) {
                    // For now, just include all open restaurants
                    // In a real implementation, we would check the opening hours against Iftar time
                    if (restaurant.isOpenNow()) {
                        filteredRestaurants.add(restaurant);
                    }
                }

                callback.onRestaurantsFound(filteredRestaurants);
            }

            @Override
            public void onSearchFailed(String errorMessage) {
                callback.onSearchFailed(errorMessage);
            }
        });
    }

    /**
     * Formats the price level as a string of dollar signs
     */
    public static String formatPriceLevel(int priceLevel) {
        switch (priceLevel) {
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
                return "$$$";
            case 4:
                return "$$$$";
            default:
                return "N/A";
        }
    }

    /**
     * Formats the distance in a human-readable format
     */
    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return String.format(Locale.getDefault(), "%.0f m", distanceInMeters);
        } else {
            return String.format(Locale.getDefault(), "%.1f km", distanceInMeters / 1000);
        }
    }

    /**
     * Formats the open status in a human-readable format
     */
    public static String formatOpenStatus(boolean isOpenNow) {
        return isOpenNow ? "Open now" : "Closed";
    }
}

