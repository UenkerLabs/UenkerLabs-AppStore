package com.uenkerlabs.alarm;

import android.app.*;
import android.content.*;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import java.io.IOException;

public final class AlarmAudioService extends Service {
    static final int NOTIFICATION_ID=2010;
    static final String CHANNEL="uenkerlabs_alarm_high_v1";
    private MediaPlayer player;
    private android.os.Vibrator vibrator;
    @Override public IBinder onBind(Intent intent){return null;}
    @Override public int onStartCommand(Intent intent,int flags,int startId){
        if(intent==null)return START_NOT_STICKY;
        if("STOP".equals(intent.getAction())){stopSelf();return START_NOT_STICKY;}
        if(!"RING".equals(intent.getAction()))return START_NOT_STICKY;
        String title=intent.getStringExtra("label");
        int id=intent.getIntExtra("alarm_id",-1);
        createChannel();
        Intent open=new Intent(this,RingActivity.class);
        open.putExtra("label",title);open.putExtra("alarm_id",id);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent full=PendingIntent.getActivity(this,100,open,
            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification notification=new Notification.Builder(this,CHANNEL)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("UenkerLabs Wecker")
            .setContentText(title==null?"Alarm":title)
            .setCategory(Notification.CATEGORY_ALARM).setOngoing(true)
            .setContentIntent(full).setFullScreenIntent(full,true)
            .addAction(new Notification.Action.Builder(null,"Schlummern",
                control("SNOOZE",id,211)).build())
            .addAction(new Notification.Action.Builder(null,"Stoppen",
                control("DISMISS",id,212)).build())
            .build();
        try{
            if(Build.VERSION.SDK_INT>=29)startForeground(NOTIFICATION_ID,notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            else startForeground(NOTIFICATION_ID,notification);
            startSound();
        }catch(Exception e){android.util.Log.e("UenkerLabsAlarm","Cannot start alarm audio",e);stopSelf();}
        return START_NOT_STICKY;
    }
    private PendingIntent control(String action,int id,int code){
        Intent i=new Intent(this,AlarmControlReceiver.class);i.setAction(action);i.putExtra("alarm_id",id);
        return PendingIntent.getBroadcast(this,code,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    private void createChannel(){
        NotificationManager manager=getSystemService(NotificationManager.class);
        NotificationChannel channel=new NotificationChannel(CHANNEL,"UenkerLabs Weckalarme",NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Nur lokal eingestellte Wecker");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        if(manager!=null)manager.createNotificationChannel(channel);
    }
    private void startSound() throws IOException {
        releaseAudio();
        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(uri==null)uri=Settings.System.DEFAULT_NOTIFICATION_URI;
        MediaPlayer mp=new MediaPlayer();
        try{
            mp.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            mp.setDataSource(this,uri);mp.setLooping(true);mp.prepare();mp.start();player=mp;
        }catch(IOException|RuntimeException ex){mp.release();throw ex;}
        vibrator=(android.os.Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(vibrator!=null&&vibrator.hasVibrator())
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,500,500},0));
    }
    private void releaseAudio(){
        if(player!=null){try{player.stop();}catch(Exception ignored){}player.release();player=null;}
        if(vibrator!=null){vibrator.cancel();vibrator=null;}
    }
    @Override public void onDestroy(){releaseAudio();super.onDestroy();}
}
