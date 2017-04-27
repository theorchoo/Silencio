package com.notifismart.silencio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by May on 28/04/2017.
 */

public class LevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RECEIVED",intent.getAction());
    }

}
