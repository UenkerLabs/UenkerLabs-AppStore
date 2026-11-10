package com.uenkerlabs.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public final class RingActivity extends Activity {
    @Override protected void onCreate(Bundle saved){
        super.onCreate(saved);
        if(android.os.Build.VERSION.SDK_INT>=27){setShowWhenLocked(true);setTurnScreenOn(true);}
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);root.setPadding(30,30,30,30);
        root.setBackgroundColor(0xff142238);
        TextView title=new TextView(this);title.setText("UenkerLabs Wecker");title.setTextSize(30);
        title.setTextColor(0xffffffff);title.setGravity(Gravity.CENTER);
        root.addView(title);
        TextView subject=new TextView(this);
        subject.setText(getIntent().getStringExtra("label"));subject.setTextSize(21);
        subject.setTextColor(0xffcee1ff);subject.setPadding(4,40,4,40);
        subject.setGravity(Gravity.CENTER);root.addView(subject);
        Button snooze=new Button(this);snooze.setText("10 Minuten schlummern");snooze.setAllCaps(false);
        snooze.setOnClickListener(v->action("SNOOZE"));root.addView(snooze);
        Button dismiss=new Button(this);dismiss.setText("Wecker ausschalten");dismiss.setAllCaps(false);
        dismiss.setOnClickListener(v->action("DISMISS"));root.addView(dismiss);
        setContentView(root);
    }
    private void action(String name){
        Intent i=new Intent(this,AlarmControlReceiver.class);
        i.setAction(name);i.putExtra("alarm_id",getIntent().getIntExtra("alarm_id",-1));
        sendBroadcast(i);finish();
    }
}
