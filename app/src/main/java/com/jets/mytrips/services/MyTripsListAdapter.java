package com.jets.mytrips.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.jets.mytrips.R;
import com.jets.mytrips.activities.TripDetails;
import com.jets.mytrips.beans.Trip;

import java.util.ArrayList;

/**
 * Created by markoiti on 04/03/17.
 */

public class MyTripsListAdapter extends ArrayAdapter {
    ArrayList<Trip> values;
    Context context;
    Bitmap imgBitmap;

    public MyTripsListAdapter(Context context, ArrayList<Trip> resource) {
        super(context, R.layout.current_trips_list_cell, R.id.trip_name, resource);
        this.values = resource;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.current_trips_list_cell, parent, false);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);

            //set img
            final Handler placeHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        imgBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.map);
                        viewHolder.getPlaceImageView().setImageBitmap(imgBitmap);
                    }
                    else {
                        viewHolder.getPlaceImageView().setImageBitmap(imgBitmap);
                    }
                }
            };

            new Thread() {
                public void run() {
                    imgBitmap = downloadImg(values.get(position).getImage());
                    if (imgBitmap == null)
                        placeHandler.sendEmptyMessage(0);
                    else
                        placeHandler.sendEmptyMessage(1);
                }
            }.start();
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.getTripName().setText(values.get(position).getName());
        viewHolder.getDate().setText(values.get(position).getDate());

        return rowView;
    }

    private Bitmap downloadImg(String placeId) {

        //Image of place
        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                //.enableAutoManage(context, context)
                .build();

        mGoogleApiClient.connect();

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                int rand = (int) (Math.floor(Math.random()) * photoMetadataBuffer.getCount());
                PlacePhotoMetadata photo = photoMetadataBuffer.get(rand);
                imgBitmap = photo.getScaledPhoto(mGoogleApiClient, 1000, 1000).await()
                        .getBitmap();
            }
            else{
                imgBitmap = null;
            }

            photoMetadataBuffer.release();
        }

        return imgBitmap;
    }
}
