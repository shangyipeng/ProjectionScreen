package com.example.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myapplication.ApplicTion;
import com.example.myapplication.utile.Constants;
import com.example.myapplication.utile.SPHelper;
import com.example.myapplication.utile.SPUtilss;
import com.example.myapplication.utile.SharedPreferencesUtils;

/**
 * 手机电量监听
 */
public class BatteryInfoReceiver extends BroadcastReceiver {

    //TODO 电量通知

    private static int number = 20;

    public BatteryInfoReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("level", 0);
        int intExtra2 = intent.getIntExtra("scale", 100);
        SharedPreferencesUtils.saveBatteryLevel(ApplicTion.mContext, String.valueOf(intExtra));

        String mainUid = SPUtilss.get(ApplicTion.mContext, "MainUid", "").toString();
        if (mainUid.equals("")) {
            return;
        }
        String s2 = SPUtilss.get(ApplicTion.mContext, "batterynotify3", "").toString();

        if (intExtra > 19) {
            SPHelper.setBoolean(context, Constants.LOW_BATTERY, true);
        }
        if (number > intExtra) {
            if (number == 1) {
                return;
            }
            if (SPHelper.getBoolean(context, Constants.LOW_BATTERY, false) == true) {
                    SPHelper.setBoolean(context, Constants.LOW_BATTERY, false);
                    number = intExtra;
                    //电量上传

                }

        }
//        UserSettingS setting= UserSettingS.getConfig();
//        if(setting.getMenu6()==1){
//            Intent intent2 = new Intent(context, BatteryLowService.class);
//            Bundle bundle = new Bundle();
//            bundle.putInt("level", intExtra);
//            bundle.putInt("scale", intExtra2);
//            intent2.putExtras(bundle);
//            ForegroundServiceUtils.startService(context, intent2);
//        }
    }
}
