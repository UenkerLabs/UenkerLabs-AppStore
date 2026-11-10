package com.uenkerlabs.calendar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class CalendarActivity extends Activity {
    private final DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("EEEE, dd. MMMM yyyy", Locale.GERMAN);
    private LocalDate selected = LocalDate.now();
    private LinearLayout root;
    private LinearLayout events;
    private TextView status;
    private final ArrayList<Cal> calendars = new ArrayList<>();

    static final class Cal {
        final long id;
        final String name;
        final boolean writable;
        Cal(long id, String name, boolean writable) { this.id=id; this.name=name; this.writable=writable; }
    }
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12),dp(12),dp(12),dp(10));
        root.setBackgroundColor(0xfff6f8fc);
        setContentView(root);
        if (!allowed(Manifest.permission.READ_CALENDAR) || !allowed(Manifest.permission.WRITE_CALENDAR)) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR},11);
        }
        render();
    }
    private int dp(int v) { return (int)(getResources().getDisplayMetrics().density*v + .5f); }
    private boolean allowed(String p) { return checkSelfPermission(p)==PackageManager.PERMISSION_GRANTED; }
    private Button button(String s, Runnable r) {
        Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setOnClickListener(v->r.run()); return b;
    }
    private TextView text(String s,int size) {
        TextView t=new TextView(this); t.setText(s); t.setTextColor(0xff1b263d); t.setTextSize(size);
        t.setPadding(dp(5),dp(7),dp(5),dp(7)); return t;
    }
    private void render() {
        root.removeAllViews();
        TextView heading=text("UenkerLabs Kalender",24); heading.setGravity(Gravity.CENTER); root.addView(heading);
        TextView help=text("Google · Infomaniak/kSync · Outlook — Android-Kalender",12);
        help.setGravity(Gravity.CENTER); root.addView(help);
        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER);
        bar.addView(button("‹",()->{selected=selected.minusMonths(1).withDayOfMonth(1);render();}));
        TextView month=text(selected.format(DateTimeFormatter.ofPattern("MMMM yyyy",Locale.GERMAN)),19);
        month.setGravity(Gravity.CENTER); bar.addView(month,new LinearLayout.LayoutParams(0,dp(58),1));
        bar.addView(button("›",()->{selected=selected.plusMonths(1).withDayOfMonth(1);render();}));
        root.addView(bar);
        TableLayout table=new TableLayout(this); table.setStretchAllColumns(true);
        TableRow weekday=new TableRow(this);
        for(String s:new String[]{"Mo","Di","Mi","Do","Fr","Sa","So"}) {
            TextView t=text(s,13); t.setGravity(Gravity.CENTER); weekday.addView(t);
        }
        table.addView(weekday);
        LocalDate first=selected.withDayOfMonth(1);
        int offset=first.getDayOfWeek().getValue()-1;
        int rows=(offset+first.lengthOfMonth()+6)/7;
        for(int row=0;row<rows;row++) {
            TableRow tr=new TableRow(this);
            for(int col=0;col<7;col++) {
                int day=row*7+col-offset+1;
                TextView cell=text(day>=1&&day<=first.lengthOfMonth()?String.valueOf(day):"",15);
                cell.setGravity(Gravity.CENTER);
                cell.setMinHeight(dp(46));
                if(day>=1&&day<=first.lengthOfMonth()) {
                    LocalDate date=first.withDayOfMonth(day);
                    if(date.equals(selected)){cell.setBackgroundColor(0xffd7e7ff);}
                    else if(date.equals(LocalDate.now())){cell.setBackgroundColor(0xffe3f1e9);}
                    cell.setOnClickListener(v->{selected=date;render();});
                }
                tr.addView(cell);
            }
            table.addView(tr);
        }
        root.addView(table);
        LinearLayout actions=new LinearLayout(this);
        actions.addView(button("Heute",()->{selected=LocalDate.now();render();}),new LinearLayout.LayoutParams(0,dp(52),1));
        actions.addView(button("+ Termin",this::addEvent),new LinearLayout.LayoutParams(0,dp(52),1));
        root.addView(actions);
        status=text("",12); root.addView(status);
        TextView selectedDay=text(selected.format(dayFormat),17); root.addView(selectedDay);
        ScrollView scroll=new ScrollView(this); events=new LinearLayout(this); events.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(events); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        reload();
    }
    private void reload() {
        events.removeAllViews(); calendars.clear();
        if(!allowed(Manifest.permission.READ_CALENDAR)) {
            status.setText("Kalender-Leseberechtigung fehlt. Bitte in Android zulassen.");
            return;
        }
        try(Cursor c=getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
            new String[]{CalendarContract.Calendars._ID,CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.VISIBLE,CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL},
            null,null,null)) {
            if(c!=null)while(c.moveToNext()){
                if(c.getInt(2)!=1)continue;
                calendars.add(new Cal(c.getLong(0),c.getString(1),c.getInt(3)>=500));
            }
        } catch(Exception e){status.setText("Kalenderquellen konnten nicht gelesen werden: "+e.getClass().getSimpleName());return;}
        status.setText(calendars.size()+" sichtbare Kalender · Nur lokal gespeicherte Android-Kalenderdaten");
        ZonedDateTime start=selected.atStartOfDay(ZoneId.systemDefault());
        long from=start.toInstant().toEpochMilli(),to=selected.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        android.net.Uri.Builder uri=CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(uri,from);ContentUris.appendId(uri,to);
        int count=0;
        try(Cursor c=getContentResolver().query(uri.build(),
            new String[]{CalendarContract.Instances.TITLE,CalendarContract.Instances.BEGIN,CalendarContract.Instances.END,
                CalendarContract.Instances.CALENDAR_ID,CalendarContract.Instances.ALL_DAY},
            null,null,CalendarContract.Instances.BEGIN+" ASC")) {
            if(c!=null)while(c.moveToNext()){
                String title=c.getString(0); long begin=c.getLong(1); long id=c.getLong(3);
                String calendar="Kalender";for(Cal a:calendars)if(a.id==id){calendar=a.name;break;}
                boolean allDay=c.getInt(4)==1;
                String hour=allDay?"Ganztägig":java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT,Locale.GERMANY).format(new Date(begin));
                events.addView(text(hour+"  "+(title==null?"(Ohne Titel)":title)+"\n"+calendar,15));count++;
            }
        } catch(Exception e){events.addView(text("Termine konnten nicht gelesen werden: "+e.getClass().getSimpleName(),14));return;}
        if(count==0)events.addView(text("Keine Termine an diesem Tag.",14));
    }
    private void addEvent() {
        if(!allowed(Manifest.permission.WRITE_CALENDAR)){requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},12);return;}
        ArrayList<Cal> writable=new ArrayList<>();for(Cal c:calendars)if(c.writable)writable.add(c);
        if(writable.isEmpty()){new AlertDialog.Builder(this).setMessage("Kein beschreibbarer Kalender verfügbar. Kontosynchronisierung prüfen.").setPositiveButton("OK",null).show();return;}
        String[] options=new String[writable.size()];for(int i=0;i<writable.size();i++)options[i]=writable.get(i).name;
        new AlertDialog.Builder(this).setTitle("Zielkalender wählen").setItems(options,(dialog,which)->{
            Cal target=writable.get(which);
            EditText title=new EditText(this);title.setSingleLine(true);title.setHint("Titel");
            new AlertDialog.Builder(this).setTitle("Neuer Termin am "+selected).setView(title).setNegativeButton("Abbrechen",null)
            .setPositiveButton("Weiter",(d,w)->{
                String s=title.getText().toString().trim();
                if(s.isEmpty()){Toast.makeText(this,"Bitte einen Titel eingeben.",Toast.LENGTH_SHORT).show();return;}
                new TimePickerDialog(this,(view,hour,minute)->saveEvent(target,s,hour,minute),10,0,true).show();
            }).show();
        }).show();
    }
    private void saveEvent(Cal target,String title,int hour,int minute) {
        long begins=selected.atTime(hour,minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        ContentValues values=new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID,target.id);
        values.put(CalendarContract.Events.TITLE,title);
        values.put(CalendarContract.Events.DTSTART,begins);
        values.put(CalendarContract.Events.DTEND,begins+3600000L);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,ZoneId.systemDefault().getId());
        try {
            android.net.Uri result=getContentResolver().insert(CalendarContract.Events.CONTENT_URI,values);
            Toast.makeText(this,result==null?"Speichern nicht bestätigt":"Termin gespeichert",Toast.LENGTH_LONG).show();reload();
        } catch(Exception e){new AlertDialog.Builder(this).setMessage("Termin konnte nicht gespeichert werden: "+e.getClass().getSimpleName()).setPositiveButton("OK",null).show();}
    }
    @Override public void onRequestPermissionsResult(int code,String[] p,int[] r) {
        super.onRequestPermissionsResult(code,p,r);render();
    }
}

