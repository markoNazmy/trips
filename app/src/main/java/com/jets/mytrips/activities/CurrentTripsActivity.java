package com.jets.mytrips.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jets.mytrips.R;

public class CurrentTripsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trips);

        TextView usernameTextView = (TextView) findViewById(R.id.username_txt_view);
        usernameTextView.setText("Welcome " + getIntent().getStringExtra("username"));
    }
}
