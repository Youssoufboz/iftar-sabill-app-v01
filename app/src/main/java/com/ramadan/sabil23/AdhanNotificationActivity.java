package com.ramadan.sabil23;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class AdhanNotificationActivity extends AppCompatActivity {

    // UI Elements
    private Switch switchEnableNotifications;
    private Switch switchRestaurantNotifications;
    private RadioGroup radioGroupTiming;
    private CheckBox checkboxFajr, checkboxDhuhr, checkboxAsr, checkboxMaghrib, checkboxIsha;
    private SeekBar seekBarRadius;
    private TextView textViewRadius;
    private Button buttonSave;
    private FloatingActionButton backButton;

    // Preferences
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "AdhanPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhan_notification);

        // Initialize preferences
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Prayer Notifications");

        // Initialize UI elements
        initializeViews();

        // Load saved preferences
        loadPreferences();

        // Set up listeners
        setupListeners();

        // Set up back button
        backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeViews() {
        // Switches
        switchEnableNotifications = findViewById(R.id.switchEnableNotifications);
        switchRestaurantNotifications = findViewById(R.id.switchRestaurantNotifications);

        // Radio group for timing
        radioGroupTiming = findViewById(R.id.radioGroupTiming);

        // Checkboxes for prayers
        checkboxFajr = findViewById(R.id.checkboxFajr);
        checkboxDhuhr = findViewById(R.id.checkboxDhuhr);
        checkboxAsr = findViewById(R.id.checkboxAsr);
        checkboxMaghrib = findViewById(R.id.checkboxMaghrib);
        checkboxIsha = findViewById(R.id.checkboxIsha);

        // Seek bar for radius
        seekBarRadius = findViewById(R.id.seekBarRadius);
        textViewRadius = findViewById(R.id.textViewRadius);

        // Save button
        buttonSave = findViewById(R.id.buttonSave);
    }

    private void loadPreferences() {
        // Load notification enabled state
        switchEnableNotifications.setChecked(
                preferences.getBoolean("notifications_enabled", true));

        // Load restaurant notification state
        switchRestaurantNotifications.setChecked(
                preferences.getBoolean("restaurant_notifications_enabled", true));

        // Load notification timing
        int timingMinutes = preferences.getInt("notification_timing", 15);
        switch (timingMinutes) {
            case 10:
                ((RadioButton) findViewById(R.id.radio10Min)).setChecked(true);
                break;
            case 15:
                ((RadioButton) findViewById(R.id.radio15Min)).setChecked(true);
                break;
            case 30:
                ((RadioButton) findViewById(R.id.radio30Min)).setChecked(true);
                break;
            case 45:
                ((RadioButton) findViewById(R.id.radio45Min)).setChecked(true);
                break;
        }

        // Load prayer selections
        checkboxFajr.setChecked(preferences.getBoolean("notify_fajr", true));
        checkboxDhuhr.setChecked(preferences.getBoolean("notify_dhuhr", true));
        checkboxAsr.setChecked(preferences.getBoolean("notify_asr", true));
        checkboxMaghrib.setChecked(preferences.getBoolean("notify_maghrib", true));
        checkboxIsha.setChecked(preferences.getBoolean("notify_isha", true));

        // Load radius
        int radius = preferences.getInt("restaurant_radius", 1);
        seekBarRadius.setProgress(radius);
        updateRadiusText(radius);
    }

    private void setupListeners() {
        // Enable/disable notification settings based on main switch
        switchEnableNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            enableDisableSettings(isChecked);
        });

        // Update radius text when seek bar changes
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateRadiusText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Save button click
        buttonSave.setOnClickListener(v -> savePreferences());

        // Initial state
        enableDisableSettings(switchEnableNotifications.isChecked());
    }

    private void enableDisableSettings(boolean enabled) {
        // Enable/disable all settings based on main switch
        findViewById(R.id.cardNotificationTiming).setEnabled(enabled);
        findViewById(R.id.cardPrayerSelection).setEnabled(enabled);
        findViewById(R.id.cardRestaurantNotifications).setEnabled(enabled);

        // Enable/disable individual controls
        radioGroupTiming.setEnabled(enabled);
        for (int i = 0; i < radioGroupTiming.getChildCount(); i++) {
            radioGroupTiming.getChildAt(i).setEnabled(enabled);
        }

        checkboxFajr.setEnabled(enabled);
        checkboxDhuhr.setEnabled(enabled);
        checkboxAsr.setEnabled(enabled);
        checkboxMaghrib.setEnabled(enabled);
        checkboxIsha.setEnabled(enabled);

        switchRestaurantNotifications.setEnabled(enabled);
        seekBarRadius.setEnabled(enabled && switchRestaurantNotifications.isChecked());
    }

    private void updateRadiusText(int progress) {
        // Convert progress to actual radius (1-5 km)
        int radius = progress + 1;
        textViewRadius.setText(radius + " km");
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferences.edit();

        // Save notification enabled state
        editor.putBoolean("notifications_enabled", switchEnableNotifications.isChecked());

        // Save restaurant notification state
        editor.putBoolean("restaurant_notifications_enabled", switchRestaurantNotifications.isChecked());

        // Save notification timing
        int selectedTimingId = radioGroupTiming.getCheckedRadioButtonId();
        int timingMinutes = 15; // Default

        if (selectedTimingId == R.id.radio10Min) {
            timingMinutes = 10;
        } else if (selectedTimingId == R.id.radio15Min) {
            timingMinutes = 15;
        } else if (selectedTimingId == R.id.radio30Min) {
            timingMinutes = 30;
        } else if (selectedTimingId == R.id.radio45Min) {
            timingMinutes = 45;
        }

        editor.putInt("notification_timing", timingMinutes);

        // Save prayer selections
        editor.putBoolean("notify_fajr", checkboxFajr.isChecked());
        editor.putBoolean("notify_dhuhr", checkboxDhuhr.isChecked());
        editor.putBoolean("notify_asr", checkboxAsr.isChecked());
        editor.putBoolean("notify_maghrib", checkboxMaghrib.isChecked());
        editor.putBoolean("notify_isha", checkboxIsha.isChecked());

        // Save radius
        editor.putInt("restaurant_radius", seekBarRadius.getProgress());

        // Apply changes
        editor.apply();

        // Schedule notifications based on new settings
        AdhanNotificationManager.scheduleNotifications(this);

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}