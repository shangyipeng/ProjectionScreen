package com.example.myapplication.utile;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class AppIconUtil {
    /**
     *
     * @param context
     * @param main com.learn.alias.MainActivity
     * @param alias com.learn.alias.AliasActivity
     */
    public static void set(Context context, String main, String alias) {
        disableComponent(context, main);
        enableComponent(context, alias);
    }

    /**
     * 启动组件
     */
    public static void enableComponent(Context context, String clazzName) {
        ComponentName componentName =  new ComponentName(context, clazzName);
        PackageManager mPackageManager = context.getPackageManager();
        mPackageManager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 禁用组件
     */
    public static void disableComponent(Context context, String clazzName) {
        ComponentName componentName =  new ComponentName(context, clazzName);
        PackageManager mPackageManager = context.getPackageManager();
        mPackageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
