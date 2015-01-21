package ru.dsoft38.smssender.sms_sender;

/**
 * Created by user on 21.01.2015.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeliverySms extends BroadcastReceiver {

    private final static String MY_TAG = "MyTag";

    public void onReceive(Context context, Intent intent) {
        switch(getResultCode()) {
            case Activity.RESULT_OK:
                Log.i(MY_TAG, "SMS delivered");
                break;
            case Activity.RESULT_CANCELED:
                Log.i(MY_TAG, "SMS not delivered");
                break;
        }
    }

}