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
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.PhoneBean;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.tencent.trtc.debug.Constant;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通话记录监听
 */
public class CallLogService extends Service implements ContractInterface.View{
    private static String[] lists;
    private ContractInterface.Presenter presenter;
    //监听联系人数据的监听对象
    private ContentObserver mObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            LogUtils.e("CHANGED","changed");
            lists=SharedPreferencesUtils.getSharedPreference("CallLogs", CallLogService.this);
            List<String> contactsList= getCallLogs();
            Gson gson=new Gson();
            if (lists.length>contactsList.size()){//删除
                for (int i = 0; i < lists.length; i++) {
                    for (int j = 0; j < contactsList.size(); j++) {
                        if (!lists[i].equals(contactsList.get(j))){
                            SharedPreferencesUtils.setParam(CallLogService.this,"Contact",
                                    "删除通信人-"+lists[i]);
                            PhoneBean phoneBean=gson.fromJson(lists[i],PhoneBean.class);
                            phoneBean.setType("删除");
                            String contents=gson.toJson(phoneBean);
                            Map<String ,Object> map=new HashMap<>();
                            map.put("type","2");//type="1"来表示通讯录，"2"=电话，"3"=短信
                            map.put("content",contents);
                            presenter.presenter(map,"/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");

                            String[] strings=new String[contactsList.size()];
                            for (int k = 0; k < contactsList.size(); k++) {
                                strings[k]=(contactsList.get(k));
                            }
                            SharedPreferencesUtils.setSharedPreference("CallLogs",strings, CallLogService.this);
                            return;
                        }
                    }
                }
            }else if (lists.length<contactsList.size()){//新增
                for (int i = 0; i < contactsList.size(); i++) {
                    for (int j = 0; j < lists.length; j++) {
                        if (!lists[j].equals(contactsList.get(i))){
                            SharedPreferencesUtils.setParam(CallLogService.this,"Contact",
                                    "新增通信人-"+contactsList.get(i));
                            PhoneBean phoneBean=gson.fromJson(contactsList.get(i),PhoneBean.class);
                            phoneBean.setType("新增");
                            String contents=gson.toJson(phoneBean);
                            Map<String ,Object> map=new HashMap<>();
                            map.put("type","2");//type="1"来表示通讯录，"2"=电话，"3"=短信
                            map.put("content",contents);
                            presenter.presenter(map,"/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");

                            String[] strings=new String[lists.length];
                            for (int k = 0; k < lists.length; k++) {
                                strings[k]=(lists[i]);
                            }
                            SharedPreferencesUtils.setSharedPreference("CallLogs",strings, CallLogService.this);
                            return;
                        }
                    }
                }
            }


        }
    };
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(CallLogService.class)) {
            return;
        }
        Intent service = new Intent(context, CallLogService.class);
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }
    @SuppressLint("Range")
    public  List<String> getCallLogs() {
        List<String> list=new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE,
                        CallLog.Calls.NUMBER }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        boolean hasRecord = cursor.moveToFirst();
        int count = 0;
        String strPhone = "";
        String date;
        Gson gson=new Gson();
        PhoneBean phoneBean=new PhoneBean();
        while (hasRecord) {
            @SuppressLint("Range") int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            @SuppressLint("Range") long duration = cursor.getLong(cursor
                    .getColumnIndex(CallLog.Calls.DURATION));
            strPhone = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss");
            Date d = new Date(Long.parseLong(cursor.getString(cursor
                    .getColumnIndex(CallLog.Calls.DATE))));
            date = dateFormat.format(d);
            phoneBean.setPhone(strPhone+"");
            phoneBean.setDate(date+"");
            phoneBean.setTime(duration+"");
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    phoneBean.setType("接听");
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    phoneBean.setType("拨打");
                default:
                    break;
            }
            long day_lead = 0;
            try {
                day_lead = DataUtile.getTimeRange(date,DataUtile.getdataTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (day_lead <=30) {//只显示30天     //话记录数据过多影响加载速度
                String results=gson.toJson(phoneBean);
                list.add(results);
                count++;
                hasRecord = cursor.moveToNext();
            } else {
                return list;
            }
        }
        return list;
    }
    private static final int NOTIFICATION_ID = 100;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        presenter=new MyPresenter(this);
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);
        getContentResolver().registerContentObserver(
                CallLog.Calls.CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
        getContentResolver().unregisterContentObserver(mObserver);
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
