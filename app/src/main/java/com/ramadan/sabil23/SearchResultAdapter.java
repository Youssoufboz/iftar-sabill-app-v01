package com.ramadan.sabil23;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ramadan.sabil23.R;
import com.ramadan.sabil23.model.Restaurant;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final List<Restaurant> restaurants;
    private final OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public SearchResultAdapter(List<Restaurant> restaurants, OnRestaurantClickListener listener) {
        this.restaurants = restaurants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getVicinity());

        // Set rating if available
        if (restaurant.getRating() > 0) {
            holder.ratingTextView.setVisibility(View.VISIBLE);
            holder.ratingTextView.setText(String.format("%.1f ★", restaurant.getRating()));
        } else {
            holder.ratingTextView.setVisibility(View.GONE);
        }

        // Set price level if available
        if (restaurant.getPriceLevel() != null && !restaurant.getPriceLevel().isEmpty()) {
            holder.priceLevelTextView.setVisibility(View.VISIBLE);
            holder.priceLevelTextView.setText(restaurant.getPriceLevel());
        } else {
            holder.priceLevelTextView.setVisibility(View.GONE);
        }

        // Set open status
        holder.statusTextView.setText(restaurant.isOpen() ? "Open" : "Closed");
        holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(
                restaurant.isOpen() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestaurantClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView ratingTextView;
        TextView priceLevelTextView;
        TextView statusTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            priceLevelTextView = itemView.findViewById(R.id.priceLevelTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}

