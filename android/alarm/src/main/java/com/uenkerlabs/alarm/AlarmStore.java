package com.uenkerlabs.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class AlarmStore {
    private static final String PREFS="alarms";
    static final class Item {
        int id, hour, minute;
        long at;
        String label;
        boolean daily, enabled;
        Item(int id,int hour,int minute,String label,boolean daily,boolean enabled,long at) {
            this.id=id;this.hour=hour;this.minute=minute;this.label=label;
            this.daily=daily;this.enabled=enabled;this.at=at;
        }
    }
    static List<Item> all(Context context) {
        String data=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getString("items","[]");
        List<Item> out=new ArrayList<>();
        try {
            JSONArray arr=new JSONArray(data);
            for(int i=0;i<arr.length();i++){
                JSONObject x=arr.getJSONObject(i);
                out.add(new Item(x.getInt("id"),x.optInt("hour"),x.optInt("minute"),
                    x.optString("label","Wecker"),x.optBoolean("daily"),x.optBoolean("enabled",true),
                    x.optLong("at")));
            }
        }catch(Exception ignored){ /* Bad user preferences never cause a silent crash. */ }
        out.sort(Comparator.comparingLong(a->nextAt(a)));
        return out;
    }
    static long nextAt(Item i) {
        if(!i.daily)return i.at;
        ZonedDateTime now=ZonedDateTime.now(), t=LocalDate.now()
            .atTime(i.hour,i.minute).atZone(ZoneId.systemDefault());
        if(!t.isAfter(now))t=t.plusDays(1);
        return t.toInstant().toEpochMilli();
    }
    private static void write(Context c,List<Item> items) {
        JSONArray a=new JSONArray();
        try{
            for(Item i:items){
                JSONObject o=new JSONObject();
                o.put("id",i.id);o.put("hour",i.hour);o.put("minute",i.minute);
                o.put("label",i.label);o.put("daily",i.daily);
                o.put("enabled",i.enabled);o.put("at",i.at);a.put(o);
            }
        }catch(Exception e){throw new IllegalStateException("Alarm data serialization failed",e);}
        if(!c.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString("items",a.toString()).commit())
            throw new IllegalStateException("Alarm settings were not saved");
    }
    private static int newId(Context c){
        SharedPreferences p=c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        int id=p.getInt("nextId",1000)+1;
        if(!p.edit().putInt("nextId",id).commit())throw new IllegalStateException("Cannot store alarm ID");
        return id;
    }
    static void add(Context c,int hour,int minute,String label,boolean daily) {
        Item item=new Item(newId(c),hour,minute,label,daily,true,0);
        if(!daily)item.at=nextDailyTime(hour,minute);
        List<Item> list=all(c);list.add(item);write(c,list);
        schedule(c,item);AlarmWidgets.refreshAll(c);
    }
    private static long nextDailyTime(int hour,int minute){
        ZonedDateTime now=ZonedDateTime.now();
        ZonedDateTime next=LocalDate.now().atTime(hour,minute).atZone(ZoneId.systemDefault());
        if(!next.isAfter(now))next=next.plusDays(1);
        return next.toInstant().toEpochMilli();
    }
    static void quick(Context c,int minutes) {
        long millis=System.currentTimeMillis()+minutes*60000L;
        ZonedDateTime z=Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault());
        Item item=new Item(newId(c),z.getHour(),z.getMinute(),minutes+" Minuten",false,true,millis);
        List<Item> items=all(c);items.add(item);write(c,items);schedule(c,item);AlarmWidgets.refreshAll(c);
    }
    static void change(Context c,int id,boolean enable) {
        List<Item> a=all(c);
        for(Item i:a)if(i.id==id){
            cancel(c,i.id);i.enabled=enable;
            if(enable&&!i.daily&&i.at<=System.currentTimeMillis())i.at=nextDailyTime(i.hour,i.minute);
            if(enable)schedule(c,i);
        }
        write(c,a);AlarmWidgets.refreshAll(c);
    }
    static void delete(Context c,int id){
        cancel(c,id);List<Item> a=all(c);a.removeIf(i->i.id==id);
        write(c,a);AlarmWidgets.refreshAll(c);
    }
    static void fired(Context c,int id){
        List<Item> a=all(c);
        for(Item i:a)if(i.id==id){
            if(i.daily)schedule(c,i);
            else i.enabled=false;
        }
        write(c,a);AlarmWidgets.refreshAll(c);
    }
    static void restore(Context c){
        for(Item i:all(c)) {
            if(!i.enabled)continue;
            if(!i.daily&&i.at<=System.currentTimeMillis())continue;
            schedule(c,i);
        }
        AlarmWidgets.refreshAll(c);
    }
    static Item next(Context c){
        Item earliest=null;
        for(Item i:all(c)){
            if(!i.enabled)continue;
            if(!i.daily&&i.at<=System.currentTimeMillis())continue;
            if(earliest==null||nextAt(i)<nextAt(earliest))earliest=i;
        }
        return earliest;
    }
    private static PendingIntent broadcast(Context c,int id){
        Intent intent=new Intent(c,AlarmBroadcast.class);
        intent.setAction("com.uenkerlabs.alarm.FIRE");
        intent.putExtra("alarm_id",id);
        return PendingIntent.getBroadcast(c,id,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    private static void schedule(Context c,Item item){
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am==null)throw new IllegalStateException("System AlarmManager unavailable");
        if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms())
            throw new SecurityException("Exact alarms are not permitted");
        long at=item.daily?nextAt(item):item.at;
        Intent screen=new Intent(c,AlarmActivity.class);
        PendingIntent open=PendingIntent.getActivity(c,0,screen,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(at,open),broadcast(c,item.id));
    }
    private static void cancel(Context c,int id){
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am!=null)am.cancel(broadcast(c,id));
    }
}
