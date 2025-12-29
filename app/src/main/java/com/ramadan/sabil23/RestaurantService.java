package com.ramadan.sabil23;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantService {
    private static final String TAG = "RestaurantService";
    private static final int SEARCH_RADIUS = 50000; // 50km for better results
    private static final int TIMEOUT_MS = 30000; // 30 seconds timeout

    private Context context;
    private GoogleMap map;
    private RequestQueue requestQueue;
    private Map<String, Restaurant> restaurantMap = new HashMap<>();
    private Polyline currentRoute;
    private OnRestaurantLoadedListener onRestaurantLoadedListener;
    private OnRouteLoadedListener onRouteLoadedListener;

    public RestaurantService(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void setOnRestaurantLoadedListener(OnRestaurantLoadedListener listener) {
        this.onRestaurantLoadedListener = listener;
    }

    public void setOnRouteLoadedListener(OnRouteLoadedListener listener) {
        this.onRouteLoadedListener = listener;
    }

    public void searchNearbyRestaurants(LatLng location) {
        restaurantMap.clear();

        // Don't clear the map here as it might remove other markers
        // We'll only clear restaurant markers when we add new ones

        String apiKey = context.getString(R.string.google_maps_key);

        // Log the API key (first few characters) to verify it's correct
        if (apiKey != null && apiKey.length() > 8) {
            Log.d(TAG, "Using API key starting with: " + apiKey.substring(0, 8) + "...");
        } else {
            Log.e(TAG, "API key is null or too short!");
        }

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + location.latitude + "," + location.longitude
                + "&radius=" + SEARCH_RADIUS
                + "&type=restaurant"
                + "&key=" + apiKey;

        Log.d(TAG, "Restaurant search URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Got response: " + response.toString());
                        try {
                            // Check for API error
                            if (response.has("status")) {
                                String status = response.getString("status");
                                if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
                                    String errorMessage = "API Error: " + status;
                                    if (response.has("error_message")) {
                                        errorMessage += " - " + response.getString("error_message");
                                    }
                                    Log.e(TAG, errorMessage);
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();

                                    if (onRestaurantLoadedListener != null) {
                                        onRestaurantLoadedListener.onRestaurantsLoaded(0, restaurantMap);
                                    }
                                    return;
                                }
                            }

                            JSONArray results = response.getJSONArray("results");
                            int count = results.length();
                            Log.d(TAG, "Found " + count + " restaurants");

                            // Remove old restaurant markers
                            for (Marker marker : restaurantMarkers) {
                                marker.remove();
                            }
                            restaurantMarkers.clear();

                            for (int i = 0; i < count; i++) {
                                JSONObject place = results.getJSONObject(i);
                                addRestaurantMarker(place);
                            }

                            if (onRestaurantLoadedListener != null) {
                                onRestaurantLoadedListener.onRestaurantsLoaded(count, restaurantMap);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
                            Toast.makeText(context, "Error parsing restaurant data: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                            if (onRestaurantLoadedListener != null) {
                                onRestaurantLoadedListener.onRestaurantsLoaded(0, restaurantMap);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.toString(), error);
                        String errorMsg = "Error fetching restaurants";
                        if (error.networkResponse != null) {
                            errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();

                        if (onRestaurantLoadedListener != null) {
                            onRestaurantLoadedListener.onRestaurantsLoaded(0, restaurantMap);
                        }
                    }
                });

        // Set a longer timeout for the request
        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    private List<Marker> restaurantMarkers = new ArrayList<>();

    private void addRestaurantMarker(JSONObject place) throws JSONException {
        String placeId = place.getString("place_id");
        String name = place.getString("name");

        JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
        double lat = location.getDouble("lat");
        double lng = location.getDouble("lng");
        LatLng position = new LatLng(lat, lng);

        Log.d(TAG, "Adding restaurant: " + name + " at " + lat + "," + lng);

        // Create restaurant object
        Restaurant restaurant = new Restaurant();
        restaurant.setId(placeId);
        restaurant.setName(name);
        restaurant.setLatLng(position);

        // Add optional fields if available
        if (place.has("vicinity")) {
            restaurant.setAddress(place.getString("vicinity"));
        }

        if (place.has("rating")) {
            restaurant.setRating(place.getDouble("rating"));
        }

        if (place.has("price_level")) {
            restaurant.setPriceLevel(place.getInt("price_level"));
        }

        if (place.has("opening_hours") && place.getJSONObject("opening_hours").has("open_now")) {
            restaurant.setOpenNow(place.getJSONObject("opening_hours").getBoolean("open_now"));
        }

        // Store in map for later reference
        restaurantMap.put(placeId, restaurant);

        // Create custom marker
        View markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null);
        TextView markerName = markerView.findViewById(R.id.marker_name);
        TextView markerRating = markerView.findViewById(R.id.marker_rating);

        markerName.setText(name);
        if (restaurant.getRating() > 0) {
            markerRating.setText(String.format("%.1f", restaurant.getRating()));
            markerRating.setVisibility(View.VISIBLE);
        } else {
            markerRating.setVisibility(View.GONE);
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(createBitmapFromView(markerView));

        // Add marker to map
        Marker marker = map.addMarker(new MarkerOptions()
                .position(position)
                .title(name)
                .icon(icon));

        if (marker != null) {
            marker.setTag(placeId);
            restaurantMarkers.add(marker);
        }
    }

    private Bitmap createBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void getDirections(LatLng origin, LatLng destination) {
        // Clear any existing route
        if (currentRoute != null) {
            currentRoute.remove();
            currentRoute = null;
        }

        String apiKey = context.getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=driving"
                + "&key=" + apiKey;

        Log.d(TAG, "Directions URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Directions response: " + response.toString());
                        try {
                            // Check for API error
                            if (response.has("status")) {
                                String status = response.getString("status");
                                if (!"OK".equals(status)) {
                                    String errorMessage = "Directions API Error: " + status;
                                    if (response.has("error_message")) {
                                        errorMessage += " - " + response.getString("error_message");
                                    }
                                    Log.e(TAG, errorMessage);
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            // Parse route
                            JSONArray routes = response.getJSONArray("routes");
                            if (routes.length() > 0) {
                                JSONObject route = routes.getJSONObject(0);
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String encodedPath = overviewPolyline.getString("points");
                                List<LatLng> path = decodePolyline(encodedPath);

                                Log.d(TAG, "Route decoded with " + path.size() + " points");

                                // Draw route on map
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(path)
                                        .width(12)
                                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                                        .geodesic(true);

                                currentRoute = map.addPolyline(polylineOptions);

                                // Get navigation instructions
                                JSONArray legs = route.getJSONArray("legs");
                                if (legs.length() > 0) {
                                    JSONObject leg = legs.getJSONObject(0);
                                    JSONArray steps = leg.getJSONArray("steps");
                                    String firstInstruction = "";

                                    if (steps.length() > 0) {
                                        JSONObject firstStep = steps.getJSONObject(0);
                                        firstInstruction = firstStep.getString("html_instructions")
                                                .replaceAll("<[^>]*>", " ");
                                        Log.d(TAG, "First instruction: " + firstInstruction);
                                    }

                                    String distance = leg.getJSONObject("distance").getString("text");
                                    String duration = leg.getJSONObject("duration").getString("text");
                                    Log.d(TAG, "Route info: " + distance + ", " + duration);

                                    if (onRouteLoadedListener != null) {
                                        onRouteLoadedListener.onRouteLoaded(firstInstruction, distance, duration);
                                    }

                                    // Zoom to show the entire route
                                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                                    boundsBuilder.include(origin);
                                    boundsBuilder.include(destination);

                                    // Add some points along the route to ensure good zoom level
                                    if (path.size() > 2) {
                                        boundsBuilder.include(path.get(path.size() / 2));
                                    }

                                    LatLngBounds bounds = boundsBuilder.build();

                                    // Add padding to the bounds
                                    int padding = 100; // pixels
                                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                                }
                            } else {
                                Log.e(TAG, "No routes found in response");
                                Toast.makeText(context, "No route found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing route data: " + e.getMessage(), e);
                            Toast.makeText(context, "Error parsing route data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Directions Volley error: " + error.toString(), error);
                        String errorMsg = "Error fetching route";
                        if (error.networkResponse != null) {
                            errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

        // Set a longer timeout for the request
        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    public void clearRoute() {
        if (currentRoute != null) {
            currentRoute.remove();
            currentRoute = null;
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }

    public Restaurant getRestaurantById(String id) {
        return restaurantMap.get(id);
    }

    public interface OnRestaurantLoadedListener {
        void onRestaurantsLoaded(int count, Map<String, Restaurant> restaurants);
    }

    public interface OnRouteLoadedListener {
        void onRouteLoaded(String instruction, String distance, String duration);
    }

    // Restaurant model class
    public static class Restaurant {
        private String id;
        private String name;
        private String address;
        private double rating;
        private int priceLevel;
        private boolean openNow;
        private LatLng latLng;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }

        public int getPriceLevel() { return priceLevel; }
        public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }

        public boolean isOpenNow() { return openNow; }
        public void setOpenNow(boolean openNow) { this.openNow = openNow; }

        public LatLng getLatLng() { return latLng; }
        public void setLatLng(LatLng latLng) { this.latLng = latLng; }
    }
}