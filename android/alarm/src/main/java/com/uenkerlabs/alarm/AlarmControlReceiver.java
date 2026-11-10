package com.uenkerlabs.alarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class AlarmControlReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent i){
        if(i==null)return;
        String action=i.getAction();
        if(!"DISMISS".equals(action)&&!"SNOOZE".equals(action))return;
        if("SNOOZE".equals(action)){
            try{AlarmStore.quick(c,10);}
            catch(Exception ex){android.util.Log.e("UenkerLabsAlarm","Snooze scheduling failed",ex);}
        }
        Intent stop=new Intent(c,AlarmAudioService.class);
        stop.setAction("STOP");c.stopService(stop);
        NotificationManager manager=c.getSystemService(NotificationManager.class);
        if(manager!=null)manager.cancel(AlarmAudioService.NOTIFICATION_ID);
    }
}
