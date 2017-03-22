package com.jets.mytrips.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jets.mytrips.R;

import java.util.GregorianCalendar;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
                if (sharedPreferences.contains("email")) {
                    Intent intent = new Intent(SplashScreenActivity.this, CurrentTripsActivity.class);
                    intent.putExtra("username", sharedPreferences.getString("fullName", ""));
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
