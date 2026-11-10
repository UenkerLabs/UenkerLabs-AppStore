package com.uenkerlabs.alarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class AlarmActivity extends Activity {
    private LinearLayout list;
    private static final DateTimeFormatter FMT=DateTimeFormatter.ofPattern("HH:mm",Locale.GERMAN);
    @Override public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},9);
        draw();
    }
    private TextView label(String text,int size){
        TextView v=new TextView(this);v.setText(text);v.setTextSize(size);
        v.setTextColor(0xff20314d);v.setPadding(16,14,16,14);return v;
    }
    private void draw(){
        LinearLayout page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(14,22,14,10);page.setBackgroundColor(0xfff4f7fb);setContentView(page);
        page.addView(label("UenkerLabs Wecker",26));
        page.addView(label("Deine Zeit. Ohne Werbung oder Tracking.",14));
        Button add=new Button(this);add.setText("+ Wecker stellen");add.setAllCaps(false);
        add.setOnClickListener(v->newAlarm());page.addView(add);
        TextView help=label("Tippe auf einen Wecker zum Aktivieren/Deaktivieren. Lange drücken zum Löschen.",13);
        page.addView(help);
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);page.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        updateList();
    }
    private void updateList(){
        list.removeAllViews();
        java.util.List<AlarmStore.Item> items=AlarmStore.all(this);
        if(items.isEmpty()){list.addView(label("Noch keine Wecker eingerichtet.",17));return;}
        for(AlarmStore.Item item:items){
            LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(10,10,10,10);
            TextView time=label(String.format(Locale.GERMAN,"%02d:%02d",item.hour,item.minute)
                +"\n"+item.label+(item.daily?" · Täglich":" · Einmalig"),18);
            row.addView(time,new LinearLayout.LayoutParams(0,-2,1));
            Switch toggle=new Switch(this);toggle.setChecked(item.enabled);
            toggle.setContentDescription("Wecker aktivieren");
            toggle.setOnCheckedChangeListener((b,enabled)->{
                try{AlarmStore.change(this,item.id,enabled);updateList();}
                catch(Exception e){Toast.makeText(this,"Wecker konnte nicht aktualisiert werden: "+e.getMessage(),Toast.LENGTH_LONG).show();}
            });
            row.addView(toggle);list.addView(row);
            row.setOnLongClickListener(v->{
                new AlertDialog.Builder(this).setTitle("Wecker entfernen?")
                    .setMessage(item.label).setNegativeButton("Abbrechen",null)
                    .setPositiveButton("Entfernen",(d,w)->{AlarmStore.delete(this,item.id);updateList();}).show();
                return true;
            });
        }
    }
    private void newAlarm(){
        LocalTime now=LocalTime.now();
        new TimePickerDialog(this,(picker,hour,minute)->{
            LinearLayout container=new LinearLayout(this);container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(24,4,24,4);
            EditText title=new EditText(this);title.setSingleLine(true);
            title.setHint("Bezeichnung (optional)");
            CheckBox repeat=new CheckBox(this);repeat.setText("Jeden Tag wiederholen");
            container.addView(title);container.addView(repeat);
            new AlertDialog.Builder(this).setTitle(String.format(Locale.GERMAN,"%02d:%02d",hour,minute))
                .setView(container).setNegativeButton("Abbrechen",null)
                .setPositiveButton("Speichern",(d,w)->{
                    String s=title.getText().toString().trim();
                    if(s.isEmpty())s="Wecker";
                    try{AlarmStore.add(this,hour,minute,s,repeat.isChecked());updateList();
                        Toast.makeText(this,"Wecker eingestellt",Toast.LENGTH_SHORT).show();}
                    catch(Exception e){new AlertDialog.Builder(this).setMessage("Einrichten fehlgeschlagen: "+e.getMessage())
                        .setPositiveButton("OK",null).show();}
                }).show();
        },now.getHour(),now.getMinute(),true).show();
    }
}
