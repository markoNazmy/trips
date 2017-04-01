package com.jets.mytrips.services;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import com.jets.mytrips.activities.ReminderActivity;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.database.DBAdapter;

/**
 * Created by rocke on 3/22/2017.
 */

public class ListenerClass extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        final Trip trip = intent.getParcelableExtra("trip");
        Log.i("myTag", "--------------------I AM LISTENING!!" + trip.getName());

        Intent startInt = new Intent(context, ReminderActivity.class);
        startInt.putExtra("trip", trip);
        startInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(startInt);
    }
}
