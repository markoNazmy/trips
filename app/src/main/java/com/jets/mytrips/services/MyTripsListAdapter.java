package com.jets.mytrips.services;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

    public MyTripsListAdapter(Context context, ArrayList<Trip> resource) {
        super(context, R.layout.current_trips_list_cell, R.id.trip_name, resource);
        this.values = resource;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.current_trips_list_cell, parent, false);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
            System.out.println("hi");
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
            System.out.println("bye");
        }

        viewHolder.getTripName().setText(values.get(position).getName());
        //viewHolder.getPlaceImageView().setImageResource();
        viewHolder.getDate().setText(values.get(position).getDate());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripDetails.class);
                intent.putExtra("TripDetails", values.get(position));
                context.startActivity(intent);
            }
        });

        return rowView;
    }

}
