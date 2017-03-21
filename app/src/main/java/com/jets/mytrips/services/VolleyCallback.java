package com.jets.mytrips.services;


/**
 * Created by Aya on 3/19/17.
 */

public interface VolleyCallback {

    void onSuccess(Object response);

    void onFailure(String errorMessage);
}
