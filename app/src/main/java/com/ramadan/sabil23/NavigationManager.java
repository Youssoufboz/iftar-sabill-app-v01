package com.ramadan.sabil23;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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
 * Manager class for handling navigation and routing
 */
public class NavigationManager {
    private static final String TAG = "NavigationManager";

    // API constants
    private static final String DIRECTIONS_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    // Transportation modes
    public static final String MODE_DRIVING = "driving";
    public static final String MODE_WALKING = "walking";
    public static final String MODE_TRANSIT = "transit";
    public static final String MODE_BICYCLING = "bicycling";

    // Traffic models
    public static final String TRAFFIC_MODEL_BEST_GUESS = "best_guess";
    public static final String TRAFFIC_MODEL_OPTIMISTIC = "optimistic";
    public static final String TRAFFIC_MODEL_PESSIMISTIC = "pessimistic";

    // Route preferences
    public static final String ROUTE_PREFERENCE_LESS_WALKING = "less_walking";
    public static final String ROUTE_PREFERENCE_FEWER_TRANSFERS = "fewer_transfers";

    // API key
    private String apiKey;

    // HTTP client
    private OkHttpClient httpClient;

    // Singleton instance
    private static NavigationManager instance;

    /**
     * Gets the singleton instance
     */
    public static synchronized NavigationManager getInstance(Context context) {
        if (instance == null) {
            instance = new NavigationManager(context);
        }
        return instance;
    }

    /**
     * Private constructor
     */
    private NavigationManager(Context context) {
        this.apiKey = context.getString(R.string.google_maps_key);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Route data model
     */
    public static class Route {
        private List<LatLng> points;
        private String distance;
        private String duration;
        private String durationInTraffic;
        private List<Step> steps;
        private String summary;
        private String warnings;
        private String polyline;

        public Route() {
            this.points = new ArrayList<>();
            this.steps = new ArrayList<>();
        }

        // Getters and setters
        public List<LatLng> getPoints() { return points; }
        public void addPoint(LatLng point) { this.points.add(point); }
        public String getDistance() { return distance; }
        public void setDistance(String distance) { this.distance = distance; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getDurationInTraffic() { return durationInTraffic; }
        public void setDurationInTraffic(String durationInTraffic) { this.durationInTraffic = durationInTraffic; }
        public List<Step> getSteps() { return steps; }
        public void addStep(Step step) { this.steps.add(step); }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getWarnings() { return warnings; }
        public void setWarnings(String warnings) { this.warnings = warnings; }
        public String getPolyline() { return polyline; }
        public void setPolyline(String polyline) { this.polyline = polyline; }
    }

    /**
     * Step data model for route instructions
     */
    public static class Step {
        private String instruction;
        private String distance;
        private String duration;
        private LatLng startLocation;
        private LatLng endLocation;
        private String polyline;
        private String travelMode;

        // Getters and setters
        public String getInstruction() { return instruction; }
        public void setInstruction(String instruction) { this.instruction = instruction; }
        public String getDistance() { return distance; }
        public void setDistance(String distance) { this.distance = distance; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public LatLng getStartLocation() { return startLocation; }
        public void setStartLocation(LatLng startLocation) { this.startLocation = startLocation; }
        public LatLng getEndLocation() { return endLocation; }
        public void setEndLocation(LatLng endLocation) { this.endLocation = endLocation; }
        public String getPolyline() { return polyline; }
        public void setPolyline(String polyline) { this.polyline = polyline; }
        public String getTravelMode() { return travelMode; }
        public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
    }

    /**
     * Interface for route search callbacks
     */
    public interface RouteSearchCallback {
        void onRoutesFound(List<Route> routes);
        void onSearchFailed(String errorMessage);
    }

    /**
     * Gets directions between two points
     */
    public void getDirections(LatLng origin, LatLng destination, String mode,
                              boolean alternatives, String departureTime,
                              String trafficModel, String transitPreference,
                              final RouteSearchCallback callback) {
        // Build the URL
        StringBuilder urlBuilder = new StringBuilder(DIRECTIONS_API_BASE_URL)
                .append("origin=").append(origin.latitude).append(",").append(origin.longitude)
                .append("&destination=").append(destination.latitude).append(",").append(destination.longitude)
                .append("&mode=").append(mode != null ? mode : MODE_DRIVING)
                .append("&key=").append(apiKey);

        // Add optional parameters
        if (alternatives) {
            urlBuilder.append("&alternatives=true");
        }

        if (departureTime != null) {
            urlBuilder.append("&departure_time=").append(departureTime);
        }

        if (trafficModel != null && mode.equals(MODE_DRIVING)) {
            urlBuilder.append("&traffic_model=").append(trafficModel);
        }

        if (transitPreference != null && mode.equals(MODE_TRANSIT)) {
            urlBuilder.append("&transit_routing_preference=").append(transitPreference);
        }

        // Create request
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();

        // Execute request
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Directions request failed", e);
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
                    if (!status.equals("OK")) {
                        callback.onSearchFailed("API error: " + status);
                        return;
                    }

                    // Parse routes
                    List<Route> routes = new ArrayList<>();
                    JSONArray routesArray = jsonResponse.getJSONArray("routes");

                    for (int i = 0; i < routesArray.length(); i++) {
                        JSONObject routeObj = routesArray.getJSONObject(i);
                        Route route = new Route();

                        // Set summary
                        if (routeObj.has("summary")) {
                            route.setSummary(routeObj.getString("summary"));
                        }

                        // Set warnings
                        if (routeObj.has("warnings") && routeObj.getJSONArray("warnings").length() > 0) {
                            StringBuilder warningsBuilder = new StringBuilder();
                            JSONArray warningsArray = routeObj.getJSONArray("warnings");
                            for (int j = 0; j < warningsArray.length(); j++) {
                                if (j > 0) warningsBuilder.append("; ");
                                warningsBuilder.append(warningsArray.getString(j));
                            }
                            route.setWarnings(warningsBuilder.toString());
                        }

                        // Set polyline
                        if (routeObj.has("overview_polyline") && routeObj.getJSONObject("overview_polyline").has("points")) {
                            route.setPolyline(routeObj.getJSONObject("overview_polyline").getString("points"));
                        }

                        // Parse legs
                        JSONArray legsArray = routeObj.getJSONArray("legs");
                        if (legsArray.length() > 0) {
                            JSONObject leg = legsArray.getJSONObject(0);

                            // Set distance
                            if (leg.has("distance")) {
                                route.setDistance(leg.getJSONObject("distance").getString("text"));
                            }

                            // Set duration
                            if (leg.has("duration")) {
                                route.setDuration(leg.getJSONObject("duration").getString("text"));
                            }

                            // Set duration in traffic
                            if (leg.has("duration_in_traffic")) {
                                route.setDurationInTraffic(leg.getJSONObject("duration_in_traffic").getString("text"));
                            }

                            // Parse steps
                            if (leg.has("steps")) {
                                JSONArray stepsArray = leg.getJSONArray("steps");
                                for (int j = 0; j < stepsArray.length(); j++) {
                                    JSONObject stepObj = stepsArray.getJSONObject(j);
                                    Step step = new Step();

                                    // Set instruction
                                    if (stepObj.has("html_instructions")) {
                                        step.setInstruction(stepObj.getString("html_instructions"));
                                    }

                                    // Set distance
                                    if (stepObj.has("distance")) {
                                        step.setDistance(stepObj.getJSONObject("distance").getString("text"));
                                    }

                                    // Set duration
                                    if (stepObj.has("duration")) {
                                        step.setDuration(stepObj.getJSONObject("duration").getString("text"));
                                    }

                                    // Set start location
                                    if (stepObj.has("start_location")) {
                                        JSONObject startLoc = stepObj.getJSONObject("start_location");
                                        step.setStartLocation(new LatLng(
                                                startLoc.getDouble("lat"),
                                                startLoc.getDouble("lng")));
                                    }

                                    // Set end location
                                    if (stepObj.has("end_location")) {
                                        JSONObject endLoc = stepObj.getJSONObject("end_location");
                                        step.setEndLocation(new LatLng(
                                                endLoc.getDouble("lat"),
                                                endLoc.getDouble("lng")));
                                    }

                                    // Set polyline
                                    if (stepObj.has("polyline") && stepObj.getJSONObject("polyline").has("points")) {
                                        step.setPolyline(stepObj.getJSONObject("polyline").getString("points"));
                                    }

                                    // Set travel mode
                                    if (stepObj.has("travel_mode")) {
                                        step.setTravelMode(stepObj.getString("travel_mode"));
                                    }

                                    route.addStep(step);
                                }
                            }
                        }

                        routes.add(route);
                    }

                    // Return results - no need to wrap in runOnUiThread here
                    // The callback implementation should handle threading
                    callback.onRoutesFound(routes);

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing directions data", e);
                    callback.onSearchFailed("Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Launches navigation in Google Maps app
     */
    public void launchGoogleMapsNavigation(Context context, LatLng origin, LatLng destination, String mode) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.latitude + "," + destination.longitude +
                "&mode=" + (mode != null ? mode : "d"));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Fallback to browser if Google Maps app is not installed
            Uri fallbackUri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                    "&origin=" + origin.latitude + "," + origin.longitude +
                    "&destination=" + destination.latitude + "," + destination.longitude +
                    "&travelmode=" + (mode != null ? mode : "driving"));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, fallbackUri);
            context.startActivity(browserIntent);
        }
    }

    /**
     * Decodes a polyline string into a list of LatLng points
     */
    public static List<LatLng> decodePolyline(String encoded) {
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
}

