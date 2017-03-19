package com.jets.mytrips.services;

import android.text.TextUtils;

/**
 * Created by Aya on 3/17/17.
 */

public class Validator {

    private static Validator validator;

    private Validator() {
    }

    public static synchronized Validator getInstance() {
        if (validator == null) {
            validator = new Validator();
        }
        return validator;
    }

    public boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidInput(CharSequence input) {
        return !TextUtils.isEmpty(input);
    }
}
