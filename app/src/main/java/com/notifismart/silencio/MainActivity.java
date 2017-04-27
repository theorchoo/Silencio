package com.notifismart.silencio;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        askForPermission(Manifest.permission.READ_CALL_LOG,1);
        askForPermission(Manifest.permission.READ_PHONE_STATE,2);
        askForPermission(Manifest.permission.CALL_PHONE,2);
        askForPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS,2);
        // TODO get notification and call mbuilder.build()
    }

    private NotificationCompat.Builder constantNotification(){
        // create action buttons to broadcast result (level choosing to be translated to thold)
        Intent lowIntent = new Intent(this, LevelReceiver.class);
        PendingIntent lowPIntent = PendingIntent.getBroadcast(this, 0, lowIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action lowAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.low_level), lowPIntent).build();

        Intent medIntent = new Intent(this, LevelReceiver.class);
        PendingIntent medPIntent = PendingIntent.getBroadcast(this, 0, medIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action medAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.med_level), medPIntent).build();

        Intent highIntent = new Intent(this, LevelReceiver.class);
        PendingIntent highPIntent = PendingIntent.getBroadcast(this, 0, highIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action highAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.high_level), highPIntent).build();

        Intent offIntent = new Intent(this, LevelReceiver.class);
        PendingIntent offPIntent = PendingIntent.getBroadcast(this, 0, offIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action offAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.off), offPIntent).build();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("SILENCIO!")
                        .setContentText("Pick noise filtering level")
                        .setOngoing(true)
                        .setPriority(2)
                        .addAction(lowAction).addAction(medAction).addAction(highAction).addAction(offAction)
                        .setStyle(new android.support.v7.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2))
                ;

//                .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));
        return mBuilder;
    }
    private void printCalls() {
        TextView textView = (TextView) findViewById(R.id.textView);
        Cursor managedCursor;
        int permissionCheck = context.checkSelfPermission(Manifest.permission.READ_CALL_LOG);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i("permission","Good");
            managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        } else {
            Log.i("permission","not Good");
            return;
        }
        JSONArray jsonArray_master = new JSONArray();

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        while (managedCursor.moveToNext()) {
            JSONArray jsonArray = new JSONArray();

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String pName = managedCursor.getString(name);

            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);

            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            jsonArray.put(callDayTime.toString());
            jsonArray.put(dir);
            jsonArray.put(phNumber);
            jsonArray.put(pName);
            jsonArray.put(callDuration);

            Log.i("json",jsonArray.toString());
            jsonArray_master.put(jsonArray);
        }
        managedCursor.close();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            printCalls();
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


}

