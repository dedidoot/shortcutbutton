package com.example.shortcutbutton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("LOB", "onReceive");
        EventBus.getDefault().post(new Callback());

        callNotif(context);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            wasScreenOn = false;
            Log.e("LOB", "wasScreenOn" + wasScreenOn);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            wasScreenOn = true;

        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            Log.e("LOB", "userpresent");
            Log.e("LOB", "wasScreenOn" + wasScreenOn);
            String url = "http://www.stackoverflow.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }
    }

    private void callNotif(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("kok", "run ");
                long[] pattern = {1000, 1000, 1000, 1000};
                PendingIntent intentPending = PendingIntent.getActivity(context, 3, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(context, "okok")
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
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(channel);
                }

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(9, notification);

                Log.e("kok", "akakakak ");
            }
        }, 3000);
    }

}