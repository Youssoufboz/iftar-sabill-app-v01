package com.ramadan.sabil23;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ramadan.sabil23.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private Context context;
    private List<Restaurant> restaurants;
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onNavigateClick(Restaurant restaurant);
        void onCallClick(Restaurant restaurant);
    }

    public RestaurantAdapter(Context context, List<Restaurant> restaurants,
                             OnRestaurantClickListener listener) {
        this.context = context;
        this.restaurants = restaurants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        // Set restaurant data
        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getVicinity());

        // Calculate distance (simplified)
        double lat1 = restaurant.getPosition().latitude;
        double lon1 = restaurant.getPosition().longitude;
        double lat2 = restaurants.get(0).getPosition().latitude; // Use first restaurant as reference
        double lon2 = restaurants.get(0).getPosition().longitude;
        double distance = calculateDistance(lat1, lon1, lat2, lon2);

        holder.distanceTextView.setText(formatDistance(distance));
        holder.ratingBar.setRating((float) restaurant.getRating());

        // Set status text
        holder.statusTextView.setText(restaurant.isOpen() ? "Open Now" : "Closed");
        holder.statusTextView.setTextColor(context.getResources().getColor(
                restaurant.isOpen() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

        // Set click listeners
        holder.navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNavigateClick(restaurant);
            }
        });

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCallClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    // Helper method to calculate distance between two points
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Convert to meters
    }

    // Helper method to format distance
    private String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return Math.round(distanceInMeters) + " m";
        } else {
            return String.format("%.1f km", distanceInMeters / 1000);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView distanceTextView;
        TextView statusTextView;
        RatingBar ratingBar;
        Button navigateButton;
        Button callButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurantName);
            addressTextView = itemView.findViewById(R.id.restaurantAddress);
            distanceTextView = itemView.findViewById(R.id.restaurantDistance);
            statusTextView = itemView.findViewById(R.id.restaurantStatus);
            ratingBar = itemView.findViewById(R.id.restaurantRating);
            navigateButton = itemView.findViewById(R.id.navigateButton);
            callButton = itemView.findViewById(R.id.callButton);
        }
    }
}