package com.example.myapplication.activity;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.MyPhone.ElectronicFenceActivity;
import com.example.myapplication.activity.MyPhone.HistoryActivity;
import com.example.myapplication.activity.MyPhone.RecordofoperationsActivity;
import com.example.myapplication.activity.MyPhone.WalkingTrajectoryHistoryActivity;
import com.example.myapplication.bean.BaseBean;
import com.example.myapplication.bean.DistalEndBean;
import com.example.myapplication.bean.LogeBean;
import com.example.myapplication.bean.PersonalInformationBean;
import com.example.myapplication.bean.ReceiveBean;
import com.example.myapplication.bean.ReceiveResultBean;
import com.example.myapplication.bean.WebSocketEvent;
import com.example.myapplication.bean.WebSocketEvent1;
import com.example.myapplication.service.WebSocketService;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.JumpPermissionManagement;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.example.myapplication.utile.WindowUtils;
import com.example.myapplication.videocall.VideoCallingActivity;
import com.example.myapplication.view.LastInputEditText;
import com.google.gson.Gson;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.example.myapplication.AudioCall.AudioCallingActivity;
import com.tencent.trtc.debug.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements ContractInterface.View, View.OnClickListener {
    TXCloudVideoView MainActivity1_main_local;
    LinearLayout MainActivity1_HuoqRelativeLayoutImage;
    private final int REQUEST_CODE = 1;
    private final int HuoqReREQUEST_CODE=21;
    private final int ScreenRecordingREQUEST_CODE=31;
    private final int ScreenRecorREQUEST_CODE=41;
    private final int ScreenREQUEST_CODE=51;
    //权限申请数组
    String[] RequstString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.CAMERA,//相机权限
            Manifest.permission.RECORD_AUDIO, //允许程序录制音频
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.MODIFY_AUDIO_SETTINGS,//允许程序修改全局音频设置
    };
    private boolean ISSameScreen = false;//是否获取实时同屏
    //实时视频
    private LinearLayout MainActivity1_ScreenRecordingSwitch;
    private boolean ISOpenScreenRecording = false;
    //实时音频
    private LinearLayout MainActivity1_ScreenRecordingSwitch2;
    private boolean ISOpenScreenRecording2 = false;
    LinearLayout MainActivityRelativeLayout;
    private LinearLayout incloud_finish;//返回上级页面
    private TextView incloud_title;//头部标签
    private int Huoq_WINDOW=11;
    private int MAccessActivity_REQUEST=123;
    private String onClic="";
    //登录
    private LastInputEditText MainActivity_logePhone;
    private LastInputEditText MainActivity_logePwd;
    private AppCompatButton MainActivity_login;
    //接口请求类型
    private String Interface="登录";
    private ContractInterface.Presenter presenter;
    //导航追踪
    private LinearLayout MainActivity_PhoneLinearLayoutWeiZhi;
    private TextView MainActivity_PhoneTextViewWeiZhi;
    //手机位置刷新
    private RelativeLayout MainActivity_PhoneRelativeLayout;

    //隐藏图标
    private TextView MainActivity_HideTheDisplaySwitch;
    //行走轨迹
    private TextView MainActivity_WalkingTrajectorySwitch;
    private TextView MainActivity_WalkingTrajectoryHistory;
    private boolean TOIsWalkingTrajectory=false;
    //电话监听
    private RelativeLayout MainActivity_PhoneMonitoringRelativeLayout;
    private TextView MainActivity_PhoneMonitoringSwitch;
    private boolean ToIsPhoneMonitoring=false;
    //短信监听
    private RelativeLayout MainActivity_SMSRelativeLayout;
    private TextView MainActivity_SMSSwitch;
    private boolean TOIsSMS=false;
    //通话记录
    private RelativeLayout MainActivity_CallLogRelativeLayout;
    private TextView MainActivity_CallLogSwitch;
    private boolean TOIsCallLog=false;
    //通讯录变化
    private RelativeLayout MainActivity_ContactsRelativeLayout;
    private TextView MainActivity_ContactsSwitch;
    private boolean TOIsPhoneMonitorings=false;
    //手机行为记录
    private TextView MainActivity_HistorySwitch1;
    private TextView MainActivity_PhoneSoftwareHistory;
    private boolean TOisHistory=false;
    //电子围栏
    private LinearLayout MainActivity_ElectronicFence;
    //判断是否登录成功
    private LinearLayout MainActivity_isLogin,MainActivity_isLogin1;
    private TextView MainActivity_isLogin1_phone;

    //下发同屏点击
    private String ScreenString="";
    private int ScreenType=0;
    //下发实时音视频点击
    private String VideoString="";
    private int VideoType=0;
    //下发实时音频点击
    private String AudioString="";
    private int AudioType=0;
    //下发开启隐藏图标指令
    private String HideTheDisplayString="";
    private int HideTheDisplayType=0;
    //下发开启行走轨迹指令
    private String WalkingTrajectoryString="";
    private int WalkingTrajectoryType=0;
    //下发开启电话监听指令
    private String PhoneMonitoringString="";
    private int PhoneMonitoringType=0;
    //下发开启短信监听指令
    private String SMSString="";
    private int SMSType=0;
    //下发开启通话记录监听指令
    private String CallLogString="";
    private int CallLogType=0;
    //下发开启通讯录变化指令
    private String ContactsString="";
    private int ContactsType=0;
    //下发开启手机行为记录监听指令
    private String HistoryString="";
    private int HistoryType=0;
    private PopupWindow popupWindow;
    private LinearLayout incloud_handoff;
    //电弧监听记录
    private TextView MainActivity_PhoneMonitoringRecording;
    //短信监听记录
    private TextView MainActivity_SMSRecording;
    //通话记录
    private TextView MainActivity_CallLogRecording;
    //通讯录变化
    private TextView MMainActivity_ContactsRecording;
    public boolean isService(){
        ActivityManager activityManager=(ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services=activityManager.getRunningServices(1000);
        for (int i = 0; i <services.size(); i++) {
            ActivityManager.RunningServiceInfo info=services.get(i);
            Log.e("tag",""+info.service.getClassName());
            if (info.service.getClassName().equals("com.example.myapplication.service.WebSocketService")){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }
    @Override
    public void setcCreate(Bundle savedInstanceState) {
        WebSocketService.closeConnect();
        WebSocketService.initSocketClient(DataUtile.getMyPhone());
        presenter=new MyPresenter(this);
        ISSameScreen = (boolean) SharedPreferencesUtils.getParam(MainActivity.this, "ISSameScreen", false);
        DataUtile.screenWidth = WindowUtils.getScreenWidth(MainActivity.this);
        DataUtile.screenHeight = WindowUtils.getScreenHeight(MainActivity.this);
        EventBus.getDefault().register(this);
        //初始化
        initFind();
        initClick();
        String ToPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"TOPhone","");
        MainActivity_isLogin1_phone.setText(ToPhone+"");
        if (DataUtile.getTOToken().equals("")){
            MainActivity_isLogin.setVisibility(View.VISIBLE);
            MainActivity_isLogin1.setVisibility(View.GONE);
        }else {
            MainActivity_isLogin.setVisibility(View.GONE);
            MainActivity_isLogin1.setVisibility(View.VISIBLE);
        }
        if (!DataUtile.getTOToken().equals("")){
            Interface="个人信息";
            presenter.presenter(null, "/api/user/info?", "GET", DataUtile.getTOToken() + "");
        }
    }
    public void showTipDialog( String msg) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("在其他应用上层显示权限为" + msg + "必要权限，请点击确认后进行授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
    }
    private int liveScreen=0;
    private int liveAudio=0;
    private int liveVideo=0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReciverTouchEvent(WebSocketEvent1 eve) {
        String event=eve.getMessage();
        LogUtils.e(event+"***********");
        if (!event.equals("连接成功")){
            if (popupWindow!=null){
                popupWindow.dismiss();
            }
            handlerCode=15;
            mHandler.removeCallbacks(Runnable);
            Gson gson=new Gson();
            ReceiveBean receiveBean=gson.fromJson(event,ReceiveBean.class);
            ReceiveResultBean receiveResultBean=gson.fromJson(receiveBean.getContent(),ReceiveResultBean.class);
                if (receiveResultBean.getType().equals("hideIcon")){//显示隐藏
                    if (receiveResultBean.getResult()==1){
                        HideTheDisplayType=0;
                        MainActivity_HideTheDisplaySwitch.setText("显示");
                    }else if (receiveResultBean.getResult()==2){
                        HideTheDisplayType=1;
                        MainActivity_HideTheDisplaySwitch.setText("隐藏");
                    }else {
                        HideTheDisplayType=1;
                        MainActivity_HideTheDisplaySwitch.setText("隐藏");
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("walkTrack")){//行走轨迹
                    if (receiveResultBean.getResult()==1){
                        TOIsWalkingTrajectory=true;
                        MainActivity_WalkingTrajectorySwitch.setText("关闭");
                    }else if (receiveResultBean.getResult()==2){
                        MainActivity_WalkingTrajectorySwitch.setText("开启");
                        TOIsWalkingTrajectory=false;
                    }else {
                        MainActivity_WalkingTrajectorySwitch.setText("开启");
                        TOIsWalkingTrajectory=false;
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("phoneListen")){   //电话监听

                    if (receiveResultBean.getResult()==1){
                        ToIsPhoneMonitoring=true;
                        MainActivity_PhoneMonitoringSwitch.setText("关闭");
                    }else if (receiveResultBean.getResult()==2){
                        ToIsPhoneMonitoring=false;
                        MainActivity_PhoneMonitoringSwitch.setText("开启");
                    }else {
                        ToIsPhoneMonitoring=false;
                        MainActivity_PhoneMonitoringSwitch.setText("开启");
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("smsListen")){   //短信监听
                    if (receiveResultBean.getResult()==1){
                        TOIsSMS=true;
                        MainActivity_SMSSwitch.setText("关闭");
                    }else if (receiveResultBean.getResult()==2){
                        TOIsSMS=false;
                        MainActivity_SMSSwitch.setText("开启");
                    }else {
                        TOIsSMS=false;
                        MainActivity_SMSSwitch.setText("开启");
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("callRecords")){   //通话记录
                    if (receiveResultBean.getResult()==1){
                        TOIsCallLog=true;
                        MainActivity_CallLogSwitch.setText("关闭");
                    }else if (receiveResultBean.getResult()==2){
                        TOIsCallLog=false;
                        MainActivity_CallLogSwitch.setText("开启");
                    }else {
                        TOIsCallLog=false;
                        MainActivity_CallLogSwitch.setText("开启");
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("contactsChange")){   //通讯录变化
                    if (receiveResultBean.getResult()==1){
                        TOIsPhoneMonitorings=true;
                        MainActivity_ContactsSwitch.setText("关闭");
                    }else if (receiveResultBean.getResult()==2){
                        TOIsPhoneMonitorings=false;
                        MainActivity_ContactsSwitch.setText("开启");
                    }else {
                        TOIsPhoneMonitorings=false;
                        MainActivity_ContactsSwitch.setText("开启");
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("mobileBehavior")){   //手机行为记录
                    if (receiveResultBean.getResult()==1){
                        TOisHistory=true;
                        MainActivity_HistorySwitch1.setText("关闭");
                    }else if (receiveResultBean.getResult()==2){
                        TOisHistory=false;
                        MainActivity_HistorySwitch1.setText("开启");
                    }else {
                        TOisHistory=false;
                        MainActivity_HistorySwitch1.setText("开启");
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("liveScreen")) {   //是否开启同屏
                    MainActivity1_HuoqRelativeLayoutImage.setOnClickListener(this);
                    if (receiveResultBean.getResult() == 1) {
                        ISSameScreen = true;
                        if (liveScreen==0){
                            liveScreen++;
                            String ToId= (String) SharedPreferencesUtils.getParam(MainActivity.this,"ToId",Constant.ROOM_ID+"");
                            String MyId= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyId","1256");
                            String MyPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyPhone",MyId+"");
                           String ToIds="125673"+ToId;
                            if (ToIds.length()>7){
                                ToIds="12567"+ToId;
                            }
                            //跳转要根据返回进行控制
                            Constant.USER_ID = DataUtile.getMyID()+"";
                            Intent intents = new Intent(MainActivity.this, WatchActivity.class);
                            intents.putExtra("ussid", MyPhone);
                            intents.putExtra("roomId",ToIds);
                            startActivityForResult(intents, 10086);
                        }


                    } else if (receiveResultBean.getResult() == 2) {
                        ISSameScreen = false;
                    } else {
                        ISSameScreen = false;
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("liveVideo")) { //是否开启视频
                    MainActivity1_ScreenRecordingSwitch.setOnClickListener(this);
                    if (receiveResultBean.getResult() == 1) {
                        ISOpenScreenRecording = true;
                        liveVideo= (int) SharedPreferencesUtils.getParam(this,"liveVideoss",0);
                        if (liveVideo==0){
                            SharedPreferencesUtils.setParam(this,"liveVideoss",1);
                            String ToId= (String) SharedPreferencesUtils.getParam(MainActivity.this,"ToId",Constant.ROOM_ID+"");
                            String MyId= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyId","1256");
                            String MyPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyPhone",MyId+"");
                            String ToIds="123673"+ToId;
                            if (ToIds.length()>7){
                                ToIds="";
                                ToIds="12367"+ToId;
                            }
                            //跳转要根据返回进行控制
                            Intent intent = new Intent(MainActivity.this, VideoCallingActivity.class);
                            intent.putExtra(Constant.ROOM_ID, ToIds);
                            intent.putExtra(Constant.USER_ID, MyPhone+"");
                            intent.putExtra("Type","远端");
                            startActivityForResult(intent, 1256);
                        }
                    } else if (receiveResultBean.getResult() == 2) {
                        ISOpenScreenRecording = false;
                    } else {
                        ISOpenScreenRecording = false;
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
                }else if (receiveResultBean.getType().equals("liveAudio")) {//是否开启音频
                    MainActivity1_ScreenRecordingSwitch2.setOnClickListener(this);
                    if (receiveResultBean.getResult() == 1) {
                        ISOpenScreenRecording2 = true;
                        liveAudio= (int) SharedPreferencesUtils.getParam(this,"liveAudioss",0);
                        if (liveAudio==0){
                            SharedPreferencesUtils.setParam(this,"liveAudioss",1);
                            String ToId= (String) SharedPreferencesUtils.getParam(MainActivity.this,"ToId",Constant.ROOM_ID+"");
                            String MyId= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyId","1256");
                            String MyPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyPhone",MyId+"");
                            String ToIds = "124673" + ToId;
                            if (ToIds.length() > 7) {
                                ToIds = "";
                                ToIds = "12467" + ToId;
                            }

                            Intent intent = new Intent(MainActivity.this, AudioCallingActivity.class);
                            intent.putExtra(Constant.ROOM_ID, ToIds);
                            intent.putExtra(Constant.USER_ID, MyPhone+"");
                            intent.putExtra("Type","远端");
                            startActivityForResult(intent, 1258);
                        }
                    } else if (receiveResultBean.getResult() == 2) {
                        ISOpenScreenRecording2 = false;
                    } else {
                        ISOpenScreenRecording2 = false;
                        if (dialog==null){
                            SetPermissions();
                        }
                    }
            }

        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume()");
        if (DataUtile.getTOToken().equals("")){
            MainActivity_isLogin.setVisibility(View.VISIBLE);
            MainActivity_isLogin1.setVisibility(View.GONE);
        }else {
            MainActivity_isLogin.setVisibility(View.GONE);
            MainActivity_isLogin1.setVisibility(View.VISIBLE);
        }
        String ToPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"TOPhone","");
        MainActivity_isLogin1_phone.setText(ToPhone+"");
        ISSameScreen = (boolean) SharedPreferencesUtils.getParam(MainActivity.this, "ISSameScreen", false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            ArrayList<String> requestList = new ArrayList<>();//允许询问列表
            ArrayList<String> banList = new ArrayList<>();//禁止列表
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.i("【" + permissions[i] + "】权限授权成功");
                } else {
                    //判断是否允许重新申请该权限
                    boolean nRet = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    LogUtils.i("shouldShowRequestPermissionRationale nRet=" + nRet);
                    if (nRet) {//允许重新申请
                        requestList.add(permissions[i]);
                    } else {//禁止申请
                        banList.add(permissions[i]);
                    }
                }
            }
            //优先对禁止列表进行判断
            if (requestList.size() > 0) {//告知权限的作用，并重新申请
             if (onClic.equals("获取实时同屏")){
                    showTipDialog(requestList,"获取实时同屏功能");
                    ISSameScreen = false;
                    SharedPreferencesUtils.setParam(MainActivity.this, "ISSameScreen", false);
                }else if (onClic.equals("实时音视频(摄像头)")){
                    showTipDialog(requestList,"实时音视频");
                    ISOpenScreenRecording = false;
                } else if (onClic.equals("实时音频")){
                    showTipDialog(requestList,"实时音频");
                    ISOpenScreenRecording2 = false;
                }
            } else if (banList.size() > 0) {//告知该权限作用，要求手动授予权限
                if (onClic.equals("获取实时同屏")){
                    showTipDialog(banList,"获取实时同屏功能");
                    ISSameScreen = false;
                    SharedPreferencesUtils.setParam(MainActivity.this, "ISSameScreen", false);
                }else if (onClic.equals("实时音视频(摄像头)")){
                    showTipDialog(banList,"实时音视频");
                    ISOpenScreenRecording = false;
                }else if (onClic.equals("实时音频")){
                    showTipDialog(banList,"实时音频");
                    ISOpenScreenRecording2 = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("权限申请回调中发生异常: " + e.getMessage());
        }
    }
    public void showTipDialog(ArrayList<String> pmList,String msg) {
        List<String> list=new ArrayList<>();
        for (int i = 0; i < pmList.size(); i++) {
            if (pmList.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                    pmList.get(i).equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                list.add("存储");
            }else if (pmList.get(i).equals(Manifest.permission.CAMERA)){
                list.add("相机");
            }else if (pmList.get(i).equals(Manifest.permission.RECORD_AUDIO)){
                list.add("允许程序录制音频");
            }else if (pmList.get(i).equals(Manifest.permission.MODIFY_AUDIO_SETTINGS)){
                list.add("程序修改全局音频设置");
            }
        }
        String qx="";
        List<String> list1=DataUtile.SetDeduplication(list);
        if (list1.size()>1){
            for (int i = 0; i < list1.size(); i++) {
                qx+=list1.get(i)+"、";
            }
            qx=qx.substring(0,qx.length()-1);
        }else {
            qx=list1.get(0);
        }

        LogUtils.e(qx);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage( qx+"权限为"+msg+"必要权限，请前往设置页面进行授权")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转至当前应用的详情页面
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
                        //跳转应用权限详情页
                        JumpPermissionManagement.GoToSetting(MainActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Gson gson=new Gson();
        if (resultCode == 10086) {
            Bundle bundle = data.getExtras(); // 继续获取intent携带的数据
            if (bundle != null) {
                String isReload = bundle.getString("isReload");
                LogUtils.e("" + isReload);
                if (isReload.equals("isReload")) {
                    DataUtile.isWatch = false;
                    Constant.USER_ID = DataUtile.getMyID()+"";
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ISSameScreen = false;
                    SharedPreferencesUtils.setParam(MainActivity.this, "ISSameScreen", false);
                }
            }
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("live_screen");
            distalEndBean.setValue("3");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=gson.toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
            ScreenType=0;
        }else if (resultCode==10087){//获取同屏页面没有成功进入房间

        }else if (requestCode == 1256) {
            ISOpenScreenRecording = false;
            VideoType=0;
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("live_video");
            distalEndBean.setValue("3");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=gson.toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");

        } else if (requestCode == 1258) {
            ISOpenScreenRecording2 = false;
            AudioType=0;
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("live_audio");
            distalEndBean.setValue("3");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=gson.toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");

        } else if (requestCode == 10089) {
            if (resultCode != RESULT_OK) {
                MediaProjectionManager mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                if (mProjectionManager != null) {
                    startActivityForResult(mProjectionManager.createScreenCaptureIntent(), 10089);
                }
            }
        }
    }

    //初始化
    private void initFind() {
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText("查找他人");
        MainActivityRelativeLayout=findViewById(R.id.MainActivityRelativeLayout);
        MainActivity1_main_local = findViewById(R.id.MainActivity1_main_local);
        MainActivity1_HuoqRelativeLayoutImage = findViewById(R.id.MainActivity1_HuoqRelativeLayoutImage);
        MainActivity1_ScreenRecordingSwitch = findViewById(R.id.MainActivity1_ScreenRecordingSwitch);
        MainActivity1_ScreenRecordingSwitch2 = findViewById(R.id.MainActivity1_ScreenRecordingSwitch2);
        MainActivity_logePhone=findViewById(R.id.MainActivity_logePhone);
        MainActivity_logePwd=findViewById(R.id.MainActivity_logePwd);
        MainActivity_login=findViewById(R.id.MainActivity_login);
        MainActivity_PhoneLinearLayoutWeiZhi=findViewById(R.id.MainActivity_PhoneLinearLayoutWeiZhi);
        MainActivity_PhoneTextViewWeiZhi=findViewById(R.id.MainActivity_PhoneTextViewWeiZhi);
        MainActivity_PhoneRelativeLayout=findViewById(R.id.MainActivity_PhoneRelativeLayout);
        MainActivity_HideTheDisplaySwitch=findViewById(R.id.MainActivity_HideTheDisplaySwitch);
        MainActivity_WalkingTrajectorySwitch=findViewById(R.id.MainActivity_WalkingTrajectorySwitch);
        MainActivity_WalkingTrajectoryHistory=findViewById(R.id.MainActivity_WalkingTrajectoryHistory);
        MainActivity_PhoneMonitoringRelativeLayout=findViewById(R.id.MainActivity_PhoneMonitoringRelativeLayout);
        MainActivity_PhoneMonitoringSwitch=findViewById(R.id.MainActivity_PhoneMonitoringSwitch);
        MainActivity_SMSRelativeLayout=findViewById(R.id.MainActivity_SMSRelativeLayout);
        MainActivity_SMSSwitch=findViewById(R.id.MainActivity_SMSSwitch);
        MainActivity_CallLogRelativeLayout=findViewById(R.id.MainActivity_CallLogRelativeLayout);
        MainActivity_CallLogSwitch=findViewById(R.id.MainActivity_CallLogSwitch);
        MainActivity_ContactsRelativeLayout=findViewById(R.id.MainActivity_ContactsRelativeLayout);
        MainActivity_ContactsSwitch=findViewById(R.id.MainActivity_ContactsSwitch);
        MainActivity_HistorySwitch1=findViewById(R.id.MainActivity_HistorySwitch1);
        MainActivity_PhoneSoftwareHistory=findViewById(R.id.MainActivity_PhoneSoftwareHistory);
        MainActivity_ElectronicFence=findViewById(R.id.MainActivity_ElectronicFence);
        MainActivity_isLogin=findViewById(R.id.MainActivity_isLogin);
        MainActivity_isLogin1=findViewById(R.id.MainActivity_isLogin1);
        MainActivity_isLogin1_phone=findViewById(R.id.MainActivity_isLogin1_phone);
        incloud_handoff=findViewById(R.id.incloud_handoff);
        incloud_handoff.setVisibility(View.VISIBLE);
        //通话记录
        MainActivity_PhoneMonitoringRecording=findViewById(R.id.MainActivity_PhoneMonitoringRecording);
        //短信监听
        MainActivity_SMSRecording=findViewById(R.id.MainActivity_SMSRecording);
        //通话记录
        MainActivity_CallLogRecording=findViewById(R.id.MainActivity_CallLogRecording);
        //通讯录变化
        MMainActivity_ContactsRecording=findViewById(R.id.MMainActivity_ContactsRecording);
    }

    @Override
    public void onClick(View v) {
        Gson gson=new Gson();
        switch (v.getId()){
            case R.id.MainActivity1_HuoqRelativeLayoutImage://同屏
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){
                        liveScreen=0;
                        onClic="下发同屏点击";
                        Interface="下发同屏点击";
                        ScreenString="下发同屏点击";
                        if (ISSameScreen) {
                            ScreenType=0;
                        } else {
                            ScreenType=1;
                        }
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("live_screen");
                        distalEndBean.setValue(ScreenType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                        MainActivity1_HuoqRelativeLayoutImage.setOnClickListener(null);
                    }else {
                        SetToLogin();
                    }
                }
                break;
            case R.id.MainActivity1_ScreenRecordingSwitch://视频
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){
                        onClic="实时音视频";
                        Interface="实时音视频";
                        VideoString="下发实时音视频点击";
                        if (ISOpenScreenRecording) {
                            VideoType=0;
                        } else {
                            VideoType=1;
                        }
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("live_video");
                        distalEndBean.setValue(VideoType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                        SharedPreferencesUtils.setParam(this,"liveVideoss",0);
                        MainActivity1_ScreenRecordingSwitch.setOnClickListener(null);
                    }else {
                        SetToLogin();
                    }
                }
                break;
            case R.id.MainActivity1_ScreenRecordingSwitch2://音频
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){
                        onClic="实时音频";
                        Interface="实时音频";
                        AudioString="下发实时音频点击";
                        if (ISOpenScreenRecording2) {
                            AudioType=0;
                        } else {
                            AudioType=1;
                        }
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("live_audio");
                        distalEndBean.setValue(AudioType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                        SharedPreferencesUtils.setParam(this,"liveAudioss",0);
                        MainActivity1_ScreenRecordingSwitch2.setOnClickListener(null);
                    }else {
                        SetToLogin();
                    }
                }
                break;
        }
    }

    //点击事件监听
    private void initClick() {
        //是否获取实时同屏点击事件
        MainActivity1_HuoqRelativeLayoutImage.setOnClickListener(this);
        //实时音视频
        MainActivity1_ScreenRecordingSwitch.setOnClickListener(this);
        //实时音频
        MainActivity1_ScreenRecordingSwitch2.setOnClickListener(this);
        //切换账号
        incloud_handoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.setParam(MainActivity.this,"TOToken","");
                SharedPreferencesUtils.setParam(MainActivity.this,"ToId","");
                SharedPreferencesUtils.setParam(MainActivity.this, "ToHones", "");
                //用户输入的密码进行保存
                SharedPreferencesUtils.setParam(MainActivity.this, "ToPhone", "");
                SharedPreferencesUtils.setParam(MainActivity.this, "ToPwd", "");
                MainActivity_logePhone.setText("");
                MainActivity_logePwd.setText("");
                MainActivity_isLogin.setVisibility(View.VISIBLE);
                MainActivity_isLogin1.setVisibility(View.GONE);
            }
        });
        Gson gson=new Gson();
        //返回
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (popupWindow!=null){
                        popupWindow.dismiss();
                    }
                    finish();
                }
            }
        });
        //登录
        MainActivity_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  phone = MainActivity_logePhone.getText().toString().trim();
                String pwdStr = MainActivity_logePwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwdStr)) {
                    Toast.makeText(MainActivity.this, "请输入对方账号", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pwdStr)) {
                    Toast.makeText(MainActivity.this, "请输入对方密码", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    String MyPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"MyPhone","");
                    if(phone.equals(MyPhone)){
                        Toast.makeText(MainActivity.this,"无法登录自己账号,请重新输入",Toast.LENGTH_SHORT).show();
                        MainActivity_logePhone.setText("");
                        MainActivity_logePwd.setText("");
                    }else {
                        Interface="登录";
                        SharedPreferencesUtils.setParam(MainActivity.this, "ToHones", phone);
                        //用户输入的密码进行保存
                        SharedPreferencesUtils.setParam(MainActivity.this, "TOPhone", phone);
                        SharedPreferencesUtils.setParam(MainActivity.this, "ToPwd", pwdStr);
                        //如果用户名和密码都不为空连接服务器进行判断用户名和密码是否正确;
                        Map<String, Object> map = new HashMap<>();
                        map.put("username", phone);
                        map.put("password", pwdStr);
                        map.put("scene", "account");
                        presenter.presenter(map, "api/login/check?", "POST","");
                    }

                }
            }
        });
        //手机位置刷新
        MainActivity_PhoneRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下发刷新指令，获取对方手机位置

            }
        });
        //导航追踪
        MainActivity_PhoneLinearLayoutWeiZhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity_PhoneTextViewWeiZhi.equals("未获取位置,请打开对方手机定位")){

                }
            }
        });
        //下发开启隐藏图标指令
        MainActivity_HideTheDisplaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        HideTheDisplayString="下发开启隐藏图标指令";
                        Interface="下发开启隐藏图标指令";
                        if (MainActivity_HideTheDisplaySwitch.getText().equals("显示")) {
                            HideTheDisplayType=0;
                        } else {
                            HideTheDisplayType=1;
                        }

                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "hide_icon");
                        map1.put("value", HideTheDisplayType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("hide_icon");
                        distalEndBean.setValue(HideTheDisplayType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //下发开启行走轨迹指令
        MainActivity_WalkingTrajectorySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        WalkingTrajectoryString="下发开启行走轨迹指令";
                        Interface="下发开启行走轨迹指令";
                        if (TOIsWalkingTrajectory) {
                            WalkingTrajectoryType=0;
                        } else {
                            WalkingTrajectoryType=1;
                        }
                        if (WalkingTrajectoryType==1){
                            MainActivity_WalkingTrajectorySwitch.setText("关闭");
                        }else {
                            MainActivity_WalkingTrajectorySwitch.setText("开启");
                        }
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "walk_track");
                        map1.put("value", WalkingTrajectoryType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("walk_track");
                        distalEndBean.setValue(WalkingTrajectoryType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //查看对方行走轨迹记录
        MainActivity_WalkingTrajectoryHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        String toId = (String) SharedPreferencesUtils.getParam(MainActivity.this, "ToId", "116");
                        String MyId = (String) SharedPreferencesUtils.getParam(MainActivity.this, "MyId", "112");
                        Intent intent = new Intent(MainActivity.this, WalkingTrajectoryHistoryActivity.class);
                        intent.putExtra("token",DataUtile.getTOToken()+"");
                        intent.putExtra("fromId", MyId);
                        intent.putExtra("name", "测试");
                        intent.putExtra("toId", toId);
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //下发开启电话监听指令
        MainActivity_PhoneMonitoringRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        PhoneMonitoringString="下发开启电话监听指令";
                        Interface="下发开启电话监听指令";
                        if (ToIsPhoneMonitoring) {
                            PhoneMonitoringType=0;
                        } else {
                            PhoneMonitoringType=1;
                        }
                        if (PhoneMonitoringType==1){
                            MainActivity_PhoneMonitoringSwitch.setText("关闭");
                        }else {
                            MainActivity_PhoneMonitoringSwitch.setText("开启");
                        }
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "walk_socket");
                        map1.put("value", PhoneMonitoringType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("phone_listen");
                        distalEndBean.setValue(PhoneMonitoringType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //电话监听记录
        MainActivity_PhoneMonitoringRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Intent intent = new Intent(MainActivity.this, RecordofoperationsActivity.class);
                        intent.putExtra("Token",DataUtile.getTOToken()+"");
                        intent.putExtra("type","2");//"1"来表示通讯录，"2"=电话，"3"=短信
                        intent.putExtra("title","获取通话记录");//1获取通讯录记录 2获取通话记录 3获取短信记录
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }
            }
        });
        //下发开启短信监听指令
        MainActivity_SMSRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Interface="下发开启短信监听指令";
                        SMSString="下发开启短信监听指令";
                        if (TOIsSMS) {
                            SMSType=0;
                        } else {
                            SMSType=1;
                        }
                        if (SMSType==1){
                            MainActivity_SMSSwitch.setText("关闭");
                        }else {
                            MainActivity_SMSSwitch.setText("开启");
                        }
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "sms_listen");
                        map1.put("value", SMSType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("sms_listen");
                        distalEndBean.setValue(SMSType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        MainActivity_SMSRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Intent intent = new Intent(MainActivity.this, RecordofoperationsActivity.class);
                        intent.putExtra("Token",DataUtile.getTOToken()+"");
                        intent.putExtra("type","3");//"1"来表示通讯录，"2"=电话，"3"=短信
                        intent.putExtra("title","获取短信记录");//1获取通讯录记录 2获取通话记录 3获取短信记录
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }
            }
        });
        //下发开启通话记录监听指令
        MainActivity_CallLogRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Interface="下发开启通话记录监听指令";
                        CallLogString="下发开启通话记录监听指令";
                        if (TOIsCallLog) {
                            CallLogType=0;
                        } else {
                            CallLogType=1;
                        }
                        if (CallLogType==1){
                            MainActivity_CallLogSwitch.setText("关闭");
                        }else {
                            MainActivity_CallLogSwitch.setText("开启");
                        }
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "call_records");
                        map1.put("value", CallLogType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("call_records");
                        distalEndBean.setValue(CallLogType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //通话记录
        MainActivity_CallLogRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Intent intent = new Intent(MainActivity.this, RecordofoperationsActivity.class);
                        intent.putExtra("Token",DataUtile.getTOToken()+"");
                        intent.putExtra("type","2");//"1"来表示通讯录，"2"=电话，"3"=短信
                        intent.putExtra("title","获取通话记录");//1获取通讯录记录 2获取通话记录 3获取短信记录
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }
            }
        });
        //下发开启通讯录变化指令
        MainActivity_ContactsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Interface="下发开启通讯录变化指令";
                        ContactsString="下发开启通讯录变化指令";
                        if (TOIsPhoneMonitorings) {
                            ContactsType=0;
                        } else {
                            ContactsType=1;
                        }
                        if (ContactsType==1){
                            MainActivity_ContactsSwitch.setText("关闭");
                        }else {
                            MainActivity_ContactsSwitch.setText("开启");
                        }
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "contacts_change");
                        map1.put("value", ContactsType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("contacts_change");
                        distalEndBean.setValue(ContactsType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //通讯录变化
        MMainActivity_ContactsRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Intent intent = new Intent(MainActivity.this, RecordofoperationsActivity.class);
                        intent.putExtra("Token",DataUtile.getTOToken()+"");
                        intent.putExtra("type","1");//"1"来表示通讯录，"2"=电话，"3"=短信
                        intent.putExtra("title","获取通讯录记录");//1获取通讯录记录 2获取通话记录 3获取短信记录
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }
            }
        });
        //下发开启手机行为记录监听指令
        MainActivity_HistorySwitch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Interface="下发开启手机行为记录监听指令";
                        HistoryString="下发开启手机行为记录监听指令";
                        if (TOisHistory) {
                            HistoryType=0;
                        } else {
                            HistoryType=1;
                        }
                        if (HistoryType==1){
                            MainActivity_HistorySwitch1.setText("关闭");
                        }else {
                            MainActivity_HistorySwitch1.setText("开启");
                        }
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("field", "mobile_behavior");
                        map1.put("value", HistoryType+"");
                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getTOToken() + "");
                        DistalEndBean distalEndBean=new DistalEndBean();
                        distalEndBean.setField("mobile_behavior");
                        distalEndBean.setValue(HistoryType+"");
                        distalEndBean.setPhone(DataUtile.getMyPhone()+"");
                        String value=gson.toJson(distalEndBean);
                        Map<String ,Object> map=new HashMap<>();
                        map.put("field","websocket");
                        map.put("value",value);
                        map.put("mobile",DataUtile.getToPhone()+"");
                        presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
                        getpopupwindow(MainActivity.this,MainActivityRelativeLayout);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //点击跳转对方行为记录
        MainActivity_PhoneSoftwareHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                        intent.putExtra("Token",DataUtile.getTOToken()+"");
                        intent.putExtra("type","MainActivity");
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
        //查询对方电子围栏
        MainActivity_ElectronicFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtile.isFastClick()){
                    if (!DataUtile.getTOToken().equals("")){//用户已登录
                        Intent intent = new Intent(MainActivity.this, ElectronicFenceActivity.class);
                        intent.putExtra("Token",DataUtile.getTOToken()+"");
                        startActivity(intent);
                    }else {
                        SetToLogin();
                    }
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(MainActivity.this, WebSocketService.class);
        intent.setPackage(getPackageName());
        stopService(intent);
        mHandler.removeCallbacks(Runnable);
        if (popupWindow!=null){
            popupWindow.dismiss();
        }
        super.onDestroy();
    }

    //接口请求返回
    @Override
    public void View(String o) {
        if (Interface.equals("登录")){
            Gson gson=new Gson();
            BaseBean baseBean=gson.fromJson(o, BaseBean.class);
            if (baseBean.code==200){
                LogeBean bean = gson.fromJson(o, LogeBean.class);
                SharedPreferencesUtils.setParam(MainActivity.this,"TOToken",bean.data.token+"");
                SharedPreferencesUtils.setParam(MainActivity.this,"ToId",bean.data.id+"");
                MainActivity_isLogin.setVisibility(View.GONE);
                MainActivity_isLogin1.setVisibility(View.VISIBLE);
                String ToPhone= (String) SharedPreferencesUtils.getParam(MainActivity.this,"TOPhone","");
                MainActivity_isLogin1_phone.setText(ToPhone+"");
                Interface="个人信息";
                presenter.presenter(null, "/api/user/info?", "GET", DataUtile.getTOToken() + "");
            }else {
                MainActivity_isLogin.setVisibility(View.VISIBLE);
                MainActivity_isLogin1.setVisibility(View.GONE);
                SharedPreferencesUtils.setParam(MainActivity.this,"TOToken","");
                SharedPreferencesUtils.setParam(MainActivity.this,"ToId","");
                Toast.makeText(MainActivity.this,baseBean.msg,Toast.LENGTH_SHORT).show();
            }
        }else if (Interface.equals("个人信息")){
            Interface="";
            Gson gson = new Gson();
            PersonalInformationBean personalInformationBean = gson.fromJson(o, PersonalInformationBean.class);
            if (personalInformationBean.getCode() == 200) {
                PersonalInformationBean.DataDTO dataDTO = personalInformationBean.getData();
                //显示或隐藏图标
                if (dataDTO.getHideIcon() == 1) {//0-关闭 1-开启
                    MainActivity_HideTheDisplaySwitch.setText("显示");
                } else {
                    MainActivity_HideTheDisplaySwitch.setText("隐藏");
                }
                //行走轨迹
                if (dataDTO.getWalkTrack() == 1) {
                    TOIsWalkingTrajectory=true;
                    MainActivity_WalkingTrajectorySwitch.setText("关闭");
                } else {
                    TOIsWalkingTrajectory=false;
                    MainActivity_WalkingTrajectorySwitch.setText("开启");
                }
                //电话监听
                if (dataDTO.getPhoneListen() == 1) {
                    ToIsPhoneMonitoring=true;
                    MainActivity_PhoneMonitoringSwitch.setText("关闭");
                } else {
                    ToIsPhoneMonitoring=false;
                    MainActivity_PhoneMonitoringSwitch.setText("开启");
                }
                //短信监听
                if (dataDTO.getSmsListen() == 1) {
                    TOIsSMS=true;
                    MainActivity_SMSSwitch.setText("关闭");
                } else {
                    TOIsSMS=false;
                    MainActivity_SMSSwitch.setText("开启");
                }
                //通话记录
                if (dataDTO.getCallRecords() == 1) {
                    TOIsCallLog=true;
                    MainActivity_CallLogSwitch.setText("关闭");
                } else {
                    TOIsCallLog=false;
                    MainActivity_CallLogSwitch.setText("开启");
                }
                //通讯录变化
                if (dataDTO.getContactsChange()==1){
                    TOIsPhoneMonitorings=true;
                    MainActivity_ContactsSwitch.setText("关闭");
                }else {
                    TOIsPhoneMonitorings=false;
                    MainActivity_ContactsSwitch.setText("开启");
                }
                //手机行为记录
                if (dataDTO.getMobileBehavior()==1){
                    TOisHistory=true;
                    MainActivity_HistorySwitch1.setText("关闭");
                }else {
                    TOisHistory=false;
                    MainActivity_HistorySwitch1.setText("开启");
                }
                //实时同屏
                if (dataDTO.getLiveScreen()==1){
                    ISSameScreen = true;
                }else {
                    ISSameScreen = false;
                }
                //实时视频
                if (dataDTO.getLiveVideo()==1){
                    ISOpenScreenRecording = true;
                }else {
                    ISOpenScreenRecording = false;
                }
                //实施音频
                if (dataDTO.getLiveAudio()==1){
                    ISOpenScreenRecording2 = true;
                }else {
                    ISOpenScreenRecording2=false;
                }
            }
        }
    }
    public void SetToLogin() {
        androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请登录对方账号后进行操作")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 远端用户因为权限等问题没有开启时，弹框通知
     */
    AlertDialog dialog;
    public void SetPermissions() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("因对方没有开启权限或关闭功能,导致远程无法正常执行,请确认开启后，重新点击")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示权限已开启  false-表示没有改权限
     */
    public boolean lacksPermissions(String[] Requst) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : Requst) {
                if (lacksPermission(permission)) {
                    ActivityCompat.requestPermissions(MainActivity.this, Requst, REQUEST_CODE);
                    return false;
                }
            }
            return true;
        }else {
            return true;
        }
    }
    /**
     * 判断是否缺少权限
     */
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(MainActivity.this, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
    /**
     * 成功效果，动图
     */
    public void getpopupwindow(Activity activity, View view) {
        //弹出选择对比列表的框
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.successpopupwindow, null);
        ImageView successpopupwindow1_image=(ImageView) customView.findViewById(R.id.successpopupwindow1_image);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout successpopupwindow1_LinearLayout=(LinearLayout) customView.findViewById(R.id.successpopupwindow1_LinearLayout);
        successpopupwindow1_LinearLayout.setBackgroundResource(R.drawable.shopactivity_popuwindow1);
        try {
            Glide.with(MainActivity.this).asGif().load(R.drawable.lodings).into(successpopupwindow1_image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        popupWindow = new PopupWindow(customView, 300, 300);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0x00);
        //为了让pop弹出来点击外部不消失且不会穿透下部，把pop传递给activity的dispatchTouchEvent事件去消费点击事件
        MainActivity.this.setComparePop(popupWindow);
        handlerCode=15;
        mHandler.postDelayed(Runnable,1000);
    }
    private Handler mHandler=new Handler();
    private int handlerCode=15;
    private Runnable Runnable = new Runnable() {
        @Override
        public void run() {
            handlerCode--;
            if (popupWindow!=null){
                if (handlerCode>0){
                    mHandler.postDelayed(this,1000);
                }else if (handlerCode==0){
                    MainActivity1_HuoqRelativeLayoutImage.setOnClickListener(MainActivity.this);
                    MainActivity1_ScreenRecordingSwitch.setOnClickListener(MainActivity.this);
                    MainActivity1_ScreenRecordingSwitch2.setOnClickListener(MainActivity.this);
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("对方并未登录APP,请确定登录后进行操作")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHandler.removeCallbacks(Runnable);
                                    if (popupWindow!=null){
                                        popupWindow.dismiss();
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        }
    };
    public void setComparePop(PopupWindow pop) {
        this.popupWindow = pop;
    }
    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupWindow!=null){
                popupWindow.dismiss();
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}