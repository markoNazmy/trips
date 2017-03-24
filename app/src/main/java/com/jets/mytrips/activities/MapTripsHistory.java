package com.jets.mytrips.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jets.mytrips.R;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.database.DBAdapter;
import com.jets.mytrips.services.MyTripsListAdapter;
import com.jets.mytrips.services.RootParser;
import com.jets.mytrips.services.TripListData;
import com.jets.mytrips.services.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MapTripsHistory extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    VolleySingleton singleton;
    RequestQueue requestQueue;
    RootParser rootParser;
    ArrayList<Trip> trips;
    Double latitudeSource, longitudeSource, latitudeDest, longitudeDest;

    public MapTripsHistory(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_trips_history);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);

        singleton = VolleySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();
        rootParser = new RootParser();
        trips = new DBAdapter(getApplicationContext()).getUserTrips(sharedPreferences.getInt("id", -1));
        trips.remove(0);
        for (Trip trip : trips) {

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + trip.getStartX() + "," + trip.getStartY() + "&destination=" + trip.getEndX() + "," + trip.getEndY() + "&key=AIzaSyA40U80aJecaH-sS08dlG4smR_Tlbjb2TA";
            draw(url);
        }
    }
        public void draw(String url) {
            final PolylineOptions lineOptions;
            lineOptions = new PolylineOptions();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("success");
                    System.out.println(response);
                    System.out.println(rootParser.parse(response));
                    List<List<HashMap<String, String>>> roots = rootParser.parse(response);
                    // Traversing through all the routes
                    for (int i = 0; i < roots.size(); i++) {
                        ArrayList<LatLng> points = new ArrayList<>();
                        // Fetching i-th route
                        List<HashMap<String, String>> path = roots.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            if (j == 0) {
                                latitudeSource = lat;
                                longitudeSource = lng;
                            }
                            if (j == path.size() - 1) {
                                latitudeDest = lat;
                                longitudeDest = lng;
                            }
                            System.out.println("Lat and lang    >" + lat + "    " + lng);
                            points.add(position);
                        }
//                        System.out.println("gwa el function     >" + points.get(2).longitude);
                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(8);
                        lineOptions.color(new Random().nextInt()+100);
                        System.out.println("ana hena done gedan we kolo zy el fol");
                    }
                    mMap.addPolyline(lineOptions);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeSource, longitudeSource)).title("start point"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeDest, longitudeDest)).title("end point"));
                    Toast.makeText(MapTripsHistory.this,"Success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error   >" + error.getMessage());
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            });
            singleton.addToRequestQueue(jsonObjectRequest);
        }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
