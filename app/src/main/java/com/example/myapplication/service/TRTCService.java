package com.example.myapplication.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.MyPhoneActivity;
import com.example.myapplication.bean.PhoneBean;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.debug.Constant;
import com.tencent.trtc.debug.GenerateTestUserSig;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同屏服务开启
 */
public class TRTCService extends Service{
    public static TRTCCloud mTRTCCloud = TRTCCloud.sharedInstance(ApplicTion.mContext);;
    private static String ToPhone="";
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(TRTCService.class)) {
            return;
        }
        Intent service = new Intent(context, TRTCService.class);
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }
    private static final int NOTIFICATION_ID = 100;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);
        new Thread(new Runnable() {
            @Override
            public void run() {
                enterRoom();
            }
        }).start();

    }
    private void enterRoom() {
        //mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
     //   mTRTCCloud.setListener(new MyPhoneActivity.TRTCCloudImplListener(this));
        final TRTCCloudDef.TRTCParams screenParams = new TRTCCloudDef.TRTCParams();
        screenParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        String MyId = (String) SharedPreferencesUtils.getParam(this, "MyId", Constant.ROOM_ID + "");
        String MyPhone = (String) SharedPreferencesUtils.getParam(this, "MyPhone", Constant.USER_ID + "");
        MyId = "125673" + MyId;
        if (MyId.length() > 7) {
            MyId = "12567" + MyId;
        }
        // Constant.USER_ID;
        screenParams.userId = MyPhone;
        screenParams.roomId = Integer.parseInt(MyId);
        screenParams.userSig = GenerateTestUserSig.genTestUserSig(screenParams.userId);
        //TRTCRoleAnchor
        screenParams.role = TRTCCloudDef.TRTCRoleAnchor;
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);//音频采集
        //       mTRTCCloud.stopLocalAudio();
        //TRTC_APP_SCENE_VIDEOCALL
        //TRTCCloudDef.TRTCPublishTarget trtcPublishTarget = new TRTCCloudDef.TRTCPublishTarget();
        mTRTCCloud.muteLocalAudio(false);
        ToPhone= (String) SharedPreferencesUtils.getParam(this,"ToPhone","");
        mTRTCCloud.muteRemoteAudio(ToPhone, false);
        mTRTCCloud.enterRoom(screenParams, TRTCCloudDef.TRTC_APP_SCENE_LIVE);

        TRTCCloudDef.TRTCVideoEncParam encParams = new TRTCCloudDef.TRTCVideoEncParam();
        encParams.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_120_120;
        encParams.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        encParams.videoFps = 10;
        encParams.enableAdjustRes = false;
        encParams.videoBitrate = 1200;
        //TRTC_VIDEO_STREAM_TYPE_BIG  高清 TRTC_VIDEO_STREAM_TYPE_SUB--分屏专用
        TRTCCloudDef.TRTCScreenShareParams params = new TRTCCloudDef.TRTCScreenShareParams();
        mTRTCCloud.startScreenCapture(TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL, encParams, params);
    }
    //暂停
    public static void stop() {
        mTRTCCloud.pauseScreenCapture();
    }
    //重启
    public static void startss() {
        mTRTCCloud.resumeScreenCapture();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
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
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        }else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        }
    }
}
