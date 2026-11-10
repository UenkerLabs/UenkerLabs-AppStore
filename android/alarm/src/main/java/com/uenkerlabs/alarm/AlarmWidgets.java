package com.uenkerlabs.alarm;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AlarmWidgets extends AppWidgetProvider {
    public static final class Next extends AlarmWidgets {}
    public static final class Clock extends AlarmWidgets {}
    public static final class Quick5 extends AlarmWidgets {}
    public static final class Quick15 extends AlarmWidgets {}
    public static final class Manage extends AlarmWidgets {}
    private static final Class<?>[] TYPES={Next.class,Clock.class,Quick5.class,Quick15.class,Manage.class};
    @Override public void onUpdate(Context c,AppWidgetManager manager,int[] ids){
        updateType(c,manager,getClass(),ids);
    }
    static void refreshAll(Context c){
        AppWidgetManager manager=AppWidgetManager.getInstance(c);
        for(Class<?> type:TYPES){
            int[] ids=manager.getAppWidgetIds(new ComponentName(c,type));
            updateType(c,manager,type,ids);
        }
    }
    private static void updateType(Context c,AppWidgetManager manager,Class<?> type,int[] ids){
        if(ids==null||ids.length==0)return;
        for(int id:ids){
            boolean clock=type==Clock.class;
            RemoteViews view=new RemoteViews(c.getPackageName(),clock?R.layout.widget_clock:R.layout.widget_card);
            PendingIntent open=PendingIntent.getActivity(c,0,new Intent(c,AlarmActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            if(type==Next.class){
                AlarmStore.Item next=AlarmStore.next(c);
                String due=next==null?"Kein Alarm":
                    Instant.ofEpochMilli(AlarmStore.nextAt(next)).atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EEE HH:mm",Locale.GERMAN));
                view.setTextViewText(R.id.widget_heading,"Nächster Wecker");
                view.setTextViewText(R.id.widget_info,due);
                view.setTextViewText(R.id.widget_action,"Wecker ansehen");
            }else if(type==Quick5.class||type==Quick15.class){
                int mins=type==Quick5.class?5:15;
                view.setTextViewText(R.id.widget_heading,"Kurz-Wecker");
                view.setTextViewText(R.id.widget_info,mins+" Minuten");
                view.setTextViewText(R.id.widget_action,"Jetzt stellen +");
                Intent action=new Intent(c,WidgetActionReceiver.class);
                action.setAction("QUICK");action.putExtra("minutes",mins);
                PendingIntent quick=PendingIntent.getBroadcast(c,mins,action,
                    PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
                view.setOnClickPendingIntent(R.id.widget_action,quick);
            }else if(type==Manage.class){
                int enabled=0;
                for(AlarmStore.Item a:AlarmStore.all(c))if(a.enabled)enabled++;
                view.setTextViewText(R.id.widget_heading,"Wecker verwalten");
                view.setTextViewText(R.id.widget_info,enabled+" aktiv");
                view.setTextViewText(R.id.widget_action,"Öffnen +");
            }
            if(type!=Quick5.class&&type!=Quick15.class)view.setOnClickPendingIntent(R.id.widget_action,open);
            manager.updateAppWidget(id,view);
        }
    }
}
