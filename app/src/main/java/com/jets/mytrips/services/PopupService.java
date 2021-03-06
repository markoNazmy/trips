package com.jets.mytrips.services;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jets.mytrips.R;
import com.jets.mytrips.activities.CurrentTripsActivity;
import com.jets.mytrips.activities.ReminderActivity;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.database.DBAdapter;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rocke on 3/31/2017.
 */

public class PopupService extends Service {

    String TAG = PopupService.class.getSimpleName();
    WindowManager mWindowManager;
    View mView;

    Button btnCancel;
    Button btnStart;
    Button btnLater;

    TextView tripName;
    TextView tripDest;
    TextView tripNotes;

    Notification.Builder builder;
    Notification n;
    NotificationManager notificationManager ;

    Trip trip;
    DBAdapter dba;

    Ringtone alarmSound;
    Vibrator vib;
    boolean isSilent = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            trip = intent.getParcelableExtra("trip");
            showDialog("Your Trip is Starting!");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showDialog(String aTitle) {

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP), "MessageReader");
        mWakeLock.acquire();
        mWakeLock.release();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mView = View.inflate(getApplicationContext(), R.layout.activity_reminder, null);
        mView.setTag(TAG);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        alarmSound = RingtoneManager.getRingtone(getApplicationContext(), uri);

        vib = (Vibrator)getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        AudioManager am = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                isSilent = true;
                Log.i("myTag", "------phone is silent");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                isSilent = true;
                Log.i("myTag", "------phone is vibrate mode");
                break;
        }

        if(isSilent) {
            long pattern[] = {60,120,180,240,300,360,420,480};
            vib.vibrate(pattern, 5);
        }
        else {
            alarmSound.play();
        }

        btnCancel = (Button) mView.findViewById(R.id.btnCancel);
        btnStart = (Button) mView.findViewById(R.id.btnStart);
        btnLater = (Button) mView.findViewById(R.id.btnLater);

        KeyguardManager kgMgr =
                (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean showing = kgMgr.inKeyguardRestrictedInputMode();
        if(showing)
            btnStart.setEnabled(false); //user can;t start maps from lock screen

        tripName = (TextView) mView.findViewById(R.id.tripName);
        tripDest = (TextView) mView.findViewById(R.id.tripDest);
        tripNotes = (TextView) mView.findViewById(R.id.tripNotes);

        System.out.println(trip.getName());
        tripName.setText(trip.getName());
        tripDest.setText(trip.getStart() + " - " + trip.getEnd());

        dba = new DBAdapter(this);
        trip.setNotes(dba.getTripNotes(trip.getId()));

        if(trip.getNotes().size() > 0)
            tripNotes.setText("Remember to: \n");

        for (int i = 0; i < trip.getNotes().size(); i++){
            tripNotes.setText(tripNotes.getText().toString() + trip.getNotes().get(i).getNote() + "\n");
        }

        dba = new DBAdapter(getApplicationContext());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trip.setStatus("cancelled");
                dba.updateTrip(trip);

                //remove cancelled trip
                ArrayList<Trip> trips = TripListData.getUpcomingTripsListInstance();
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).remove(trip);
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).notifyDataSetChanged();

                alarmSound.stop();
                vib.cancel();
                hideDialog();
                refreshList();
                stopSelf();
                if (notificationManager!=null){
                    notificationManager.cancel(trip.getAlarmId());
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("google.navigation:q=" + trip.getEnd() + "&mode=d");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //remove old object from adapter
                ArrayList<Trip> trips = TripListData.getUpcomingTripsListInstance();
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).remove(trip);

                trip.setStatus("done");
                trip.setDone(1);
                dba.updateTrip(trip);

                //replace old object with new object
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).add(trip);
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).notifyDataSetChanged();

                alarmSound.stop();
                vib.cancel();
                hideDialog();
                refreshList();
                stopSelf();
                if (notificationManager!=null){
                    notificationManager.cancel(trip.getAlarmId());
                }
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListenerClass.class);
                intent.putExtra("trip", trip);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        (new Random().nextInt(100000 - 5) + 5), intent, PendingIntent.FLAG_UPDATE_CURRENT);

               builder = new Notification.Builder(PopupService.this)
                        .setContentTitle("Trips")
                        .setContentText("Your trip is pending, click to start..")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.map)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    n = builder.build();
                } else {
                    n = builder.getNotification();

                }

                n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                }
                notificationManager.notify(trip.getAlarmId(), n);

                //remove old object from adapter
                ArrayList<Trip> trips = TripListData.getUpcomingTripsListInstance();
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).remove(trip);

                trip.setStatus("later");
                dba.updateTrip(trip);

                //replace old object with new object
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).add(trip);
                TripListData.getMyTripsListAdapterInstance(mView.getContext(), trips).notifyDataSetChanged();

                alarmSound.stop();
                vib.cancel();
                hideDialog();
                refreshList();
            }
        });

        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
                PixelFormat.RGBA_8888);

        mView.setVisibility(View.VISIBLE);
        mWindowManager.addView(mView, mLayoutParams);
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    private void hideDialog(){
        if(mView != null && mWindowManager != null){
            mWindowManager.removeView(mView);
            mView = null;
        }
    }
    void refreshList(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
        TripListData.getUpcomingTripsListInstance().clear();
        TripListData.getUpcomingTripsListInstance().addAll(new DBAdapter(getApplicationContext()).getUpcomingUserTrips(sharedPreferences.getInt("id", -1)));
        TripListData.getMyTripsListAdapterInstance(getApplicationContext(), TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();
        TripListData.getHistoricalTripsListInstance().clear();
        TripListData.getHistoricalTripsListInstance().addAll(new DBAdapter(getApplicationContext()).getHistoricalUserTrips(sharedPreferences.getInt("id", -1)));
        TripListData.getMyHistoricalTripsListAdapterInstance(getApplicationContext(),TripListData.getHistoricalTripsListInstance()).notifyDataSetChanged();
    }
    @Override
    public void onDestroy() {
        System.out.println("service on destroyyyyyyyyyyyyyyyyyyyyyy");

        super.onDestroy();


    }
}
