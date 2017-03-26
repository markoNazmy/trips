package com.jets.mytrips.services;

import android.content.Context;

import com.jets.mytrips.beans.Trip;

import java.util.ArrayList;

/**
 * Created by markoiti on 19/03/17.
 */

public class TripListData {
    private static ArrayList<Trip> upcomingTripsList;
    private static ArrayList<Trip> historicalTripsList;
    private static MyTripsListAdapter myTripsListAdapter;
    private static MyTripsListAdapter myHistoricalTripsListAdapter;

    private TripListData() {
    }

    public static synchronized ArrayList<Trip> getUpcomingTripsListInstance() {
        if (upcomingTripsList == null) {
            upcomingTripsList = new ArrayList<>();
        }
        return upcomingTripsList;
    }

    public static synchronized ArrayList<Trip> getHistoricalTripsListInstance() {
        if (historicalTripsList == null) {
            historicalTripsList = new ArrayList<>();
        }
        return historicalTripsList;
    }

    public static synchronized MyTripsListAdapter getMyTripsListAdapterInstance(Context context, ArrayList<Trip> resource) {
        if (myTripsListAdapter == null) {
            myTripsListAdapter = new MyTripsListAdapter(context, resource);
        }
        return myTripsListAdapter;
    }

    public static synchronized MyTripsListAdapter getMyHistoricalTripsListAdapterInstance(Context context, ArrayList<Trip> resource) {
        if (myHistoricalTripsListAdapter == null) {
            myHistoricalTripsListAdapter = new MyTripsListAdapter(context, resource);
        }
        return myHistoricalTripsListAdapter;
    }
}
