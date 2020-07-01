package com.example.shortcutbutton;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        startService(new Intent(getApplicationContext(), LockService.class));

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                5000, 60000, pendingIntent);


        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("kok", "run ");
                long[] pattern = {1000, 1000, 1000, 1000};
                PendingIntent intentPending = PendingIntent.getActivity(TestActivity.this, 3, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(TestActivity.this, "okok")
                        .setContentTitle("Title")
                        .setContentText("Message")
                        .setContentIntent(intentPending)
                        .setSmallIcon(R.drawable.notif)
                        .setVibrate(pattern)
                        .setLights(Color.RED, 3000, 3000)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("ok", "3", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(channel);
                }

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(TestActivity.this);
                notificationManagerCompat.notify(9, notification);

                Log.e("kok", "akakakak ");
            }
        }, 3000);*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("kok", "akakakak ");

        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void oke(Callback ok) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Log.e("kok", "sipp " + v);
        Log.e("kok", "sippX " + v.hasVibrator());
        // if (v.hasVibrator()) {
       // v.vibrate(5000);

        AudioManager myAudioManager;
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}