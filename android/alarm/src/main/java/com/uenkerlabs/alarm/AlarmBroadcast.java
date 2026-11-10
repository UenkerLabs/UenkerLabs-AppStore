package com.uenkerlabs.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public final class AlarmBroadcast extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent intent){
        if(intent==null||!"com.uenkerlabs.alarm.FIRE".equals(intent.getAction()))return;
        int id=intent.getIntExtra("alarm_id",-1);
        if(id<0)return;
        String title="Wecker";
        for(AlarmStore.Item alarm:AlarmStore.all(c)){
            if(alarm.id==id){title=alarm.label;break;}
        }
        try{AlarmStore.fired(c,id);}catch(Exception e){Log.e("UenkerLabsAlarm","Repeat scheduling failed",e);}
        Intent service=new Intent(c,AlarmAudioService.class);
        service.setAction("RING");service.putExtra("alarm_id",id);service.putExtra("label",title);
        try {
            if(Build.VERSION.SDK_INT>=26)c.startForegroundService(service);
            else c.startService(service);
        }catch(RuntimeException e){Log.e("UenkerLabsAlarm","Foreground alarm service not started",e);}
    }
}
