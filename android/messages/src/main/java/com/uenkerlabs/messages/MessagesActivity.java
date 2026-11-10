package com.uenkerlabs.messages;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.widget.*;
import java.text.DateFormat;
import java.util.*;

public final class MessagesActivity extends Activity {
    private LinearLayout list;
    private TextView summary;
    private boolean spamOnly=false;
    private static final String PREF="local_sms_blocklist";
    @Override public void onCreate(Bundle b){super.onCreate(b);render();if(checkSelfPermission(Manifest.permission.READ_SMS)!=PackageManager.PERMISSION_GRANTED)
        requestPermissions(new String[]{Manifest.permission.READ_SMS},4);}
    private LinearLayout column(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);return c;}
    private TextView text(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(16);t.setTextColor(0xff18253b);t.setPadding(16,12,16,12);return t;}
    private Button button(String label,Runnable task){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setOnClickListener(v->task.run());return b;}
    private String normalize(String a){return a==null?"":a.replaceAll("[^0-9+]","");}
    private Set<String> blocked(){return new HashSet<>(getSharedPreferences(PREF,MODE_PRIVATE).getStringSet("numbers",Collections.emptySet()));}
    private boolean suspicious(String s){if(s==null)return false;String v=s.toLowerCase(Locale.ROOT);
        return v.contains("bit.ly/")||v.contains("tinyurl.com/")||v.contains("konto gesperrt")||
               v.contains("passwort bestätigen")||v.contains("paketgebühr")||v.contains("crypto gewinn");}
    private void saveBlock(String number){String norm=normalize(number);if(norm.isEmpty())return;
        Set<String> set=blocked();set.add(norm);getSharedPreferences(PREF,MODE_PRIVATE).edit().putStringSet("numbers",set).apply();refresh();}
    private void compose(String addr){try{
        Intent i=new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+Uri.encode(addr==null?"":addr)));startActivity(i);
        }catch(Exception e){Toast.makeText(this,"Keine SMS-App verfügbar",Toast.LENGTH_LONG).show();}}
    private void render(){
        LinearLayout root=column();root.setPadding(10,16,10,10);root.setBackgroundColor(0xfff6f8fc);setContentView(root);
        root.addView(text("UenkerLabs Nachrichten · SMS-Vorschau"));
        root.addView(text("Lokal sortierter Posteingang. Keine automatische Löschung und kein RCS/MMS. Verdacht ist keine Gewissheit."));
        LinearLayout actions=new LinearLayout(this);
        actions.addView(button("SMS verfassen",()->compose("")),new LinearLayout.LayoutParams(0,-2,1));
        actions.addView(button("Ansicht wechseln",()->{spamOnly=!spamOnly;refresh();}),new LinearLayout.LayoutParams(0,-2,1));
        root.addView(actions);
        root.addView(button("Nummer manuell sperren",()->{
            EditText input=new EditText(this);input.setHint("Telefonnummer");
            new AlertDialog.Builder(this).setTitle("Lokale Sperrliste").setView(input).setNegativeButton("Abbrechen",null)
                .setPositiveButton("Hinzufügen",(d,w)->saveBlock(input.getText().toString())).show();
        }));
        summary=text("");root.addView(summary);
        ScrollView scroll=new ScrollView(this);list=column();scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));refresh();
    }
    private void refresh(){
        if(list==null)return;list.removeAllViews();
        summary.setText(spamOnly?"Spam-/Blocklisten-Ansicht":"Posteingang");
        if(checkSelfPermission(Manifest.permission.READ_SMS)!=PackageManager.PERMISSION_GRANTED) {
            list.addView(text("SMS-Leseberechtigung fehlt. Android-Einstellungen prüfen."));return;}
        Set<String> numbers=blocked();int shown=0;
        String[] columns={Telephony.Sms.ADDRESS,Telephony.Sms.BODY,Telephony.Sms.DATE};
        try(Cursor c=getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI,columns,null,null,Telephony.Sms.DATE+" DESC")){
            if(c!=null)while(c.moveToNext()&&shown<100){
                String number=c.getString(0),body=c.getString(1);long timestamp=c.getLong(2);
                boolean blockedSender=numbers.contains(normalize(number));boolean flag=blockedSender||suspicious(body);
                if(spamOnly&&!flag)continue;
                if(!spamOnly&&flag)continue;
                String preview=body==null?"":body.substring(0,Math.min(110,body.length()));
                String marker=blockedSender?"Gesperrter Absender":suspicious(body)?"Spam-Verdacht":"SMS";
                TextView row=text(marker+" · "+number+"\n"+DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.GERMANY).format(new Date(timestamp))+"\n"+preview);
                row.setOnClickListener(v->new AlertDialog.Builder(this).setTitle(number==null?"Unbekannt":number)
                    .setMessage(body==null?"":body).setNegativeButton("Schließen",null)
                    .setNeutralButton("Antworten",(d,w)->compose(number))
                    .setPositiveButton("Nummer sperren",(d,w)->saveBlock(number)).show());
                list.addView(row);shown++;
            }
        }catch(Exception e){list.addView(text("SMS konnten nicht gelesen werden: "+e.getClass().getSimpleName()));return;}
        if(shown==0)list.addView(text("Keine passenden Nachrichten gefunden."));
    }
    @Override public void onRequestPermissionsResult(int request,String[] permissions,int[] results){
        super.onRequestPermissionsResult(request,permissions,results);refresh();}
}

