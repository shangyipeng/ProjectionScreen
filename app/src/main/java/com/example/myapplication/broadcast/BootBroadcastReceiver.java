package com.example.myapplication.broadcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.myapplication.HomeActivity;


public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //屏幕唤醒
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "BootBroadcastReceiver");
        wl.acquire();

        //屏幕解锁
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("BootBroadcastReceiver");
        kl.disableKeyguard();

        //启动APP
        if (intent.getAction().equals(ACTION)) {
            Intent intent1 = new Intent(context, HomeActivity.class);  // 要启动的Activity
            if (!(context instanceof Activity)) {
                //如果不是在Activity中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                intent1 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent1);
        }
    }
}
