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
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.ContactsBean;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.tencent.trtc.debug.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通讯录数据变化
 */
public class ServiceSynchContract extends Service implements ContractInterface.View {
    private static String[] lists;
    private ContractInterface.Presenter presenter;
    //监听联系人数据的监听对象
    private ContentObserver mObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            LogUtils.d("CHANGED","changed");
            // 当联系人表发生变化时进行相应的操作
            lists=SharedPreferencesUtils.getSharedPreference("contacts",ServiceSynchContract.this);
            if (lists!=null){
                List<ContactsBean> contactsList=readCotacts();
                String[] strings=new String[contactsList.size()];
                for (int i = 0; i < contactsList.size(); i++) {
                    strings[i]=(contactsList.get(i).getDisplayName()+"")+"-"+(contactsList.get(i).getNumber()+"");
                }
                SharedPreferencesUtils.setSharedPreference("contacts",strings, ServiceSynchContract.this);
            }
        }
    };
    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(ServiceSynchContract.class)) {
            return;
        }
        Intent service = new Intent(context, ServiceSynchContract.class);
        if (Build.VERSION.SDK_INT >= 28) {
            context.startForegroundService(service);
        }
    }
    private List<ContactsBean>  readCotacts(){
        List<ContactsBean> contactsList=new ArrayList<>();
        Cursor cursor = null;   //先置为空值
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            if (cursor != null){  //当游标不为空
                while (cursor.moveToNext()){
                    @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //读取电话簿名字
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactsBean contactsBean=new ContactsBean();
                    if (displayName.equals("？")){
                        contactsBean.setDisplayName(number);
                    }else {
                        contactsBean.setDisplayName(displayName);
                    }
                    contactsBean.setNumber(number);
                    contactsList.add(contactsBean);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){   //当游标不为空
                cursor.close();  //结束
            }
            List<ContactsBean> list=new ArrayList<>();
            ContactsBean[] contactsBeans=new ContactsBean[lists.length];
            String contents="";
            for (int i = 0; i < lists.length; i++) {
                String DisplayName=lists[i];
                DisplayName=DisplayName.substring(0,DisplayName.indexOf("-"))+"";
                if (!DisplayName.equals("")){
                    DisplayName=DisplayName.replace("-","");
                }
                String Number=lists[i];
                Number=Number.substring(Number.indexOf("-"))+"";
                if (!Number.equals("")){
                    Number=Number.replace("-","");
                }
                ContactsBean contactsBean=new ContactsBean();
                contactsBean.setDisplayName(DisplayName);
                contactsBean.setNumber(Number);
                contactsBeans[i]=contactsBean;
            }
           list.addAll(Arrays.asList(contactsBeans));
            LogUtils.e(list.size()+"   contactsList "+contactsList.size()+"");
            if (list.size()>contactsList.size()){//减少
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < contactsList.size(); j++) {
                        if (!list.get(i).getDisplayName().equals(contactsList.get(j).getDisplayName())){
                            ContactsBean contactsBean=new ContactsBean();
                            contactsBean.setNumber(list.get(i).getNumber());
                            contactsBean.setDisplayName(list.get(i).getDisplayName());
                            contactsBean.setType("删除");
                            Gson gson=new Gson();
                            String DeleString=gson.toJson(contactsBean);
                            LogUtils.e(DeleString);
                            contents=DeleString;
                            SharedPreferencesUtils.setParam(ServiceSynchContract.this,"Contact",
                                    DeleString);
                            String content= (String) SharedPreferencesUtils.getParam(ServiceSynchContract.this,"Contact", "");
                            LogUtils.e(content);
                            Map<String ,Object> map=new HashMap<>();
                            map.put("type","1");//type="1"来表示通讯录，"2"=电话，"3"=短信
                            map.put("content",contents);
                            presenter.presenter(map,"/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                            return contactsList;
                        }
                    }
                }

            }else if (list.size()<contactsList.size()){//增加
                for (int i = 0; i < contactsList.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (!list.get(j).getDisplayName().equals(contactsList.get(i).getDisplayName())){
                            ContactsBean contactsBean=new ContactsBean();
                            contactsBean.setNumber(list.get(i).getNumber());
                            contactsBean.setDisplayName(list.get(i).getDisplayName());
                            contactsBean.setType("新增");
                            Gson gson=new Gson();
                            String addString=gson.toJson(contactsBean);
                            LogUtils.e(addString);
                            contents=addString;
                            SharedPreferencesUtils.setParam(ServiceSynchContract.this,"Contact",
                                    addString);
                            String content= (String) SharedPreferencesUtils.getParam(ServiceSynchContract.this,"Contact", "");
                            LogUtils.e(content);
                            LogUtils.e(contents);
                            Map<String ,Object> map=new HashMap<>();
                            map.put("type","1");//type="1"来表示通讯录，"2"=电话，"3"=短信
                            map.put("content",contents);
                            presenter.presenter(map,"/api/user/addOperateLog?","POST", DataUtile.getMyToken()+"");
                            return contactsList;
                        }
                    }
                }
            }
        }
        return contactsList;
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
                ContactsContract.Contacts.CONTENT_URI, true, mObserver);
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
    //请求接口，上传数据
    @Override
    public void View(String o) {

    }
}
