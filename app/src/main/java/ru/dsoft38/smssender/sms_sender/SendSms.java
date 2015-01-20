package ru.dsoft38.smssender.sms_sender;

/**
 * Created by user on 20.01.2015.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SendSms extends BroadcastReceiver {

    private final static String MY_TAG = "MyTag";

    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.i(MY_TAG, "SMS send");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.i(MY_TAG, "unknown problems");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.i(MY_TAG, "modul is down");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.i(MY_TAG, "PDU error");
                break;
        }
    }
}

