package com.jets.mytrips.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jets.mytrips.R;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.database.DBAdapter;
import com.jets.mytrips.services.ListenerClass;

public class ReminderActivity extends AppCompatActivity {

    Button btnCancel;
    Button btnStart;
    Button btnLater;

    TextView tripName;
    TextView tripDest;
    TextView tripNotes;

    Trip trip;
    DBAdapter dba;

    Ringtone alarmSound;
    Vibrator vib;
    boolean isSilent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_reminder);

        trip = getIntent().getParcelableExtra("trip");
        Log.i("----myTag", "Trip Name: " + trip.getName());

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//
//        getWindow().setLayout((int)(width*0.90), (int)(height*0.50));

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        alarmSound = RingtoneManager.getRingtone(getApplicationContext(), uri);

        vib = (Vibrator)getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        AudioManager am = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "MessageReader");
        wakeLock.acquire();

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

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnLater = (Button) findViewById(R.id.btnLater);

        tripName = (TextView) findViewById(R.id.tripName);
        tripDest = (TextView) findViewById(R.id.tripDest);
        tripNotes = (TextView) findViewById(R.id.tripNotes);

        tripName.setText(trip.getName());
        tripDest.setText(trip.getStart() + " - " + trip.getEnd());

        dba = new DBAdapter(this);
        trip.setNotes(dba.getTripNotes(trip.getId()));

        if(trip.getNotes().size() > 0)
            tripNotes.setText("Remember to: \n");

        for (int i = 0; i < trip.getNotes().size(); i++){
            tripNotes.setText(tripNotes.getText().toString() + trip.getNotes().get(i).getNote() + "\n");
        }

        dba = new DBAdapter(ReminderActivity.this);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: handle in list and move to history
                trip.setStatus("cancelled");
                dba.updateTrip(trip);
                alarmSound.stop();
                vib.cancel();
                wakeLock.release();
                finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("google.navigation:q=" + trip.getEnd() + "&mode=d");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

                trip.setStatus("done");
                dba.updateTrip(trip);
                alarmSound.stop();
                vib.cancel();
                wakeLock.release();
                finish();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListenerClass.class);
                intent.putExtra("trip", trip);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        trip.getAlarmId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(ReminderActivity.this)
                        .setContentTitle("Trips")
                        .setContentText("Your trip is pending, click to start..")
                        .setContentIntent(pendingIntent)
                      //  .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                Notification n;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    n = builder.build();
                } else {
                    n = builder.getNotification();
                }

                n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

                NotificationManager notificationManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                }
                notificationManager.notify(trip.getAlarmId(), n);

                trip.setStatus("later");
                dba.updateTrip(trip);
                alarmSound.stop();
                vib.cancel();
                wakeLock.release();
                finish();
            }
        });
    }
}
