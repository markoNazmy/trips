package com.jets.mytrips.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.jets.mytrips.activities.AddOrEditTrip;
import com.jets.mytrips.activities.CurrentTripsActivity;
import com.jets.mytrips.activities.ReminderActivity;
import com.jets.mytrips.beans.Trip;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by markoiti on 19/03/17.
 */

public class AlarmManager {

    public static void setTask(Trip trip , Context mContext , long timeInMilies){

        Intent intent = new Intent(mContext, ListenerClass.class);
        //intent.setAction("tripBegins");
        intent.putExtra("trip", trip);

        Log.i("myTag", "---------Alarm ID: " + trip.getAlarmId());
        Log.i("myTag", "---------Time in Millies: " + timeInMilies);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, trip.getAlarmId(),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.AlarmManager am = (android.app.AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInMilies, pendingIntent);
        }
    }

    public static void deleteTask(int alarmId ,Context mContext){
        Intent intent = new Intent(mContext, ListenerClass.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,alarmId,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.AlarmManager am = (android.app.AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
