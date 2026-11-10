package com.uenkerlabs.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class RescheduleReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent i){
        try{AlarmStore.restore(c);}catch(Exception e){Log.e("UenkerLabsAlarm","Cannot restore alarms",e);}
    }
}
