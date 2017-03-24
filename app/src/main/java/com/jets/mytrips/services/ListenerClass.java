package com.jets.mytrips.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.jets.mytrips.activities.ReminderActivity;
import com.jets.mytrips.beans.Trip;

/**
 * Created by rocke on 3/22/2017.
 */

public class ListenerClass extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("myTag", "--------------------I AM LISTENING!!");

        Trip trip = intent.getParcelableExtra("trip");

        Intent startInt = new Intent(context, ReminderActivity.class);
        startInt.putExtra("trip", trip);
        startInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startInt);
    }
}
