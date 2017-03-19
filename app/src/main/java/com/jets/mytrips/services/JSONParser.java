package com.jets.mytrips.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.beans.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Aya on 3/17/17.
 */

public class JSONParser {

    private static JSONParser jsonParser;

    private JSONParser() {
    }

    public static synchronized JSONParser getInstance() {
        if (jsonParser == null) {
            jsonParser = new JSONParser();
        }
        return jsonParser;
    }

    public String prepareUserJsonString(User user) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", user.getEmail());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("fullName", user.getFullName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String prepareJsonStringForSignIn(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public User getUserFromJsonSting(String jsonString) {
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            user = new User(jsonObject.getInt("id"), jsonObject.getString("email"), jsonObject.getString("password"),
                    jsonObject.getString("fullName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
    public ArrayList<Trip> convertJsonArrayToTrips(JSONObject jsonObject) {


        Gson gson = new Gson();
        ArrayList<Trip> trips;
        Type type = new TypeToken<ArrayList<Trip>>(){}.getType();
        trips = gson.fromJson(jsonObject.toString(), type);

        return trips;
    }
}
