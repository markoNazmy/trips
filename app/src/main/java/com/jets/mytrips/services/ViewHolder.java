package com.jets.mytrips.services;

import android.view.View;
import android.widget.TextView;

import com.jets.mytrips.R;

/**
 * Created by markoiti on 04/03/17.
 */

public class ViewHolder {
    View view;





    public TextView getFrom() {
        if(from==null){
            from = (TextView) view.findViewById(R.id.from);
        }
        return from;
    }

    public TextView getTo() {
        if(to==null){
            to = (TextView) view.findViewById(R.id.to);
        }
        return to;
    }

    public TextView getDate() {
        if(date==null){
            date = (TextView) view.findViewById(R.id.date);
        }
        return date;
    }

    TextView from;
    TextView to;
    TextView date;


    public ViewHolder(View view) {
        this.view=view;
    }

}
