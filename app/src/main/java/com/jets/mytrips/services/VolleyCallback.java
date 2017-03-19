package com.jets.mytrips.services;

import com.jets.mytrips.beans.User;

/**
 * Created by Aya on 3/19/17.
 */

public interface VolleyCallback {

    void onSuccess(User user);

    void onFailure(String errorMessage);
}
