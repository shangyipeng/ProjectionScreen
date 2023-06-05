package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.activity.MyPhoneActivity;
import com.example.myapplication.broadcast.BatteryInfoReceiver;
import com.example.myapplication.service.WebSocketService;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.tencent.trtc.TRTCCloud;

import org.json.JSONException;
import org.json.JSONObject;


public class ApplicTion extends MultiDexApplication{
    public static Context mContext;
    public static ApplicTion app;
    public static ApplicTion instance;
    public static Intent screenIntent;
    public static int resultCode;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        initBaiduTrace();
        initBattery();
    }
    private void initBaiduTrace() {
        SDKInitializer.setAgreePrivacy(this,true);
//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
    }
    private void initBattery() {
        try {
            BatteryInfoReceiver batteryInfoReceiver = new BatteryInfoReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(Integer.MAX_VALUE);
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            intentFilter.addAction("android.intent.action.BATTERY_LOW");
            intentFilter.addAction("android.intent.action.BATTERY_OKAY");
            intentFilter.addAction("android.intent.action.DATE_CHANGED");
            mContext.registerReceiver(batteryInfoReceiver, intentFilter);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }


    //    -------------------------------------WebSocket发送空消息心跳检测------------------------------------------------
    public static ApplicTion getInstance() {
        if (instance == null) {
            instance = new ApplicTion();
        }
        return instance;
    }



}
