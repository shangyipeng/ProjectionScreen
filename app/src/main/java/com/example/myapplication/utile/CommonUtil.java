package com.example.myapplication.utile;

import android.app.ActivityManager;
import android.content.Context;

public class CommonUtil {
    public static boolean isServiceRunning(Context context, String str) {
        try {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
                if (str.equals(runningServiceInfo.service.getClassName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }
}
