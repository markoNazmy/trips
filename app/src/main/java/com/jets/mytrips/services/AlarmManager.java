package com.jets.mytrips.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.jets.mytrips.activities.AddOrEditTrip;
import com.jets.mytrips.beans.Trip;

/**
 * Created by markoiti on 19/03/17.
 */

public class AlarmManager {

    public static void setTask(Trip trip , Context mContext){
        Intent intent = new Intent(mContext,AddOrEditTrip.class);
        intent.putExtra("trip",trip);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,1111,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.AlarmManager am = (android.app.AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(android.app.AlarmManager.RTC_WAKEUP,846,pendingIntent);
        }
    }

    public static void deleteTask(int tripId ,Context mContext){
        Intent intent = new Intent(mContext, AddOrEditTrip.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,tripId,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.AlarmManager am = (android.app.AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
