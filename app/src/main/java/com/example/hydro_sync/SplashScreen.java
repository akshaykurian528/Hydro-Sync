package com.example.hydro_sync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide the action bar if available
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Delayed navigation to the sign-in activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an intent to navigate to the sign-in activity
                Intent intent = new Intent(SplashScreen.this, signin.class);
                // Start the sign-in activity
                startActivity(intent);
                // Finish the current activity (splash screen)
                finish();
            }
        }, 2000); // Delay of 2000 milliseconds (2 seconds)
    }
}
