package com.ramadan.sabil23;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Activity for displaying the side menu with various options
 */
public class SideMenuActivity extends AppCompatActivity {
    private static final String TAG = "SideMenuActivity";

    // UI elements
    private ImageButton closeButton;
    private TextView rideHistoryOption;
    private TextView messagesOption;
    private TextView helpOption;
    private LinearLayout rideHistoryContainer;
    private RecyclerView rideHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        // Initialize UI elements
        closeButton = findViewById(R.id.closeButton);
        rideHistoryOption = findViewById(R.id.rideHistoryOption);
        messagesOption = findViewById(R.id.messagesOption);
        helpOption = findViewById(R.id.helpOption);
        rideHistoryContainer = findViewById(R.id.rideHistoryContainer);
        rideHistoryRecyclerView = findViewById(R.id.rideHistoryRecyclerView);

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Sets up click listeners for UI elements
     */
    private void setupClickListeners() {
        // Close button click
        closeButton.setOnClickListener(v -> finish());

        // Ride history option click - now opens a new activity
        rideHistoryOption.setOnClickListener(v -> {
            // Open RideHistoryActivity instead of showing container
            Intent intent = new Intent(SideMenuActivity.this, RideHistoryActivity.class);
            startActivity(intent);
        });

        // Messages option click
        messagesOption.setOnClickListener(v -> {
            // Open Adhan notification settings
            Intent intent = new Intent(SideMenuActivity.this, AdhanNotificationActivity.class);
            startActivity(intent);
        });

        // Help option click
        helpOption.setOnClickListener(v -> {
            // Show help information
            Toast.makeText(this, "Help information coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}