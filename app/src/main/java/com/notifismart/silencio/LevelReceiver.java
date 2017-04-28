package com.notifismart.silencio;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by May on 28/04/2017.
 */

public class LevelReceiver extends BroadcastReceiver {

//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public LevelReceiver(String name) {
//        super(name);
//    }
//
//    @Override
//    public void onHandleIntent(Intent intent) {
//        Log.i("IN", "ININ");
//        final String action = intent.getAction();
//        Bundle extras = intent.getExtras();
//        if(extras != null){
//            String data1 = extras.getString("data1");
//            String data2 = extras.getString("data2");
//            Log.e("Ddata1" + data1, data2);
//        }        if (action.equals("MSG")) {
//            Log.i("BLABLA","BLABLABLA");
//        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if("1".equals(action)) {
            StatusActivity.setThreshold(1);
            phoneService.setThreshold(1);
            silService.setThreshold(1);
        } else if("2".equals(action)) {
            StatusActivity.setThreshold(2);
            phoneService.setThreshold(2);
            silService.setThreshold(2);
        } else if("3".equals(action)) {
            StatusActivity.setThreshold(3);
            phoneService.setThreshold(3);
            silService.setThreshold(3);
        } else if("0".equals(action)) {
            StatusActivity.setThreshold(0);
            phoneService.setThreshold(0);
            silService.setThreshold(0);
        }

    }
}
