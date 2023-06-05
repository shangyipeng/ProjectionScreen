package com.example.myapplication.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.baidu.location.Address;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.HomeActivity1;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.BDLocationBean;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.ForegroundServiceUtils;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.tencent.trtc.debug.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkingTrajectoryService extends Service implements ContractInterface.View {
    private LocationClient locationClient;
    private ContractInterface.Presenter presenter;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    //1，首先创建一个Handler对象
    Handler handlers=new Handler();
    //2，然后创建一个Runnable对像
    Runnable runnable=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            try {
                initLocationOption();
                handlers.postDelayed(runnable,1*60*1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        presenter=new MyPresenter(this);
        if (locationClient == null) {
            handlers.postDelayed(runnable,1000);
        } else {
            locationClient.stop();
            locationClient.disableLocInForeground(true);
            handlers.postDelayed(runnable,1000);
        }
    }
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(WalkingTrajectoryService.class)) {
            return;
        }
        Intent service = new Intent(context, WalkingTrajectoryService.class);
        /*
         * 9.0 及之后的系统，应用退后台后摄像头和麦克风将停止工作（https://developer.android.com/about/versions/pie/android-9.0-changes-all）
         * 该 Service 是为了保证应用退后台摄像头和麦克风依旧可以正常工作，故仅在 9.0 及之后的系统启动。
         */
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification=createForegroundNotification();
        startForeground(100, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
            locationClient.stop();
            locationClient.disableLocInForeground(true);
        }
    }
    private void initLocationOption() throws Exception {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        BDLocationListener myLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(bdLocation!=null){
                    int errorCode=bdLocation.getLocType();
                    double lat=bdLocation.getLatitude();
                    double lng=bdLocation.getLongitude();
                    float radius=bdLocation.getRadius();
                    Address address=bdLocation.getAddress();
                    if(address!=null){
                        String str=address.address;
                        SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"Address",str);
                    }else{
                        SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"Address","未知");
                    }
                    SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"saveLat",String.format("%.06f",lat));
                    SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"saveLng",String.format("%.06f",lng));
                    LogUtils.e("lat:"+lat+" lng:"+lng+" address:"+new Gson().toJson(bdLocation.getAddress())+" radius:"+radius);
                    //上报轨迹点
                }
            }
        };
        //注册监听函数
        locationClient.registerLocationListener(new MyLocationListener());
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0);//5分钟上传一次定位数据
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    @Override
    public void View(String o) {

    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            if(location!=null){
                int errorCode=location.getLocType();
                double lat=location.getLatitude();
                double lng=location.getLongitude();
                float radius=location.getRadius();
                Address address=location.getAddress();
                String Address= (String) SharedPreferencesUtils.getParam(WalkingTrajectoryService.this,"Address","");
                String saveLat= (String) SharedPreferencesUtils.getParam(WalkingTrajectoryService.this,"saveLat","");
                String saveLng= (String) SharedPreferencesUtils.getParam(WalkingTrajectoryService.this,"saveLng","");
                if (!saveLat.equals(String.format("%.06f",lat))||saveLng.equals(String.format("%.06f",lng))){
                        SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"saveLat",String.format("%.06f",lat));
                        SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"saveLng",String.format("%.06f",lng));
                        String str="未知";
                        if(address!=null){
                            str=address.address+"";
                            SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"Address",str+"");
                        }else{
                            SharedPreferencesUtils.setParam(WalkingTrajectoryService.this,"Address","未知");
                        }
                        BDLocationBean bdLocationBean=new BDLocationBean();
                        bdLocationBean.setLatitude(lat);
                        bdLocationBean.setLongitude(lng);
                        bdLocationBean.setAddress(str);
                        String contents=new Gson().toJson(bdLocationBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("type","6");//type="1"来表示通讯录，"2"=电话，"3"=短信
                        map.put("content",contents);
                        presenter.presenter(map,"/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                    }
            }
        }
    }
    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationChannelId = "notification_channel_id_01";
        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "TRTC Foreground Service Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("Channel description");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        if (Constant.isShow){
            //开启另一个页面
            Intent appIntent = new Intent(this, HomeActivity1.class);
            appIntent.setAction(Intent.ACTION_MAIN);
            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            appIntent.putExtra("Hide","true");
            //设置启动模式
            PendingIntent contentIntent;
            appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                contentIntent=PendingIntent.getActivity(this, 0,appIntent,PendingIntent.FLAG_IMMUTABLE);
            }else {
                contentIntent=PendingIntent.getActivity(this, 0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
            builder.setContentIntent(contentIntent);//跳转activity
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        }else {
            //开启另一个页面
            Intent appIntent = new Intent(this, HomeActivity1.class);
            appIntent.setAction(Intent.ACTION_MAIN);
            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            appIntent.putExtra("Hide","true");
            //设置启动模式
            PendingIntent contentIntent;
            appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                contentIntent=PendingIntent.getActivity(this, 0,appIntent,PendingIntent.FLAG_IMMUTABLE);
            }else {
                contentIntent=PendingIntent.getActivity(this, 0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        }


    }
}
