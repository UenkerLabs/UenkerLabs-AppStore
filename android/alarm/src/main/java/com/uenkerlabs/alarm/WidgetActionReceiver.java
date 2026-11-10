package com.uenkerlabs.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public final class WidgetActionReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent intent){
        if(intent==null||!"QUICK".equals(intent.getAction()))return;
        int minutes=intent.getIntExtra("minutes",5);
        if(minutes!=5&&minutes!=15)return;
        try{
            AlarmStore.quick(c,minutes);
            Toast.makeText(c,minutes+"-Minuten-Wecker gestellt",Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(c,"Wecker konnte nicht gestellt werden",Toast.LENGTH_LONG).show();
        }
    }
}
