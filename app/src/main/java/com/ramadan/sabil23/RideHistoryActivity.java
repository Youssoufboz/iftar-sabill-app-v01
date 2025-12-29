package com.ramadan.sabil23;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RideHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private TextView emptyView;
    private RideHistoryAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton backButton, filterButton, clearHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        emptyView = findViewById(R.id.emptyView);
        backButton = findViewById(R.id.backButton);
        filterButton = findViewById(R.id.filterButton);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up button click listeners
        setupButtonListeners();

        // Load ride history
        loadRideHistory();
    }

    private void setupButtonListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Filter button click listener
        filterButton.setOnClickListener(v -> {
            showFilterOptions();
        });

        // Clear history button click listener
        clearHistoryButton.setOnClickListener(v -> {
            showClearHistoryConfirmation();
        });
    }

    private void showFilterOptions() {
        // Create filter options
        final CharSequence[] options = {"All Rides", "Last Week", "Last Month"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Rides");
        builder.setItems(options, (dialog, which) -> {
            // Handle filter selection
            switch (which) {
                case 0: // All Rides
                    loadRideHistory();
                    break;
                case 1: // Last Week
                    filterRidesByPeriod("week");
                    break;
                case 2: // Last Month
                    filterRidesByPeriod("month");
                    break;
            }
            Toast.makeText(RideHistoryActivity.this,
                    "Filtered by " + options[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void filterRidesByPeriod(String period) {
        List<RideHistoryItem> filteredList;

        if (period.equals("week")) {
            // Get rides from last week
            filteredList = databaseHelper.getRidesFromLastWeek();
        } else if (period.equals("month")) {
            // Get rides from last month
            filteredList = databaseHelper.getRidesFromLastMonth();
        } else {
            // Default to all rides
            filteredList = databaseHelper.getAllRides();
        }

        updateRideHistoryDisplay(filteredList);
    }

    private void loadRideHistory() {
        // Get ride history from database
        List<RideHistoryItem> rideHistory = databaseHelper.getAllRides();
        updateRideHistoryDisplay(rideHistory);
    }

    private void updateRideHistoryDisplay(List<RideHistoryItem> rides) {
        // Check if we have any ride history
        if (rides.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);

            // Set up adapter
            adapter = new RideHistoryAdapter(rides);
            recyclerView.setAdapter(adapter);
        }
    }

    private void showClearHistoryConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to clear all ride history?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    databaseHelper.clearRideHistory();
                    loadRideHistory(); // Refresh the list
                    Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Ride history item data model
     */
    public static class RideHistoryItem {
        private String destination;
        private String time;

        public RideHistoryItem(String destination, String time) {
            this.destination = destination;
            this.time = time;
        }

        public String getDestination() { return destination; }
        public String getTime() { return time; }
    }

    /**
     * Adapter for ride history items
     */
    private class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {
        private List<RideHistoryItem> items;

        public RideHistoryAdapter(List<RideHistoryItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_ride_history, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RideHistoryItem item = items.get(position);
            holder.destinationText.setText(item.getDestination());
            holder.timeText.setText(item.getTime());

            // Set click listener for item
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(RideHistoryActivity.this,
                        "Selected: " + item.getDestination(), Toast.LENGTH_SHORT).show();
                // You can add more functionality here, like showing ride details
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView rideIcon;
            TextView destinationText;
            TextView timeText;

            public ViewHolder(View itemView) {
                super(itemView);
                rideIcon = itemView.findViewById(R.id.rideIcon);
                destinationText = itemView.findViewById(R.id.destinationText);
                timeText = itemView.findViewById(R.id.timeText);
            }
        }
    }
}