package com.ramadan.sabil23;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Bottom sheet dialog fragment for displaying restaurant details
 */
public class RestaurantDetailsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "RestaurantDetailsSheet";

    // Restaurant data
    private RestaurantManager.Restaurant restaurant;

    // Context
    private Context context;

    // Navigation manager
    private NavigationManager navigationManager;

    /**
     * Constructor
     */
    public RestaurantDetailsBottomSheet(Context context, RestaurantManager.Restaurant restaurant) {
        this.context = context;
        this.restaurant = restaurant;
        this.navigationManager = NavigationManager.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_restaurant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get UI elements
        ImageView photoImageView = view.findViewById(R.id.restaurant_photo);
        TextView nameTextView = view.findViewById(R.id.restaurant_name);
        TextView ratingTextView = view.findViewById(R.id.restaurant_rating);
        TextView priceLevelTextView = view.findViewById(R.id.restaurant_price_level);
        TextView statusTextView = view.findViewById(R.id.restaurant_status);
        TextView addressTextView = view.findViewById(R.id.restaurant_address);
        Button navigateButton = view.findViewById(R.id.navigate_button);

        // Set restaurant details
        nameTextView.setText(restaurant.getName());
        addressTextView.setText(restaurant.getAddress());

        // Set status
        String statusText = restaurant.isOpenNow() ? "Open Now" : "Closed";
        if (restaurant.hasIftarSpecial()) {
            statusText += " • Iftar Special";
        }
        if (restaurant.isHalal()) {
            statusText += " • Halal";
        }
        statusTextView.setText(statusText);
        statusTextView.setTextColor(restaurant.isOpenNow() ?
                getResources().getColor(android.R.color.holo_green_dark) :
                getResources().getColor(android.R.color.holo_red_dark));

        // Set rating if available
        if (restaurant.getRating() > 0) {
            ratingTextView.setText(String.format("%.1f ★", restaurant.getRating()));
            ratingTextView.setVisibility(View.VISIBLE);
        } else {
            ratingTextView.setVisibility(View.GONE);
        }

        // Set price level if available
        if (restaurant.getPriceLevel() > 0) {
            priceLevelTextView.setText(RestaurantManager.formatPriceLevel(restaurant.getPriceLevel()));
            priceLevelTextView.setVisibility(View.VISIBLE);
        } else {
            priceLevelTextView.setVisibility(View.GONE);
        }

        // Load photo if available
        if (restaurant.getPhotoUrl() != null && !restaurant.getPhotoUrl().isEmpty()) {
            try {
                // Use Glide to load the image
                Glide.with(context)
                        .load(restaurant.getPhotoUrl())
                        .placeholder(R.drawable.placeholder_restaurant)
                        .into(photoImageView);
                photoImageView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                photoImageView.setVisibility(View.GONE);
            }
        } else {
            photoImageView.setVisibility(View.GONE);
        }

        // Set navigate button click listener
        navigateButton.setOnClickListener(v -> {
            // Get current location
            LatLng currentLocation = null;
            if (context instanceof MainActivity) {
                MainActivity activity = (MainActivity) context;
                if (activity.getCurrentLocation() != null) {
                    currentLocation = new LatLng(
                            activity.getCurrentLocation().getLatitude(),
                            activity.getCurrentLocation().getLongitude());
                }
            }

            // If current location is not available, use a default location
            if (currentLocation == null) {
                currentLocation = new LatLng(0, 0);
            }

            // Launch navigation
            navigationManager.launchGoogleMapsNavigation(
                    context,
                    currentLocation,
                    restaurant.getLatLng(),
                    NavigationManager.MODE_DRIVING);

            // Dismiss the bottom sheet
            dismiss();
        });
    }
}
