package com.notifismart.silencio;

import android.*;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class StatusActivity extends Activity {
    Context context;
    RequestQueue queue;
    // Instantiate the RequestQueue.
    String url;
    ArrayList<String> message;
    static int threshold = 0;
    static int rejCount = 0;
    static final double factor = 23.15;
    static StatusActivity currentActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        currentActivity = this;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringArrayListExtra("GMAIL");

        url = "http://10.10.17.210:5000/";
        queue = Volley.newRequestQueue(this);

        context = getApplicationContext();
        printCalls(context);

        NotificationCompat.Builder mBuilder = constantNotification();
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        onNewIntent(getIntent());


    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }

    static public void setThreshold(int t) {
        threshold = t;
        TextView tv = (TextView) currentActivity.findViewById(R.id.textView9);
        tv.setText(String.valueOf(t));
    }

    static public void pushup() {
        rejCount++;
        TextView num = (TextView) currentActivity.findViewById(R.id.textView3);
        TextView time = (TextView) currentActivity.findViewById(R.id.textView6);
        num.setText(String.valueOf(rejCount));
        time.setText(String.valueOf(rejCount * factor).concat(" MIN."));
    }

    private NotificationCompat.Builder constantNotification(){

//
//        // create action buttons to broadcast result (level choosing to be translated to thold)
//        Intent lowIntent = new Intent(this, LevelReceiver.class);
//        lowIntent.setAction("MSG");
//        PendingIntent piAction1 = PendingIntent.getService(context, 0, lowIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////        PendingIntent lowPIntent = PendingIntent.getBroadcast(this, 0, lowIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////        NotificationCompat.Action lowAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.low_level), lowPIntent).build();
//
//        Intent medIntent = new Intent(this, LevelReceiver.class);
//        PendingIntent medPIntent = PendingIntent.getBroadcast(this, 0, medIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action medAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.med_level), medPIntent).build();

//        Intent highIntent = new Intent(this, LevelReceiver.class);
//        PendingIntent highPIntent = PendingIntent.getBroadcast(this, 0, highIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action highAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.high_level), highPIntent).build();
//
//        Intent offIntent = new Intent(this, LevelReceiver.class);
//        PendingIntent offPIntent = PendingIntent.getBroadcast(this, 0, offIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action offAction = new NotificationCompat.Action.Builder(R.drawable.ic_highlight_off, getString(R.string.off), offPIntent).build();


        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_highlight_off)
                        .setContentTitle("SILENCIO!")
                        .setContentText("Pick noise filtering level")
                        .setOngoing(true)
                        .setPriority(2)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launch))
                        .setSmallIcon(R.drawable.ic_launch);

        Intent low = new Intent();
        low.setAction("1");
        PendingIntent pendingIntentLow = PendingIntent.getBroadcast(this, 12345, low, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_highlight_off, "Low", pendingIntentLow);

        Intent medium = new Intent();
        medium.setAction("2");
        PendingIntent pendingIntentMedium = PendingIntent.getBroadcast(this, 12345, medium, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_highlight_off, "Medium", pendingIntentMedium);

        Intent high = new Intent();
        high.setAction("3");
        PendingIntent pendingIntentHigh = PendingIntent.getBroadcast(this, 12345, high, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_highlight_off, "High", pendingIntentHigh);

        Intent off = new Intent();
        off.setAction("4");
        PendingIntent pendingIntentOff = PendingIntent.getBroadcast(this, 12345, off, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_highlight_off, "OFF", pendingIntentOff);

        mBuilder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2,3));
        return mBuilder;
    }

//
//    @Override
//    public void onNewIntent(Intent intent){
//        Log.i("got",intent.getAction());
//    }


    private void printCalls(Context context) {
        Cursor managedCursor;
        int permissionCheck = context.checkSelfPermission(android.Manifest.permission.READ_CALL_LOG);

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
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("calls",jsonArray_master);
            toSend.put("gmail",message);
        } catch (Exception e) {

        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, toSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ",response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    public int predict(float[] x, float[][] w) {
        float final_arr[] = {0,0,0};

        int j = 0;
        for (float[] w_row : w) {
            int i = 0;
            for (float w_i : w_row) {
                final_arr[j] += w_i * x[i];
                i++;
            }
            j++;
        }

        if (final_arr[0] > final_arr[1]) {
            if (final_arr[2] > final_arr[3]) {
                if (final_arr[0] > final_arr[2]) {
                    return 0;
                } else {
                    return 2;
                }
            } else {
                if (final_arr[0] > final_arr[3]) {
                    return 0;
                } else {
                    return 3;
                }
            }
        } else {
            if (final_arr[2] > final_arr[3]) {
                if (final_arr[1] > final_arr[2]) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (final_arr[1] > final_arr[3]) {
                    return 1;
                } else {
                    return 3;
                }
            }
        }
    }
}
