package com.example.myapplication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.baidu.navisdk.util.common.LogUtil;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.HomeActivity1;
import com.example.myapplication.R;
import com.example.myapplication.activity.HideActivity;
import com.example.myapplication.activity.MyPhoneActivity;
import com.example.myapplication.bean.WebSocketEvent;
import com.example.myapplication.bean.WebSocketEvent1;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.JWebSocketClient;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.tencent.trtc.debug.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketService extends Service {
    private final static String TAG = WebSocketService.class.getSimpleName();
    public static JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    private final static int GRAY_SERVICE_ID = 1001;

    private static final long CLOSE_RECON_TIME = 100;//连接断开或者连接错误立即重连
    private static String phone=DataUtile.getMyPhone()+"";
    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }

    }
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(WebSocketService.class)) {
            return;
        }
        Intent service = new Intent(context, WebSocketService.class);
        /*
         * 9.0 及之后的系统，应用退后台后摄像头和麦克风将停止工作（https://developer.android.com/about/versions/pie/android-9.0-changes-all）
         * 该 Service 是为了保证应用退后台摄像头和麦克风依旧可以正常工作，故仅在 9.0 及之后的系统启动。
         */
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }
    //灰色保活
    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e(TAG, "WebSocketService onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification=createForegroundNotification();
        startForeground(100, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification=createForegroundNotification();
        startForeground(100, notification);
        //初始化WebSocket
        initSocketClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
        return START_STICKY;
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
            builder.setContentIntent(contentIntent);//跳转activity
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        }


    }
    private static void initSocketClient() {
        String url = "wss://www.yousin.cn/websocket/"+ phone;
        LogUtils.e("initSocketClient  *****   "+url);
        URI uri = URI.create(url);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                if (message.indexOf("回传")==-1){
                    EventBus.getDefault().post(new WebSocketEvent(message));
                }else {
                    EventBus.getDefault().post(new WebSocketEvent1(message));
                }
            }

            @Override
            public void onOpen(ServerHandshake handShakeData) {//在webSocket连接开启时调用
                LogUtils.e(TAG, "WebSocket 连接成功");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {//在连接断开时调用
                LogUtil.e(TAG, "onClose() 连接断开_reason：" + reason);

                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }

            @Override
            public void onError(Exception ex) {//在连接出错时调用
                LogUtil.e(TAG, "onError() 连接出错：" + ex.getMessage());

                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }
        };
        connect();
    }

    public static void initSocketClient(String phon) {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
        phone=phon;
        String url = "wss://www.yousin.cn/websocket/"+ phone;
        LogUtils.e("initSocketClient  *****   "+url);
        URI uri = URI.create(url);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                if (message.indexOf("回传")==-1){
                    EventBus.getDefault().post(new WebSocketEvent(message));
                }else {
                    EventBus.getDefault().post(new WebSocketEvent1(message));
                }

            }

            @Override
            public void onOpen(ServerHandshake handShakeData) {//在webSocket连接开启时调用
                LogUtils.e(TAG, "WebSocket 连接成功");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {//在连接断开时调用
                LogUtil.e(TAG, "onClose() 连接断开_reason：" + reason);

                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }

            @Override
            public void onError(Exception ex) {//在连接出错时调用
                LogUtil.e(TAG, "onError() 连接出错：" + ex.getMessage());

                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }
        };
        connect();
    }

    /**
     * 连接WebSocket
     */
    private static void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    if (client!=null){
                        client.connectBlocking();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发送消息
     */
    public void sendMsg(String msg) {
        if (null != client) {
            try {
                client.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e(TAG, "Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    /**
     * 断开连接
     */
    public static void closeConnect() {
        mHandler.removeCallbacks(heartBeatRunnable);
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }


    //    -------------------------------------WebSocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private static Handler mHandler = new Handler();
    private static Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                    LogUtil.e(TAG, "心跳包检测WebSocket连接状态：已关闭");
                } else if (client.isOpen()) {
                    LogUtil.d(TAG, "心跳包检测WebSocket连接状态：已连接");
                } else {
                    LogUtil.e(TAG, "心跳包检测WebSocket连接状态：已断开");
                }
            } else {
                //如果client已为空，重新初始化连接
                initSocketClient();
                LogUtil.e(TAG, "心跳包检测WebSocket连接状态：client已为空，重新初始化连接");
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private static void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    LogUtil.e(TAG, "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
