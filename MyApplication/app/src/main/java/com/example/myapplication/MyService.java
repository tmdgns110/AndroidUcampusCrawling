package com.example.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MyService extends Service {

    ServiceThread thread;
    SharedPreferences mPref;
    String loginid;
    String pass;
    ArrayList<String> subject;
    ArrayList<String> subseq;
    Map<String, String> map;
    Map<String, String> map1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        unregisterRestartAlarm(); //이미 등록된 알람이 있으면 제거
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myServiceHandler handler = new myServiceHandler();
        subject= intent.getStringArrayListExtra("subject");
        subseq =intent.getStringArrayListExtra("subseq");
        map = (Map<String, String>) intent.getSerializableExtra("map");
        map1 = (Map<String, String>) intent.getSerializableExtra("map1");
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        loginid = mPref.getString("username", "");
        pass =  mPref.getString("password", "");
        thread = new ServiceThread(handler,loginid,pass);
        thread.start();
        return START_REDELIVER_INTENT;
        //return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(!mPref.getString("username","").equals(""))
            if(!mPref.getString("stop","").equals("stop"))
                registerRestartAlarm(); // 서비스가 죽을때 알람을 등록
            else{
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.remove("stop");
                    thread.stopForever();
                    thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
                }
        else{
            SharedPreferences.Editor editor = mPref.edit();
            editor.remove("stop");
            thread.stopForever();
            thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
        }
    }

    void registerRestartAlarm() {

        Intent intent = new Intent( MyService.this, MyService.class );

        PendingIntent sender = PendingIntent.getService( MyService.this, 0, intent, 0 );

        long firstTime = SystemClock.elapsedRealtime();

        firstTime += 50*1000; // 10초 후에 알람이벤트 발생

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10*1000, sender);

    }




    void unregisterRestartAlarm() {

        Intent intent = new Intent(MyService.this, MyService.class);

        PendingIntent sender = PendingIntent.getService( MyService.this, 0, intent, 0 );

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

        am.cancel(sender);

    }

    class myServiceHandler extends Handler {

        public void handleMessage(android.os.Message msg) {
            Bundle b;
            b = msg.getData();

            Intent intent = new Intent(MyService.this, Main2Activity.class);
            intent.putStringArrayListExtra("subject",subject);
            intent.putStringArrayListExtra("subseq",subseq);
            intent.putExtra("map", (Serializable) map);
            intent.putExtra("map1", (Serializable) map1);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(MyService.this)
                    .setSmallIcon(R.drawable.stat_sample)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                     .setLights(Color.RED, 3000, 3000)
                    .setContentTitle("새 글이 등록되었습니다.")
                    .setContentText(b.getString("msg"))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
    }
}