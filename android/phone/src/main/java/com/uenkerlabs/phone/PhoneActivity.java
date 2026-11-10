package com.uenkerlabs.phone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.*;
import java.text.DateFormat;
import java.util.*;

public final class PhoneActivity extends Activity {
    private EditText number;private LinearLayout history;
    @Override public void onCreate(Bundle b){super.onCreate(b);render();}
    private TextView text(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(16);t.setTextColor(0xff18253b);t.setPadding(12,10,12,10);return t;}
    private Button button(String s,Runnable run){Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setOnClickListener(v->run.run());return b;}
    private String norm(String s){return s==null?"":s.replaceAll("[^+0-9]","");}
    private Set<String> blocked(){return new HashSet<>(getSharedPreferences("call_rules",MODE_PRIVATE).getStringSet("blocked",Collections.emptySet()));}
    private void setBlocked(String input,boolean shouldBlock){
        String normalized=norm(input);if(normalized.isEmpty())return;
        Set<String> list=blocked();if(shouldBlock)list.add(normalized);else list.remove(normalized);
        getSharedPreferences("call_rules",MODE_PRIVATE).edit().putStringSet("blocked",list).apply();
        Toast.makeText(this,shouldBlock?"Lokal gesperrt":"Von Sperrliste entfernt",Toast.LENGTH_SHORT).show();
    }
    private void dial(String n){if(n.isEmpty())return;try{
        startActivity(new Intent(Intent.ACTION_DIAL,Uri.fromParts("tel",n,null)));
        }catch(Exception e){Toast.makeText(this,"Keine System-Telefon-App verfügbar",Toast.LENGTH_LONG).show();}}
    private void requestScreening(){
        RoleManager manager=(RoleManager)getSystemService(ROLE_SERVICE);
        if(manager!=null && manager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)){
            if(manager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)){
                Toast.makeText(this,"Anruffilter bereits aktiviert",Toast.LENGTH_SHORT).show();
            }else startActivityForResult(manager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),7);
        }else Toast.makeText(this,"Anruffilter auf diesem Gerät nicht verfügbar",Toast.LENGTH_LONG).show();
    }
    private void render(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(10,14,10,10);
        root.setBackgroundColor(0xfff6f8fc);setContentView(root);
        root.addView(text("UenkerLabs Telefon · Vorschau"));
        root.addView(text("Anrufe laufen noch über die vorhandene Telefon-App. Anruffilter sperrt ausschließlich Nummern aus deiner lokalen Liste und nur nach Aktivierung."));
        number=new EditText(this);number.setSingleLine(true);number.setHint("Telefonnummer");number.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        root.addView(number);
        for(String[] pad:new String[][]{{"1","2","3"},{"4","5","6"},{"7","8","9"},{"*","0","#"}}){
            LinearLayout row=new LinearLayout(this);
            for(String s:pad)row.addView(button(s,()->number.append(s)),new LinearLayout.LayoutParams(0,-2,1));
            root.addView(row);
        }
        LinearLayout actions=new LinearLayout(this);
        actions.addView(button("Wählen",()->dial(norm(number.getText().toString()))),new LinearLayout.LayoutParams(0,-2,1));
        actions.addView(button("Sperren",()->setBlocked(number.getText().toString(),true)),new LinearLayout.LayoutParams(0,-2,1));
        actions.addView(button("Freigeben",()->setBlocked(number.getText().toString(),false)),new LinearLayout.LayoutParams(0,-2,1));
        root.addView(actions);
        root.addView(button("Anrufschutz in Android aktivieren",this::requestScreening));
        root.addView(button("Anrufliste lesen",this::loadHistory));
        ScrollView scroll=new ScrollView(this);history=new LinearLayout(this);history.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(history);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        history.addView(text("Anrufliste nur nach Berechtigung sichtbar."));
    }
    private void loadHistory(){
        if(checkSelfPermission(Manifest.permission.READ_CALL_LOG)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},9);return;
        }
        history.removeAllViews();
        try(Cursor c=getContentResolver().query(CallLog.Calls.CONTENT_URI,
            new String[]{CallLog.Calls.NUMBER,CallLog.Calls.DATE,CallLog.Calls.TYPE},null,null,CallLog.Calls.DATE+" DESC")){
            int count=0;
            if(c!=null)while(c.moveToNext()&&count<40){
                String n=c.getString(0);long date=c.getLong(1);
                TextView t=text((n==null?"Unbekannt":n)+" · "+DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.GERMANY).format(new Date(date)));
                t.setOnClickListener(v->number.setText(n));history.addView(t);count++;
            }
            if(count==0)history.addView(text("Keine Anrufe gefunden."));
        }catch(Exception e){history.addView(text("Anrufliste nicht verfügbar: "+e.getClass().getSimpleName()));}
    }
    @Override public void onRequestPermissionsResult(int code,String[] p,int[] r){super.onRequestPermissionsResult(code,p,r);if(code==9 && r.length>0 && r[0]==PackageManager.PERMISSION_GRANTED)loadHistory();}
}

