package com.jets.mytrips.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jets.mytrips.services.VolleyCallback;

import java.util.ArrayList;

public class CurrentTripsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DBAdapter dbAdapter;
    SwipeMenuListView trips_list;
    SwipeMenuCreator creator;
    MyTripsListAdapter myTripsListAdapter;
    int listPosition;
    ArrayList<Trip> trips = TripListData.getUpcomingTripsListInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curr_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(),AddOrEditTrip.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //        JSONParser jsonParser = JSONParser.getInstance();
//
//        String url = "http://192.168.1.2:8080/MyTripsBackend/SynchronizeServlet";
//
//        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
        trips_list = (SwipeMenuListView) findViewById(R.id.trips_list);
        dbAdapter = new DBAdapter(getBaseContext());
//        Trip trip = new Trip();
//        trip.setId(1);
//        trip.setUserId(1);
//        trip.setStart("start");
//        trip.setStartCoord("startCoord");
//        trip.setStart("end");
//        trip.setStartCoord("endCoord");
//        trip.setDate("date");
//        trip.setTime("time");
//        trip.setStatus("upcoming");
//        dbAdapter.addTrip(trip);


        ////////////////////////////
        SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
        /* Clear trips list in case of rotation */
        trips.clear();
        trips.addAll(dbAdapter.getUpcomingUserTrips(sharedPreferences.getInt("id", -1)));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username)).setText(getSharedPreferences("MyTrips", MODE_PRIVATE).getString("fullName", ""));

//        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrr"+trips.get(0).getStart());
//        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrr"+trips.get(0).getEnd());
//        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrr"+trips.get(1).getStart());
//        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrr"+trips.get(1).getEnd());
        myTripsListAdapter = new MyTripsListAdapter(getApplicationContext(), trips);
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "Start" item
                SwipeMenuItem startItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                startItem.setBackground(new ColorDrawable(Color.rgb(0xFE, 0xC5,
                        0xB0)));
                // set item width
                startItem.setWidth((200));
                // set item title
                startItem.setTitle("Start");
                // set item title font size
                startItem.setTitleSize(18);
                // set item title font color
                startItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(startItem);

                // create "Edit" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth((200));
                // set item title
                openItem.setTitle("Edit");
                // set item title font size
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);


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
                Intent intent = new Intent(CurrentTripsActivity.this, TripDetails.class);
                intent.putExtra("TripDetails", trips.get(position));
                CurrentTripsActivity.this.startActivity(intent);
            }
        });

        trips_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Intent intent;
                switch (index) {
                    case 0:
                        // Start
                        Uri uri = Uri.parse("google.navigation:q=" + trips.get(listPosition).getEnd() + "&mode=d");
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                        break;
                    case 1:
                        // Edit
                        intent = new Intent(CurrentTripsActivity.this, AddOrEditTrip.class);
                        intent.putExtra("tripPositionAtList", listPosition);
                        startActivity(intent);
                        break;
                    case 2:
                        // delete
                        Trip deletedTrip = trips.get(listPosition);
                        deletedTrip.setStatus("deleted");
                        dbAdapter.updateTrip(deletedTrip);
                        // User trips is now asynchronous
                        TripController.getInstance(CurrentTripsActivity.this).setUserTripsSynchronized(false);
                        // Remove trip from the list of upcoming trips
                        trips.remove(listPosition);
                        myTripsListAdapter.notifyDataSetChanged();
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

        trips_list.setAdapter(myTripsListAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();


        for (Trip trip : trips) {

            System.out.println("gggggggggggggggggggggggggggggg" + trip.getName());

        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.curr_trips, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_synchronize) {
            synchronizeUserTrips();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {

        } else if (id == R.id.nav_map_history) {
            Intent intent = new Intent(getBaseContext(),MapTripsHistory.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_trip) {
            startActivity(new Intent(getBaseContext(), AddOrEditTrip.class));
        } else if (id == R.id.nav_synchronize) {
            synchronizeUserTrips();
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = getSharedPreferences("MyTrips", MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            dbAdapter.logout();
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void synchronizeUserTrips() {
        final TripController tripController = TripController.getInstance(this);

        if (!tripController.isUserTripsSynchronized()) {
            final ArrayList<Trip> trips = dbAdapter.getUserTrips(getSharedPreferences("MyTrips", MODE_PRIVATE).getInt("id", -1));
            if (!trips.isEmpty()) {
                for (Trip trip : trips) {
                    trip.setNotes(dbAdapter.getTripNotes(trip.getId()));
                }
                tripController.synchronizeUserTrips(trips, new VolleyCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        for (Trip trip : trips) {
                            if (trip.getStatus().equals("deleted")) {
                                dbAdapter.deleteTrip(trip.getId());
                            }
                        }
                        tripController.setUserTripsSynchronized(true);
                        Toast.makeText(CurrentTripsActivity.this, "Your data has been synchronized successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CurrentTripsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            if (trips.isEmpty()) {
                Toast.makeText(CurrentTripsActivity.this, "There is no data to synchronize", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CurrentTripsActivity.this, "Your data is already synchronous", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
