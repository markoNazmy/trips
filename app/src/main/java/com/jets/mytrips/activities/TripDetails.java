package com.jets.mytrips.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
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


public class TripDetails extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView tripName;
    TextView tripStart;
    TextView tripDest;
    TextView tripDate;
    TextView tripStatus;

    Bitmap imgBitmap;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Trip trip = getIntent().getParcelableExtra("TripDetails");

        //Image of place
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        tripName = (TextView) findViewById(R.id.tripNameDet);
        tripStart = (TextView) findViewById(R.id.tripStart);
        tripDest = (TextView) findViewById(R.id.tripDestDet);
        tripDate = (TextView) findViewById(R.id.dateTxtDet);
        tripStatus = (TextView) findViewById(R.id.statusTxtDet);
        LinearLayout tripNotesView = (LinearLayout) findViewById(R.id.noteList);

        tripName.setText(trip.getName());
        tripStart.setText(trip.getStart());
        tripDest.setText(trip.getEnd());
        tripDate.setText(trip.getDate());
        tripStatus.setText(trip.getStatus());

        final DBAdapter dba = new DBAdapter(this);
        trip.setNotes(dba.getTripNotes(trip.getId()));

        if (trip.getNotes().size() > 0) {
            tripNotesView.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < trip.getNotes().size(); i++) {
            TextView noteTextView = new TextView(this);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = 20;
            params.leftMargin = 10;
            noteTextView.setLayoutParams(params);
            noteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bullet, 0, 0, 0);
            noteTextView.setCompoundDrawablePadding(10);
            noteTextView.setTextSize(13);
            noteTextView.setText(trip.getNotes().get(i).getNote());
            tripNotesView.addView(noteTextView);
        }

        Log.i("myTag", "-----image place id in details: " + trip.getImage());

        //set img
        final Handler messageHandler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
                }
                BitmapDrawable toolBarBarBackground = new BitmapDrawable(getResources(), imgBitmap);
                collapsingToolbarLayout.setBackground(toolBarBarBackground);
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
