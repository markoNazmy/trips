package com.jets.mytrips.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jets.mytrips.R;
import com.jets.mytrips.services.Switcher;

public class SplashScreenActivity extends AppCompatActivity implements Switcher {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    TextView helloSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        helloSplash = (TextView) findViewById(R.id.helloSplash);

        final SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
        final String fullname = sharedPreferences.getString("fullName", "");

        if (sharedPreferences.contains("email")) {
            helloSplash.setText("Hello, " + fullname + "!");
        } else {
            helloSplash.setText("Hello!");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.contains("email")) {
                    switchToCurrentTripsActivity(fullname);
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void switchToCurrentTripsActivity(String userFullName) {
        Intent intent = new Intent(SplashScreenActivity.this, CurrentTripsActivity.class);
        intent.putExtra("username", userFullName);
        startActivity(intent);
    }
}