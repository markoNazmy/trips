package com.jets.mytrips.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jets.mytrips.R;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.controllers.TripController;
import com.jets.mytrips.database.DBAdapter;
import com.jets.mytrips.services.MyTripsListAdapter;
import com.jets.mytrips.services.TripListData;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    DBAdapter dbAdapter;
    SwipeMenuListView trips_list;
    SwipeMenuCreator creator;
    MyTripsListAdapter myHistoricalTripsListAdapter;
    int listPosition;
    ArrayList<Trip> trips = TripListData.getHistoricalTripsListInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        trips_list = (SwipeMenuListView) findViewById(R.id.trips_list);

        dbAdapter = new DBAdapter(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);

        /* Clear trips list in case of rotation */
        trips.clear();
        trips.addAll(dbAdapter.getHistoricalUserTrips(sharedPreferences.getInt("id", -1)));

        myHistoricalTripsListAdapter = TripListData.getMyHistoricalTripsListAdapterInstance(getApplicationContext(), trips);
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "Delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth((200));
                // set item title
                deleteItem.setTitle("Delete");
                // set item title font size
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        trips_list.setMenuCreator(creator);

        trips_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HistoryActivity.this, TripDetails.class);
                intent.putExtra("HistoryTripDetails", trips.get(position));
                startActivity(intent);
            }
        });

        trips_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Intent intent;
                switch (index) {
                    case 0:
                        // delete
                        Trip deletedTrip = trips.get(listPosition);
                        deletedTrip.setStatus("deleted");
                        dbAdapter.updateTrip(deletedTrip);
                        // User trips is now asynchronous
                        TripController.getInstance(HistoryActivity.this).setUserTripsSynchronized(false);
                        // Remove trip from the list of upcoming trips
                        trips.remove(listPosition);
                        myHistoricalTripsListAdapter.notifyDataSetChanged();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        trips_list.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                listPosition = position;

            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        trips_list.setAdapter(myHistoricalTripsListAdapter);
    }
}
