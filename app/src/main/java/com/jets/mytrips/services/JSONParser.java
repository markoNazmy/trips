package com.jets.mytrips.services;

import com.jets.mytrips.beans.User;

import org.json.JSONException;
import org.json.JSONObject;

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
}
