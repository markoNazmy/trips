package com.jets.mytrips.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.jets.mytrips.R;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.database.DBAdapter;

//import com.jets.mytrips.R;

public class TripDetails extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextView tripName;
    TextView tripDest;
    TextView tripDate;
    TextView tripNotes;
    TextView tripStatus;

    ImageView tripImg;
    Bitmap imgBitmap;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Trip trip = getIntent().getParcelableExtra("TripDetails");

        //Image of place
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show location on map
                String map = "http://maps.google.co.in/maps?q=" + trip.getEnd();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);
            }
        });

        tripName = (TextView) findViewById(R.id.tripNameDet);
        tripDest = (TextView) findViewById(R.id.tripDestDet);
        tripDate = (TextView) findViewById(R.id.dateTxtDet);
        tripNotes = (TextView) findViewById(R.id.tripNotesDet);
        tripImg = (ImageView) findViewById(R.id.tripImgDet);
        tripStatus = (TextView) findViewById(R.id.statusTxtDet);

        tripName.setText(trip.getName());
        tripDest.setText(trip.getStart() + " - " + trip.getEnd());
        tripDate.setText(trip.getDate());
        tripStatus.setText("Status: " + trip.getStatus());

        final DBAdapter dba = new DBAdapter(this);
        trip.setNotes(dba.getTripNotes(trip.getId()));

        if (trip.getNotes().size() > 0)
            tripNotes.setText("Remember to: \n");

        for (int i = 0; i < trip.getNotes().size(); i++) {
            tripNotes.setText(tripNotes.getText().toString() + trip.getNotes().get(i).getNote() + "\n");
        }

        Log.i("myTag", "-----image place id in details: " + trip.getImage());

        //set img
        final Handler messageHandler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
                    tripImg.setImageBitmap(imgBitmap);
                } else {
                    tripImg.setImageBitmap(imgBitmap);
                }
            }
        };

        new Thread() {
            public void run() {
                imgBitmap = downloadBitmap(trip.getImage());

                if (imgBitmap == null)
                    messageHandler.sendEmptyMessage(0);
                else
                    messageHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private Bitmap downloadBitmap(String url) {

        mGoogleApiClient.connect();

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, url).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                // Get the first bitmap and its attributions.

                int rand = (int) (Math.floor(Math.random()) * photoMetadataBuffer.getCount());

                PlacePhotoMetadata photo = photoMetadataBuffer.get(rand);
                CharSequence attribution = photo.getAttributions();
                // Load a scaled bitmap for this photo.
                imgBitmap = photo.getScaledPhoto(mGoogleApiClient, 1000, 1000).await()
                        .getBitmap();
            } else {
                imgBitmap = null;
            }

            photoMetadataBuffer.release();
        }

        return imgBitmap;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("myTag", "-----Connection to get image failed");
    }
}
