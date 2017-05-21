package com.jets.mytrips.controllers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.services.JSONParser;
import com.jets.mytrips.services.VolleyCallback;
import com.jets.mytrips.services.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aya on 3/21/17.
 */

public class TripController {
    private static TripController tripController;
    private VolleySingleton volleySingleton;
    private JSONParser jsonParser;
    private boolean isUserTripsSynchronized;

    private TripController(Context context) {
        volleySingleton = VolleySingleton.getInstance(context);
        jsonParser = JSONParser.getInstance();
        isUserTripsSynchronized = false;
    }

    public boolean isUserTripsSynchronized() {
        return isUserTripsSynchronized;
    }

    public void setUserTripsSynchronized(boolean userTripsSynchronized) {
        isUserTripsSynchronized = userTripsSynchronized;
    }

    public static synchronized TripController getInstance(Context context) {
        if (tripController == null) {
            tripController = new TripController(context);
        }
        return tripController;
    }

    public void synchronizeUserTrips(final ArrayList<Trip> trips, final VolleyCallback callback) {
        String url = "https://thawing-anchorage-30033.herokuapp.com/SynchronizeServlet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        callback.onSuccess(true);
                    } else {
                        callback.onFailure("Something wrong happened!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(volleySingleton.identifyVolleyErrors(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("json", jsonParser.convertTripsToJsonString(trips));
                return params;
            }
        };

        volleySingleton.addToRequestQueue(stringRequest);
    }

    public void getUserTrips(final int userId, final VolleyCallback callback) {
        String url = "https://thawing-anchorage-30033.herokuapp.com/GetTripsServlet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("{}")) {
                    ArrayList<Trip> trips = jsonParser.getUserTripsFromJsonString(response);
                    callback.onSuccess(trips);
                    return;
                }
                callback.onFailure("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(volleySingleton.identifyVolleyErrors(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(userId));
                return params;
            }
        };

        volleySingleton.addToRequestQueue(stringRequest);
    }
}
