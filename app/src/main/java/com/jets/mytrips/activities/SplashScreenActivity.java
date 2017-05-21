package com.jets.mytrips.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jets.mytrips.R;
import com.jets.mytrips.services.Switcher;

public class SplashScreenActivity extends AppCompatActivity implements Switcher {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.splash_screen_partial, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        final SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
        final String fullName = sharedPreferences.getString("fullName", "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.contains("email")) {
                    switchToCurrentTripsActivity(fullName);
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ImageView) findViewById(R.id.photo_1)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_2)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_3)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_4)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_5)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_6)).setImageDrawable(null);
        Runtime.getRuntime().gc();
    }

    @Override
    public void switchToCurrentTripsActivity(String userFullName) {
        Intent intent = new Intent(SplashScreenActivity.this, CurrentTripsActivity.class);
        intent.putExtra("username", userFullName);
        startActivity(intent);
    }
}