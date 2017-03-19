package com.jets.mytrips.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.mytrips.R;
import com.jets.mytrips.beans.Trip;

import java.util.ArrayList;

/**
 * Created by markoiti on 04/03/17.
 */

public class MyTripsListAdapter extends ArrayAdapter {
    ArrayList<Trip> values;
    Context context;
    public MyTripsListAdapter(Context context, ArrayList<Trip> resource) {
        super(context, R.layout.current_trips_list_cell,R.id.from,resource);
        this.values =resource;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       View rowView= convertView;
       ViewHolder viewHolder ;
        if (rowView==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.current_trips_list_cell,parent,false);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
            System.out.println("hi");
        }
        else{
            viewHolder =(ViewHolder) rowView.getTag();
            System.out.println("bye");
        }
        viewHolder.getFrom().setText(values.get(position).getStart());
        viewHolder.getTo().setText(values.get(position).getEnd());
        viewHolder.getDate().setText(values.get(position).getDate());


        return rowView;
    }

}
