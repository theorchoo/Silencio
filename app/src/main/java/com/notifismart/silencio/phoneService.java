package com.notifismart.silencio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class phoneService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(CustomPhoneStateListener,);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");
            Log.d("INCOMING", phoneNumber);
            if ((phoneNumber != null) && phoneNumber.equals("0546522802")) {
                Log.d("SILENCING", phoneNumber);
                telephonyService.silenceRinger();
                final AudioManager mode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                telephonyService.endCall();
//                Log.d("HANG UP", phoneNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class CustomPhoneStateListener extends PhoneStateListener {
        Context context;

        public CustomPhoneStateListener(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String callingNumber) {
            super.onCallStateChanged(state, callingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    resumeSound();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                default:
                    break;
            }
        }

        public void resumeSound() {
            final AudioManager mode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }
}