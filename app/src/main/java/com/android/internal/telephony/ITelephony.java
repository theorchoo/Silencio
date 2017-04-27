package com.android.internal.telephony;

/**
 * Created by ordagan on 27/04/2017.
 */

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}
