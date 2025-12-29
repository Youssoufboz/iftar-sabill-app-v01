package com.ramadan.sabil23;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2200; // 2.2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide status bar for a more immersive experience
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Find views
        ImageView logoImageView = findViewById(R.id.splash_logo);
        TextView appNameTextView = findViewById(R.id.splash_app_name);

        // Load animations
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Set animation listeners for smoother transitions
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the text animation after logo animation completes
                appNameTextView.setVisibility(View.VISIBLE);
                appNameTextView.startAnimation(slideUpAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Initially hide the app name text
        appNameTextView.setVisibility(View.INVISIBLE);

        // Start logo animation
        logoImageView.startAnimation(fadeInAnimation);

        // Navigate to MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Use a custom transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}

