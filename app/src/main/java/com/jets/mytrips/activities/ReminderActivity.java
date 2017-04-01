package com.jets.mytrips.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jets.mytrips.R;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.database.DBAdapter;
import com.jets.mytrips.services.ListenerClass;
import com.jets.mytrips.services.PopupService;

public class ReminderActivity extends AppCompatActivity {

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //setContentView(R.layout.activity_reminder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                Trip trip = getIntent().getParcelableExtra("trip");
                Log.i("myTag", "--------------------REMINDER!!" + trip.getName());
                Intent intent = new Intent(this, PopupService.class);
                intent.putExtra("trip", trip);
                startService(intent);
                finish();
            }
            else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "You must enable app overlay in settings..", Toast.LENGTH_LONG);
                }
                else{
                    Trip trip = getIntent().getParcelableExtra("trip");
                    Intent intent = new Intent(this, PopupService.class);
                    intent.putExtra("trip", trip);
                    startService(intent);
                    finish();
                }
            }
        }
    }
}
