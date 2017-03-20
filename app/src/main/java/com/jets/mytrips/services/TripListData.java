package com.jets.mytrips.services;

import android.content.Context;

import com.jets.mytrips.beans.Trip;

import java.util.ArrayList;

/**
 * Created by markoiti on 19/03/17.
 */

public class TripListData {
    private static ArrayList<Trip> tripsList;
    private static MyTripsListAdapter myTripsListAdapter ;
    private TripListData() {
    }

    public static synchronized ArrayList<Trip> getTripsListInstance() {
        if (tripsList == null) {
            tripsList = new ArrayList<>();
        }
        return tripsList;
    }
    public static synchronized MyTripsListAdapter getMyTripsListAdapterInstance(Context context, ArrayList<Trip> resource) {
        if (myTripsListAdapter == null) {
            myTripsListAdapter = new MyTripsListAdapter(context,resource);
        }
        return myTripsListAdapter;
    }

}
