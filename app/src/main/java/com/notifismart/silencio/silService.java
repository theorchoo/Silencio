package com.notifismart.silencio;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class silService extends NotificationListenerService {
    Context context;
    static int threshold = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    static public void setThreshold(int t) {
        threshold = t;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        Notification notification = sbn.getNotification();
        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence text_sub = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        Log.i("app", sbn.getPackageName());

        String dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(new Date(sbn.getPostTime()));

        Log.i("time", dateString);
        Log.i("title", title);

        if (text != null) {
            Log.i("text", text.toString());
        }

        if (text_sub != null) {
            Log.i("text", text_sub.toString());
        }

        try {
            String people[] = extras.getStringArray(Notification.EXTRA_PEOPLE);
            for (String p : people) {
                Log.i("person",p);
            }
        } catch (Exception e) {}

        try {
            Log.i("group", notification.getGroup());
        } catch (Exception e) {}

        Log.i("priority", String.valueOf(sbn.getNotification().priority));

//        if (text.contains("cool")) {
//            cancelNotification(sbn.getKey());
//        }
        StatusActivity.pushup();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("removed",sbn.getNotification().extras.getString("android.title"));
    }

}
