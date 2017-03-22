package com.jets.mytrips.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jets.mytrips.beans.User;
import com.jets.mytrips.services.JSONParser;
import com.jets.mytrips.services.VolleyCallback;
import com.jets.mytrips.services.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aya on 3/17/17.
 */

public class UserController {

    private static UserController userController;
    private Context context;
    private JSONParser jsonParser;

    private UserController(Context context) {
        this.context = context;
        jsonParser = JSONParser.getInstance();
    }

    public static synchronized UserController getInstance(Context context) {
        if (userController == null) {
            userController = new UserController(context);
        }
        return userController;
    }

    public void registerUser(final User user, final VolleyCallback callback) {
        String url = "http://10.118.50.95:8081/MyTripsBackend/RegisterServlet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("Email already exists");
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
                params.put("json", jsonParser.prepareUserJsonString(user));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void login(final String email, final String password, final VolleyCallback callback) {
        String url = "http://10.118.50.95:8081/MyTripsBackend/LoginServlet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("{}")) {
                    User user = jsonParser.getUserFromJsonSting(response);

                    // Save user data in preferences file
                    saveUserSession(user);

                    callback.onSuccess(user);
                } else {
                    callback.onFailure("Invalid email or password");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(), "Something wrong happened", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("json", jsonParser.prepareJsonStringForSignIn(email, password));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void saveUserSession(User user) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MyTrips", context.MODE_PRIVATE).edit();
        editor.putInt("id", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("fullName", user.getFullName());
        editor.commit();
    }
}
