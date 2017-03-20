package com.jets.mytrips.services;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jets.mytrips.R;

/**
 * Created by markoiti on 04/03/17.
 */

public class ViewHolder {
    View view;


    public TextView getTripName() {
        if (trip_name == null) {
            trip_name = (TextView) view.findViewById(R.id.trip_name);
        }
        return trip_name;
    }

    public ImageView getPlaceImageView() {
        if (placeImageView == null) {
            placeImageView = (ImageView) view.findViewById(R.id.placeImageView);
        }
        return placeImageView;
    }

    public TextView getDate() {
        if (date == null) {
            date = (TextView) view.findViewById(R.id.date);
        }
        return date;
    }

    TextView trip_name;
    ImageView placeImageView;
    TextView date;


    public ViewHolder(View view) {
        this.view = view;
    }

}
