package com.example.myapplication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.PhoneBean;
import com.example.myapplication.utile.DataUtile;
import com.google.gson.Gson;
import com.tencent.trtc.debug.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机号码监听（拨打电话，接听电话）
 */
public class PhoneService extends Service implements ContractInterface.View{
    private static String[] lists;
    private static ContractInterface.Presenter presenter;
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(PhoneService.class)) {
            return;
        }
        Intent service = new Intent(context, PhoneService.class);
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }
    //传递数值
    public static void SetContens(String mIncomingNumber,String type) {
        if (!type.equals("空闲")){
                PhoneBean phoneBean=new PhoneBean();
                phoneBean.setPhone(mIncomingNumber+"");
                phoneBean.setTime(DataUtile.getdataTime());
                phoneBean.setDate(DataUtile.getdataTime());
                phoneBean.setType(type);
                Gson gson=new Gson();
                String contents=gson.toJson(phoneBean);
                Map<String ,Object> map=new HashMap<>();
                map.put("type","2");//type="1"来表示通讯录，"2"=电话，"3"=短信
                map.put("content",contents);
                presenter.presenter(map,"/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
        }
        LogUtils.e(type);
    }
    private static final int NOTIFICATION_ID = 100;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        presenter=new MyPresenter(this);
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
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

    @Override
    public void View(String o) {

    }
}
