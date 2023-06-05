package com.tencent.trtc.videocall;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ForegroundServiceUtils {
    public static int id = 10000;

    public static void start(Context context, Service service) {
        try {
            NotificationCompat.Builder silentForegroundNotification = silentForegroundNotification(context, id);
            if (silentForegroundNotification != null) {
                service.startForeground(id, silentForegroundNotification.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop(Service service) {
        try {
            int i = Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public static NotificationCompat.Builder silentForegroundNotification(Context context, int i) {
            String CHANNEL_ID = "com.example.myapplication";
            String CHANNEL_NAME = "foreground";
            String NOTIFICATION_SERVICE = "notification";
            NotificationChannel notificationChannel;
            NotificationCompat.Builder notification;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLockscreenVisibility(-1);
                notificationChannel.setDescription("");
                notificationChannel.setName("foreground");
                notificationChannel.setShowBadge(false);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);

                NotificationManager manager;
                manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
                notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("蓝色守护")
                        .setContentText("定位中")
                        .setDefaults(-1)
                        .setAutoCancel(true)
                        .setTimeoutAfter(1000)
                        .setSmallIcon(R.mipmap.app_icon);
            } else {
                notification = new NotificationCompat.Builder(context)
                        .setContentTitle("蓝色守护")
                        .setContentText("定位中")
                        .setSmallIcon(R.mipmap.app_icon);
            }

            return notification;

    }

    public static void startService(Context context, Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            }else{
                context.startService(intent);
            }
        } catch (Throwable th) {
            Log.e("---ForegroundServi-79-",th.getMessage());
        }
    }
}
