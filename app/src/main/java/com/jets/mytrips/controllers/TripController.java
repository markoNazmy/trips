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
    private Context context;
    private JSONParser jsonParser;

    private TripController(Context context) {
        this.context = context;
        jsonParser = JSONParser.getInstance();
    }

    public static synchronized TripController getInstance(Context context) {
        if (tripController == null) {
            tripController = new TripController(context);
        }
        return tripController;
    }

    public void synchronizeUserTrips(final ArrayList<Trip> trips, final VolleyCallback callback) {
        String url = "http://10.118.50.95:8081/MyTripsBackend/SynchronizeServlet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        callback.onSuccess(true);
                    } else {
                        callback.onFailure("Something wrong happened");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure("Something wrong happened");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("json", jsonParser.convertTripsToJsonString(trips));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }
}
