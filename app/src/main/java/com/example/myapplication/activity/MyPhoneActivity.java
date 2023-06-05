package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.HomeActivity1;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.MyPhone.ElectronicFenceActivity;
import com.example.myapplication.activity.MyPhone.HistoryActivity;
import com.example.myapplication.activity.MyPhone.RecordofoperationsActivity;
import com.example.myapplication.activity.MyPhone.WalkingTrajectoryHistoryActivity;
import com.example.myapplication.bean.ContactsBean;
import com.example.myapplication.bean.DistalEndBean;
import com.example.myapplication.bean.HistoryBean;
import com.example.myapplication.bean.PersonalInformationBean;
import com.example.myapplication.bean.PhoneBean;
import com.example.myapplication.bean.ReceiveBean;
import com.example.myapplication.bean.ReceiveResultBean;
import com.example.myapplication.bean.SMSBean;
import com.example.myapplication.bean.TouchEvent;
import com.example.myapplication.bean.WebSocketEvent;
import com.example.myapplication.dao.DbDao;
import com.example.myapplication.service.AutoTouchService;
import com.example.myapplication.service.CallLogService;
import com.example.myapplication.service.MusicService;
import com.example.myapplication.service.PhoneService;
import com.example.myapplication.service.ServiceSynchContract;
import com.example.myapplication.service.SmsService;
import com.example.myapplication.service.TRTCService;
import com.example.myapplication.service.ToolKitService;
import com.example.myapplication.service.WalkingTrajectoryService;
import com.example.myapplication.service.WebSocketService;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.JumpPermissionManagement;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.liteav.device.TXDeviceManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.tencent.trtc.debug.Constant;
import com.tencent.trtc.debug.GenerateTestUserSig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本机操作
 */
public class MyPhoneActivity extends BaseActivity1 implements ContractInterface.View {
    private LinearLayout incloud_finish;//返回上级页面
    private TextView incloud_title;//头部标签
    private TextView MyPhoneActivity_Phone;//手机号码
    private String Phones = "";
    //隐藏图标
    private RelativeLayout MyPhoneActivity_HideTheDisplayRelativeLayout;
    private ImageView MyPhoneActivity_HideTheDisplaySwitch;
    private boolean IsShowHide = false;
    private String SourceBounds;//app在屏幕中的坐标位置
    private int MyPhoneActivity_REQUEST = 123;

    private boolean IsContacts = false;
    //权限申请
    private final int REQUEST_CODE = 1;
    //权限申请数组
    private String[] RequstString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.READ_CONTACTS, //联系人权限
            Manifest.permission.WRITE_CONTACTS,//联系人权限
    };
    //电话状态监听
    private String[] PhoneMonitoringRequstString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.CALL_PHONE,//电话权限申请
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };
    //通信记录监听
    private String[] CallLogString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.READ_CONTACTS, //联系人权限
            Manifest.permission.WRITE_CONTACTS,//联系人权限
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };
    //短信监听权限
    private String[] SMSString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS
    };
    //电子围栏权限请求
    private String[] ElectronicFenceString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
    };
    private String[] WalkingTrajectoryString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    //实时同屏
    String[] SameScreenString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.CAMERA,//相机权限
            Manifest.permission.RECORD_AUDIO, //允许程序录制音频
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.MODIFY_AUDIO_SETTINGS,//允许程序修改全局音频设置
    };
    //实时视频或音频
    String[] ScreenRecordingRequstString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.CAMERA,//相机权限
            Manifest.permission.RECORD_AUDIO, //允许程序录制音频
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.MODIFY_AUDIO_SETTINGS,//允许程序修改全局音频设置
    };
    //悬浮窗权限
    private static final int CODE_WINDOW = 0;
    private static final int HIDE_WINDOW = 11;
    private static final int PhoneMonitoringCODE_WINDOW = 21;
    private static final int CALLLOGCODE_WINDOW = 31;
    private static final int SMSCODE_WINDOW = 41;
    private static final int PHoneMonitoringCODE_WINDOW1 = 51;
    private static final int ElectronicFenceCODE_WINDOW = 61;
    private static final int WalkingTrajectoryCODE_WINDOW = 71;
    private static final int SameScreenREQUEST_CODE = 81;
    private static final int ScreenINT = 91;
    private boolean IsPhoneMonitoring = false;
    private boolean IsCallLog = false;
    private boolean IsSMS = false;
    //判断点击哪个按钮
    String onClic = "";
    //手机软件使用监听
    private TextView MyPhoneActivity_PhoneSoftwareHistory;
    private ImageView MyPhoneActivity_HistorySwitch1;
    private boolean isHistory = false;
    //电子围栏
    private RelativeLayout MyPhoneActivity_ElectronicFence;
    //行走轨迹
    private TextView MyPhoneActivity_WalkingTrajectoryHistory;
    private ImageView MyPhoneActivity_WalkingTrajectorySwitch;
    private boolean isWalkingTrajectory = false;
    private String EventMessage = "";
    //手机锁屏保护
    private RelativeLayout MyPhoneActivity_LockScreenRelativeLayout;
    private ImageView MyPhoneActivity_LockScreenSwitch;
    private boolean isLockScreen;
    private boolean ISOpenSameScreen = false;//是否开启实时同屏
    private boolean ISOpenScreenRecording = false;//是否开启实时同屏
    private boolean ISOpenScreenRecording2 = false;//是否开启实时同屏
    private MediaProjectionManager mProjectionManager;
    //音视频相关
    private static TRTCCloud mTRTCCloud = TRTCCloud.sharedInstance(ApplicTion.mContext);
    private boolean isPause = false;
    private String CloseOROpen = "";
    private ContractInterface.Presenter presenter;
    private String RequestType = "本机配置获取";
    private int REQUES_CODE = 952;
    private String EventBusRequest = "";
    private TXCloudVideoView MyPhoneActivity_TXCloudVideoView;
//    //操作记录
//    private RelativeLayout MyPhoneActivity_Recordofoperations;
    //保存短信数据
    private DbDao mDbDao;
    @Override
    public int getLayout() {
        return R.layout.activity_remote;
    }

    private int liveScreen = 0;
    private int liveAudio = 0;
    private int liveAudios = 0;
    private int liveVideo = 0;
    private int liveVideos = 0;
    private DistalEndBean distalEndBean;
    //音频
    private static TRTCCloud AudioMTRTCCloud;
    //视频
    private static TRTCCloud VideoCallingenterMTRTCCloud;
    private TXDeviceManager mTXDeviceManager;
    private boolean mIsFrontCamera = false;
    private String ToPhone = "186";
    private AppCompatButton MyPhoneActivity_EDLogin;
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
    public void setcCreate(Bundle savedInstanceState) {
        WebSocketService.closeConnect();
        WebSocketService.initSocketClient(DataUtile.getMyPhone());
        mDbDao = new DbDao(this);
        EventBus.getDefault().register(this);
        Phones = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", "");
        IsContacts = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsContacts", false);
        IsPhoneMonitoring = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
        IsCallLog = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsCallLog", false);
        IsSMS = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsSMS", false);
        IsShowHide = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsShowHide", false);
        isWalkingTrajectory = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
        isLockScreen = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "isLockScreen", false);
        ISOpenSameScreen = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
        initFind();
        initClick();
        String Hide = getIntent().getStringExtra("Hide");
        if (Hide != null && !Hide.isEmpty()) {
            onClic = "隐藏图标";
            RequestType = "隐藏图标";
            Map<String, Object> map1 = new HashMap<>();
            map1.put("field", "hide_icon");
            map1.put("value", "0");
            presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
            if (distalEndBean != null) {
                //返回结果
                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                receiveResultBean.setType("hideIcon");
                receiveResultBean.setResult(2);
                receiveResultBean.setTypes("回传");
                String value = new Gson().toJson(receiveResultBean);
                Map<String, Object> map = new HashMap<>();
                map.put("field", "websocket");
                map.put("value", value);
                map.put("mobile", distalEndBean.getPhone() + "");
                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
            }
            DataUtile.isShow = false;
            SharedPreferencesUtils.setParam(this, "IsShowHide", false);
            MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
            chaneIcon1();

        }
        isHistory = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "isHistory", false);
        if (DataUtile.isHuaWei()) {
            if (IsShowHide) {
                DataUtile.isShow = true;
                IsShowHide = true;
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
            } else {
                IsShowHide = false;
                DataUtile.isShow = false;
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
            }
        } else {
            if (IsShowHide) {
                DataUtile.isShow = true;
                IsShowHide = true;
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
            } else {
                IsShowHide = false;
                DataUtile.isShow = false;
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
            }
        }

        if (IsContacts) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(RequstString)) {
                    ServiceSynchContract.start(MyPhoneActivity.this);
                } else {
                    Intent intent = new Intent(MyPhoneActivity.this, ServiceSynchContract.class);
                    intent.setPackage(getPackageName());
                    stopService(intent);
                }
            } else {
                Intent intent = new Intent(MyPhoneActivity.this, ServiceSynchContract.class);
                intent.setPackage(getPackageName());
                stopService(intent);
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, ServiceSynchContract.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (IsPhoneMonitoring) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(PhoneMonitoringRequstString)) {
                    PhoneService.start(MyPhoneActivity.this);
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    MyPhoneActivity.this.sendBroadcast(intent);
                }
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, PhoneService.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (IsCallLog) {
            if (lacksPermissions(CallLogString)) {
                CallLogService.start(MyPhoneActivity.this);
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, CallLogService.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (IsSMS) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(SMSString)) {
                    SmsService.start(MyPhoneActivity.this);
                }
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, SmsService.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (isHistory) {
            if (hasPermissionToReadNetworkStats()) {
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_open);
                MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.VISIBLE);
            } else {
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
            }
        } else {
            MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
            MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
        }
        if (isWalkingTrajectory) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(WalkingTrajectoryString)) {
                    MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_open);

                    MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.VISIBLE);
                } else {
                    MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                    MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                }
            } else {
                MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
            }
        } else {
            MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
            MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
        }
        if (isLockScreen) {
            MyPhoneActivity_LockScreenSwitch.setBackgroundResource(R.mipmap.switch_open);
        } else {
            MyPhoneActivity_LockScreenSwitch.setBackgroundResource(R.mipmap.switch_close);
        }
        //获取个人信息
        RequestType = "个人信息";
        presenter.presenter(null, "/api/user/info?", "GET", DataUtile.getTOToken() + "");
    }

    @Override
    protected void onDestroy() {
        Intent intents = new Intent(MyPhoneActivity.this, WebSocketService.class);
        intents.setPackage(getPackageName());
        stopService(intents);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReciverTouchEvent(WebSocketEvent eve) {
        String event = eve.getMessage();
        LogUtils.e(event+"************");
        if (!event.equals("连接成功")) {
            if (event.equals("辅助功能已开启")) {
                EventMessage = event;
            } else{
                Gson gson = new Gson();
                ReceiveBean receiveBean = gson.fromJson(event, ReceiveBean.class);
                ToPhone = receiveBean.getMobile() + "";
                SharedPreferencesUtils.setParam(this,"ToPhone","");
                if (!receiveBean.getContent().isEmpty()){
                    distalEndBean = gson.fromJson(receiveBean.getContent(), DistalEndBean.class);
                    if (distalEndBean != null) {
                        if (distalEndBean.getField().equals("hide_icon")) {//显示或隐藏
                            EventBusRequest = "隐藏图标";
                            onClic = "隐藏图标";
                            RequestType = "隐藏图标";
                            //显示或隐藏图标
                            if (distalEndBean.getValue().equals("1")) {//0-关闭 1-开启
                                if (DataUtile.isHuaWei()) {
                                    if (isAccessibilitySettingsOn()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                                && Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                            isHistory=true;
                                            TouchEvent.postCLOSESAction();
                                            Map<String, Object> map1 = new HashMap<>();
                                            map1.put("field", "hide_icon");
                                            map1.put("value", "0");
                                            presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                            //返回结果
                                            ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                            receiveResultBean.setType("hideIcon");
                                            receiveResultBean.setResult(1);
                                            receiveResultBean.setTypes("回传");
                                            String value = gson.toJson(receiveResultBean);
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("field", "websocket");
                                            map.put("value", value);
                                            map.put("mobile", distalEndBean.getPhone() + "");
                                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                            DataUtile.isShow = true;
                                            SharedPreferencesUtils.setParam(this, "IsShowHide", true);
                                            MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
                                            chaneIcon1();
                                        } else {
                                            showTipDialog(HIDE_WINDOW, "隐藏图标功能");
                                        }
                                    } else {
                                        goAccess("隐藏图标");
                                    }
                                } else if (!DataUtile.isHuaWei()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                            && Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        isHistory=true;
                                        Map<String, Object> map1 = new HashMap<>();
                                        map1.put("field", "hide_icon");
                                        map1.put("value", "0");
                                        presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                        DataUtile.isShow = false;
                                        SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                                        MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                                        chaneIcon1();
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("hideIcon");
                                        receiveResultBean.setResult(0);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");

                                    } else {
                                        showTipDialog(HIDE_WINDOW, "隐藏图标功能");
                                    }
                                }
                            } else {
                                isHistory=false;
                                Map<String, Object> map1 = new HashMap<>();
                                map1.put("field", "hide_icon");
                                map1.put("value", "0");
                                presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("hideIcon");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                DataUtile.isShow = false;
                                SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                                chaneIcon();
                            }
                        } else if (distalEndBean.getField().equals("VIDEOCalling_muteVideo")) {
                            if (distalEndBean.getValue().equals("1")) {
                                VideoCallingenterMTRTCCloud.stopLocalPreview();
                            } else {
                                VideoCallingenterMTRTCCloud.startLocalPreview(mIsFrontCamera, MyPhoneActivity_TXCloudVideoView);
                            }
                        } else if (distalEndBean.getField().equals("VIDEOCalling_muteAudio")) {
                            if (distalEndBean.getValue().equals("1")) {
                                VideoCallingenterMTRTCCloud.muteLocalAudio(false);
                            } else {
                                VideoCallingenterMTRTCCloud.muteLocalAudio(true);
                            }
                        } else if (distalEndBean.getField().equals("VIDEOCalling_switchCamera")) {
                            if (distalEndBean.getValue().equals("1")) {
                                mTXDeviceManager.switchCamera(false);
                            } else {
                                mTXDeviceManager.switchCamera(true);
                            }
                        } else if (distalEndBean.getField().equals("walk_track")) {
                            //行走轨迹
                            if (distalEndBean.getValue().equals("1")) {
                                if (lacksPermissions(WalkingTrajectoryString)) {
                                    if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("walkTrack");
                                        receiveResultBean.setResult(1);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");

                                        isWalkingTrajectory = true;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", true);
                                        MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_open);
                                        MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.VISIBLE);
                                    } else {
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("walkTrack");
                                        receiveResultBean.setResult(0);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");

                                        isWalkingTrajectory = false;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                                        MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                                        MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                                    }
                                } else {
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("walkTrack");
                                    receiveResultBean.setResult(0);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");

                                    isWalkingTrajectory = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                                    MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                                    MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                                }
                            } else {
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("walkTrack");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                isWalkingTrajectory = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                                MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                                MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                            }
                        } else if (distalEndBean.getField().equals("phone_listen")) {
                            //电话监听
                            if (distalEndBean.getValue().equals("1")) {
                                if (lacksPermissions(PhoneMonitoringRequstString)) {
                                    if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        IsPhoneMonitoring = true;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", true);
                                        PhoneService.start(MyPhoneActivity.this);
                                        Intent intent = new Intent(Intent.ACTION_EDIT);
                                        MyPhoneActivity.this.sendBroadcast(intent);
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("phoneListen");
                                        receiveResultBean.setResult(1);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    } else {
                                        Intent intent = new Intent(this, PhoneService.class);
                                        intent.setPackage(getPackageName());
                                        stopService(intent);
                                        IsPhoneMonitoring = false;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("phoneListen");
                                        receiveResultBean.setResult(0);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    }
                                } else {
                                    Intent intent = new Intent(this, PhoneService.class);
                                    intent.setPackage(getPackageName());
                                    stopService(intent);
                                    IsPhoneMonitoring = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("phoneListen");
                                    receiveResultBean.setResult(0);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                }
                            } else {
                                Intent intent = new Intent(this, PhoneService.class);
                                intent.setPackage(getPackageName());
                                stopService(intent);
                                IsPhoneMonitoring = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("phoneListen");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("sms_listen")) {
                            //短信监听
                            if (distalEndBean.getValue().equals("1")) {
                                if (lacksPermissions(SMSString)) {
                                    if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        List<String> ReceiptList = getSmsInPhone();//收件箱
                                        List<String> SendList = getSmsInPhone1();//收件箱

                                        IsSMS = true;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", true);
                                        SmsService.start(MyPhoneActivity.this);
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("smsListen");
                                        receiveResultBean.setResult(1);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    } else {
                                        Intent intent = new Intent(this, SmsService.class);
                                        intent.setPackage(getPackageName());
                                        stopService(intent);
                                        IsSMS = false;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("smsListen");
                                        receiveResultBean.setResult(0);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    }
                                } else {
                                    Intent intent = new Intent(this, SmsService.class);
                                    intent.setPackage(getPackageName());
                                    stopService(intent);
                                    IsSMS = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("smsListen");
                                    receiveResultBean.setResult(0);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                }
                            } else {
                                Intent intent = new Intent(this, SmsService.class);
                                intent.setPackage(getPackageName());
                                stopService(intent);
                                IsSMS = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("smsListen");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("call_records")) {
                            //通话记录
                            if (distalEndBean.getValue().equals("1")) {
                                if (lacksPermissions(CallLogString)) {
                                    if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        List<String> list = getCallLogs();
                                        if (list != null && list.size() > 0) {
                                            List<String> CallLogList = list;
                                            String[] strings = new String[CallLogList.size()];
                                            for (int i = 0; i < CallLogList.size(); i++) {
                                                strings[i] = CallLogList.get(i);
                                            }
                                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "CallLogs", strings);
                                        } else {
                                            String[] strings = new String[0];
                                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "CallLogs", strings);
                                        }
                                        IsCallLog = true;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", true);
                                        CallLogService.start(MyPhoneActivity.this);
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("callRecords");
                                        receiveResultBean.setResult(1);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    } else {
                                        showTipDialog(CALLLOGCODE_WINDOW, "通话记录");
                                    }
                                } else {
                                    Intent intent = new Intent(this, CallLogService.class);
                                    intent.setPackage(getPackageName());
                                    stopService(intent);
                                    IsCallLog = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("callRecords");
                                    receiveResultBean.setResult(0);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                }
                            } else {
                                Intent intent = new Intent(this, CallLogService.class);
                                intent.setPackage(getPackageName());
                                stopService(intent);
                                IsCallLog = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("callRecords");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("contacts_change")) {
                            //通讯录变化
                            if (distalEndBean.getValue().equals("1")) {
                                if (lacksPermissions(RequstString)) {
                                    // 申请悬浮窗权限
                                    if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        readCotacts();
                                        IsContacts = true;
                                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", true);
                                        ServiceSynchContract.start(MyPhoneActivity.this);
                                    } else {
                                        showTipDialog(CODE_WINDOW, "通讯录变化功能");
                                        //返回结果
                                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                        receiveResultBean.setType("contactsChange");
                                        receiveResultBean.setResult(0);
                                        receiveResultBean.setTypes("回传");
                                        String value = gson.toJson(receiveResultBean);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("field", "websocket");
                                        map.put("value", value);
                                        map.put("mobile", distalEndBean.getPhone() + "");
                                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    }
                                } else {
                                    ActivityCompat.requestPermissions(MyPhoneActivity.this, RequstString, REQUEST_CODE);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("contactsChange");
                                    receiveResultBean.setResult(0);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                }
                            } else {
                                Intent intent = new Intent(this, ServiceSynchContract.class);
                                intent.setPackage(getPackageName());
                                stopService(intent);
                                IsContacts = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("contactsChange");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("mobile_behavior")) {
                            LogUtils.e((distalEndBean.getValue().equals("1")));
                            //手机行为记录
                            if (distalEndBean.getValue().equals("1")) {
                                if (hasPermissionToReadNetworkStats()) {
                                    isHistory = true;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", true);
                                    MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_open);
                                    MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.VISIBLE);
                                    //获取手机行为记录数据并上传
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        setHistoy(1);
                                    }
                                } else {//请求用户打开行为记录
                                    isHistory = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", false);
                                    MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
                                    MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
                                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("mobileBehavior");
                                    receiveResultBean.setResult(0);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                }
                            } else {
                                isHistory = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", false);
                                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
                                MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("mobileBehavior");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("live_screen")) {
                            //是否开启同屏
                            if (distalEndBean.getValue().equals("1")) {
                                EventBusRequest = "开启同屏请求1";
                                onClic = "实时同屏";
                                ToolKitService.start(MyPhoneActivity.this);
                                if (lacksPermissions(SameScreenString, ScreenINT)) {
//                            if (!isAccessibilitySettingsOn()) {
//                                goAccess("实时同屏");
//                            } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                            && !Settings.canDrawOverlays(MyPhoneActivity.this)) {
                                        showTipDialog(SameScreenREQUEST_CODE, "实时同屏");
                                    } else {
                                        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                                        Intent intent = mProjectionManager.createScreenCaptureIntent();
                                        PackageManager packageManager = getPackageManager();
                                        if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                                            enterRoom();
                                            screenCapture();
                                            //返回结果
                                            ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                            receiveResultBean.setType("liveScreen");
                                            receiveResultBean.setResult(1);
                                            receiveResultBean.setTypes("回传");
                                            String value = gson.toJson(receiveResultBean);
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("field", "websocket");
                                            map.put("value", value);
                                            map.put("mobile", distalEndBean.getPhone() + "");
                                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                            ISOpenSameScreen = true;
                                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                                        } else {
                                            showTipDialog("实时同屏");
                                        }
                                    }
                                }
//                        } else {
//                            //请求权限
//                            ActivityCompat.requestPermissions(MyPhoneActivity.this, SameScreenString, REQUEST_CODE);
//                        }
                            } else if (distalEndBean.getValue().equals("3")) {
                                LogUtils.e("关闭 exitRoom");
                                exitRoom();
                            } else {
                                ISOpenSameScreen = false;
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("liveScreen");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("live_video")) {
                            //是否开启视频
                            if (distalEndBean.getValue().equals("1")) {
                                EventBusRequest = "是否开启视频1";
                                onClic = "";
                                ToolKitService.start(MyPhoneActivity.this);
                                if (lacksPermissions(ScreenRecordingRequstString)) {
                                    String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "1256");
                                    String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", MyId + "");
                                    String MyIds = "123673" + MyId;
                                    if (MyIds.length() > 7) {
                                        MyIds = "";
                                        MyIds = "12367" + MyId;
                                    }
                                    VideoCallingenterRoom(MyIds, MyPhone);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("liveVideo");
                                    receiveResultBean.setResult(1);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    ISOpenScreenRecording = true;
                                } else {
                                    //请求权限
                                    ActivityCompat.requestPermissions(MyPhoneActivity.this, ScreenRecordingRequstString, REQUEST_CODE);
                                }
                            }else  if (distalEndBean.getValue().equals("3")) {
                                LogUtils.e("关闭 VideoCallingenExitRoom");
                                VideoCallingenExitRoom();
                            }else {
                                ISOpenScreenRecording = false;
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("liveVideo");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        } else if (distalEndBean.getField().equals("live_audio")) {
                            //是否开启音频
                            if (distalEndBean.getValue().equals("1")) {
                                EventBusRequest = "是否开启音频1";
                                onClic = "";
                                ToolKitService.start(MyPhoneActivity.this);
                                if (lacksPermissions(ScreenRecordingRequstString)) {
                                    String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "1256");
                                    String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", MyId + "");
                                    String MyIds = "124673" + MyId;
                                    if (MyIds.length() > 7) {
                                        MyIds = "";
                                        MyIds = "12467" + MyId;
                                    }
                                    AudioexitRoom();
                                    AudioEnterRoom(MyPhone, MyIds);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("liveAudio");
                                    receiveResultBean.setResult(1);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    ISOpenScreenRecording2 = true;
                                } else {
                                    ActivityCompat.requestPermissions(MyPhoneActivity.this, ScreenRecordingRequstString, REQUEST_CODE);
                                }
                            } else if (distalEndBean.getValue().equals("3")) {
                                AudioexitRoom();
                            } else {
                                ISOpenScreenRecording2 = false;
                                //返回结果
                                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                receiveResultBean.setType("liveAudio");
                                receiveResultBean.setResult(2);
                                receiveResultBean.setTypes("回传");
                                String value = gson.toJson(receiveResultBean);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "websocket");
                                map.put("value", value);
                                map.put("mobile", distalEndBean.getPhone() + "");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Phones = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", "");
        IsContacts = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsContacts", false);
        IsPhoneMonitoring = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
        IsCallLog = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsCallLog", false);
        IsSMS = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsSMS", false);
        IsShowHide = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "IsShowHide", false);
        isHistory = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "isHistory", false);
        isWalkingTrajectory = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
        isLockScreen = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "isLockScreen", false);
        ISOpenSameScreen = (boolean) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
        if (IsContacts) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(RequstString)) {
                    ServiceSynchContract.start(MyPhoneActivity.this);
                }
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, ServiceSynchContract.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (IsPhoneMonitoring) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(PhoneMonitoringRequstString)) {
                    PhoneService.start(MyPhoneActivity.this);
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    MyPhoneActivity.this.sendBroadcast(intent);
                }
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, PhoneService.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (IsCallLog) {
            if (lacksPermissions(CallLogString)) {
                CallLogService.start(MyPhoneActivity.this);
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, CallLogService.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (IsSMS) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(SMSString)) {
                    SmsService.start(MyPhoneActivity.this);
                }
            }
        } else {
            Intent intent = new Intent(MyPhoneActivity.this, SmsService.class);
            intent.setPackage(getPackageName());
            stopService(intent);
        }
        if (isHistory) {
            if (hasPermissionToReadNetworkStats()) {
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_open);
                MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.VISIBLE);
            } else {
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
            }
        } else {
            MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
            MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
        }
        if (isWalkingTrajectory) {
            if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                if (lacksPermissions(WalkingTrajectoryString)) {
                    MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_open);


                    MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.VISIBLE);
                } else {
                    MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                    MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                }
            } else {
                MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
            }
        } else {
            MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);

            MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
        }
        if (isLockScreen) {
            MyPhoneActivity_LockScreenSwitch.setBackgroundResource(R.mipmap.switch_open);
        } else {
            MyPhoneActivity_LockScreenSwitch.setBackgroundResource(R.mipmap.switch_close);
        }
    }

    //初始化
    private void initFind() {
        presenter = new MyPresenter(this);
        incloud_finish = findViewById(R.id.incloud_finish);
        incloud_title = findViewById(R.id.incloud_title);
        MyPhoneActivity_Phone = findViewById(R.id.MyPhoneActivity_Phone);
        Phones = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", "");
        MyPhoneActivity_Phone.setText(Phones + "");
        incloud_title.setText("本机登录");
        //隐藏图标
        MyPhoneActivity_HideTheDisplayRelativeLayout = findViewById(R.id.MyPhoneActivity_HideTheDisplayRelativeLayout);
        MyPhoneActivity_HideTheDisplaySwitch = findViewById(R.id.MyPhoneActivity_HideTheDisplaySwitch);
//        //操作记录
//        MyPhoneActivity_Recordofoperations = findViewById(R.id.MyPhoneActivity_Recordofoperations);
        //手机软件使用监听
        MyPhoneActivity_HistorySwitch1 = findViewById(R.id.MyPhoneActivity_HistorySwitch1);
        MyPhoneActivity_PhoneSoftwareHistory = findViewById(R.id.MyPhoneActivity_PhoneSoftwareHistory);
        //电子围栏
        MyPhoneActivity_ElectronicFence = findViewById(R.id.MyPhoneActivity_ElectronicFence);
        //行走轨迹
        MyPhoneActivity_WalkingTrajectoryHistory = findViewById(R.id.MyPhoneActivity_WalkingTrajectoryHistory);
        MyPhoneActivity_WalkingTrajectorySwitch = findViewById(R.id.MyPhoneActivity_WalkingTrajectorySwitch);
        //手机锁屏保护
        MyPhoneActivity_LockScreenRelativeLayout = findViewById(R.id.MyPhoneActivity_LockScreenRelativeLayout);
        MyPhoneActivity_LockScreenSwitch = findViewById(R.id.MyPhoneActivity_LockScreenSwitch);
        //退出登录
        MyPhoneActivity_EDLogin=findViewById(R.id.MyPhoneActivity_EDLogin);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (IsShowHide) {
                Intent intent = new Intent(MyPhoneActivity.this, HomeActivity1.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MyPhoneActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //点击事件
    private void initClick() {
        //返回
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsShowHide) {
                    Intent intent = new Intent(MyPhoneActivity.this, HomeActivity1.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MyPhoneActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //隐藏图标
        MyPhoneActivity_HideTheDisplayRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClic = "隐藏图标";
                RequestType = "隐藏图标";
                Gson gson = new Gson();
                if (DataUtile.isHuaWei()) {
                    if (!isAccessibilitySettingsOn() && !EventMessage.equals("辅助功能已开启")) {
                        goAccess("隐藏图标");
                    } else {
                        if (!Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            showTipDialog(HIDE_WINDOW, "隐藏图标功能");
                        } else {
                            if (IsShowHide) {
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsShowHide", false);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "hide_icon");
                                map.put("value", "0");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            } else {
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsShowHide", true);
                                Map<String, Object> map = new HashMap<>();
                                map.put("field", "hide_icon");
                                map.put("value", "1");
                                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                            }
                        }
                    }
                } else if (!DataUtile.isHuaWei()) {
                    if (!Settings.canDrawOverlays(MyPhoneActivity.this)) {
                        showTipDialog(HIDE_WINDOW, "隐藏图标功能");
                    } else {
                        if (IsShowHide) {
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsShowHide", false);
                            //返回结果
                            Map<String, Object> map = new HashMap<>();
                            map.put("field", "hide_icon");
                            map.put("value", "0");
                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        } else {
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsShowHide", true);
                            Map<String, Object> map = new HashMap<>();
                            map.put("field", "hide_icon");
                            map.put("value", "1");
                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        }
                    }

                }
            }
        });
//        //操作记录
//        MyPhoneActivity_Recordofoperations.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClic = "操作记录";
//                Intent intent = new Intent(MyPhoneActivity.this, RecordofoperationsActivity.class);
//                intent.putExtra("Token", DataUtile.getMyToken() + "");
//                startActivity(intent);
//            }
//        });
        //手机软件使用记录
        MyPhoneActivity_HistorySwitch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClic = "手机软件使用记录";
                RequestType = "手机行为记录";
                if (hasPermissionToReadNetworkStats()) {
                    if (isHistory) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "mobile_behavior");
                        map.put("value", "0");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            setHistoy(0);
//                        }
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "mobile_behavior");
                        map.put("value", "1");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            setHistoy(1);
                        }
                    }
                } else {
                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                }
            }
        });
        //点击跳转手机行为记录历史
        MyPhoneActivity_PhoneSoftwareHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPhoneActivity.this, HistoryActivity.class);
                intent.putExtra("Token", DataUtile.getMyToken() + "");
                intent.putExtra("type", "MyPhoneActivity");
                startActivity(intent);
            }
        });
        //电子围栏
        MyPhoneActivity_ElectronicFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClic = "电子围栏";
                if (lacksPermissions(ElectronicFenceString)) {
                    Intent intent = new Intent(MyPhoneActivity.this, ElectronicFenceActivity.class);
                    intent.putExtra("Token", DataUtile.getMyToken() + "");
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(MyPhoneActivity.this, ElectronicFenceString, ElectronicFenceCODE_WINDOW);
                }
            }
        });
        //行走轨迹
        MyPhoneActivity_WalkingTrajectorySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClic = "行走轨迹";
                RequestType = "行走轨迹";
                if (lacksPermissions(WalkingTrajectoryString)) {
                    if (!Settings.canDrawOverlays(MyPhoneActivity.this)) {
                        showTipDialog(WalkingTrajectoryCODE_WINDOW, "行走轨迹功能");
                        //       startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), SMSCODE_WINDOW);
                    } else {
                        if (isWalkingTrajectory) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("field", "walk_track");
                            map.put("value", "0");
                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("field", "walk_track");
                            map.put("value", "1");
                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(MyPhoneActivity.this, WalkingTrajectoryString, REQUEST_CODE);
                }
            }
        });
        //行走轨迹记录
        MyPhoneActivity_WalkingTrajectoryHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "116");
                Intent intent = new Intent(MyPhoneActivity.this, WalkingTrajectoryHistoryActivity.class);
                intent.putExtra("token", DataUtile.getMyToken() + "");
                intent.putExtra("fromId", toId);
                intent.putExtra("name", "测试");
                intent.putExtra("toId", toId);
                startActivity(intent);
            }
        });
        //手机锁屏保护
        MyPhoneActivity_LockScreenRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLockScreen) {
                    isLockScreen = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isLockScreen", false);
                    MyPhoneActivity_LockScreenSwitch.setBackgroundResource(R.mipmap.switch_close);
                } else {
                    isLockScreen = true;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isLockScreen", true);
                    MyPhoneActivity_LockScreenSwitch.setBackgroundResource(R.mipmap.switch_open);
                }
            }
        });
        //退出登录
        MyPhoneActivity_EDLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "MyPhone", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "MyPhone1", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "MyToken", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "MyId", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ToPhone", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "TOToken", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ToId", "");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this,"Address","");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this,"saveLat","");
                SharedPreferencesUtils.setParam(MyPhoneActivity.this,"saveLng","");
                if (IsShowHide) {
                    Intent intent = new Intent(MyPhoneActivity.this, HomeActivity1.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MyPhoneActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    //同步代码数据
    @Override
    public void View(String o) {
        if (RequestType.equals("个人信息")) {
            Gson gson = new Gson();
            PersonalInformationBean personalInformationBean = gson.fromJson(o, PersonalInformationBean.class);
            if (personalInformationBean.getCode() == 200) {
                PersonalInformationBean.DataDTO dataDTO = personalInformationBean.getData();
                //显示或隐藏图标
                if (dataDTO.getHideIcon() == 1) {//0-关闭 1-开启
                    if (DataUtile.isHuaWei()) {
                        if (isAccessibilitySettingsOn()) {
                            if (Settings.canDrawOverlays(this)) {
                                DataUtile.isShow = true;
                                SharedPreferencesUtils.setParam(this, "IsShowHide", true);
                            } else {
                                DataUtile.isShow = false;
                                SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                            }
                        } else {
                            DataUtile.isShow = false;
                            SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                        }
                    } else if (!DataUtile.isHuaWei()) {
                        DataUtile.isShow = true;
                        SharedPreferencesUtils.setParam(this, "IsShowHide", true);
                    }
                } else {
                    DataUtile.isShow = false;
                    SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                }
                //行走轨迹
                if (dataDTO.getWalkTrack() == 1) {
                    if (lacksPermissions(WalkingTrajectoryString)) {
                        if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            isWalkingTrajectory = true;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", true);
                            MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_open);

                            MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.VISIBLE);
                        } else {
                            isWalkingTrajectory = false;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                            MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                            MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                        }
                    } else {
                        isWalkingTrajectory = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                        MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                        MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                    }
                } else {
                    isWalkingTrajectory = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                    MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                    MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
                }
                //电话监听
                if (dataDTO.getPhoneListen() == 1) {
                    if (lacksPermissions(PhoneMonitoringRequstString)) {
                        if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            IsPhoneMonitoring = true;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", true);
                            PhoneService.start(MyPhoneActivity.this);
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            MyPhoneActivity.this.sendBroadcast(intent);
                        } else {
                            IsPhoneMonitoring = false;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                        }
                    } else {
                        IsPhoneMonitoring = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                    }
                } else {
                    IsPhoneMonitoring = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                }
                //短信监听
                if (dataDTO.getSmsListen() == 1) {
                    if (lacksPermissions(SMSString)) {
                        if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            List<String> ReceiptList = getSmsInPhone();//收件箱
                            String[] ReceipString = new String[ReceiptList.size()];
                            for (int i = 0; i < ReceiptList.size(); i++) {
                                ReceipString[i] = ReceiptList.get(i);
                            }
                            List<String> SendList = getSmsInPhone1();//收件箱
                            String[] SendString = new String[SendList.size()];
                            for (int i = 0; i < SendList.size(); i++) {
                                SendString[i] = SendList.get(i);
                            }
                            IsSMS = true;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", true);
                            SmsService.start(MyPhoneActivity.this);
                        } else {
                            IsSMS = false;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                        }
                    } else {
                        IsSMS = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                    }
                } else {
                    IsSMS = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                }
                //通话记录
                if (dataDTO.getCallRecords() == 1) {
                    if (lacksPermissions(CallLogString)) {
                        if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            List<String> list = getCallLogs();
                            if (list != null && list.size() > 0) {
                                List<String> CallLogList = list;
                                String[] strings = new String[CallLogList.size()];
                                for (int i = 0; i < CallLogList.size(); i++) {
                                    strings[i] = CallLogList.get(i);
                                }
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "CallLogs", strings);
                            } else {
                                String[] strings = new String[0];
                                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "CallLogs", strings);
                            }
                            IsCallLog = true;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", true);
                            CallLogService.start(MyPhoneActivity.this);
                        } else {
                            IsCallLog = false;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                        }
                    } else {
                        ActivityCompat.requestPermissions(MyPhoneActivity.this, CallLogString, REQUEST_CODE);
                    }
                } else {
                    IsCallLog = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                }
                //通讯录变化
                if (dataDTO.getContactsChange() == 1) {
                    if (lacksPermissions(RequstString)) {
                        // 申请悬浮窗权限
                        if (Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            readCotacts();
                            IsContacts = true;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", true);
                            ServiceSynchContract.start(MyPhoneActivity.this);
                        } else {
                            IsContacts = false;
                            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                        }
                    } else {
                        IsContacts = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                    }
                } else {
                    IsContacts = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                }
                //手机行为记录
                if (dataDTO.getMobileBehavior() == 1) {
                    if (hasPermissionToReadNetworkStats()) {
                        isHistory = true;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", true);
                        MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_open);
                        MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.VISIBLE);
                    } else {
                        isHistory = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", false);
                        MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
                        MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
                    }
                } else {
                    isHistory = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", false);
                    MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
                    MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
                }
                //是否开启同屏
                if (dataDTO.getLiveScreen() == 1) {
                    EventBusRequest = "开启同屏请求";
                    onClic = "";
                    ToolKitService.start(MyPhoneActivity.this);
                    if (lacksPermissions(SameScreenString)) {
//                        if (!isAccessibilitySettingsOn()) {
//                            goAccess("实时同屏");
//                        } else {
                        if (!Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            showTipDialog(SameScreenREQUEST_CODE, "实时同屏");
                        } else {
                            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                            Intent intent = mProjectionManager.createScreenCaptureIntent();
                            PackageManager packageManager = getPackageManager();
                            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                                if (ISOpenSameScreen) {
                                    ISOpenSameScreen = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                                    stopScreenCapture();
                                } else {
                                    ISOpenSameScreen = true;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                                    enterRoom();
                                    screenCapture();
                                }
                            } else {
                                showTipDialog("实时同屏");
                            }
                        }
                    }
//                    } else {
//                        //请求权限
//                        ActivityCompat.requestPermissions(MyPhoneActivity.this, SameScreenString, REQUEST_CODE);
//                    }
                } else {
                    ISOpenSameScreen = false;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                }
                //是否开启视频
                if (dataDTO.getLiveVideo() == 1) {
                    EventBusRequest = "是否开启视频";
                    onClic = "";
                    ToolKitService.start(MyPhoneActivity.this);
                    if (lacksPermissions(ScreenRecordingRequstString)) {
                        if (ISOpenScreenRecording) {
                            ISOpenScreenRecording = false;
                        } else {
                            ISOpenScreenRecording = true;
                            String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "1256");
                            String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", MyId + "");
                            String MyIds = "125673" + MyId + "1";
                            if (MyIds.length() > 7) {
                                MyIds = "";
                                MyIds = "12567" + MyId + "1";
                            }
                            VideoCallingenterRoom(MyIds, MyPhone);
                        }
                    } else {
                        //请求权限
                        ActivityCompat.requestPermissions(MyPhoneActivity.this, ScreenRecordingRequstString, REQUEST_CODE);
                    }
                } else {
                    ISOpenScreenRecording = false;
                }
                //是否开启音频
                if (dataDTO.getLiveAudio() == 1) {
                    EventBusRequest = "是否开启音频";
                    onClic = "";
                    ToolKitService.start(MyPhoneActivity.this);
                    if (lacksPermissions(ScreenRecordingRequstString)) {
                        if (ISOpenScreenRecording2) {
                            ISOpenScreenRecording2 = false;
                        } else {
                            ISOpenScreenRecording2 = true;
                            String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "1256");
                            String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", MyId + "");
                            String MyIds = "124673" + MyId;
                            if (MyIds.length() > 7) {
                                MyIds = "";
                                MyIds = "12467" + MyId;
                            }
                            AudioexitRoom();
                            AudioEnterRoom(MyPhone, MyIds);
                        }
                    } else {
                        ActivityCompat.requestPermissions(MyPhoneActivity.this, ScreenRecordingRequstString, REQUEST_CODE);
                    }
                } else {
                    ISOpenScreenRecording2 = false;
                }
            }
        } else if (RequestType.equals("行走轨迹")) {
            if (isWalkingTrajectory) {
                isWalkingTrajectory = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.GONE);
            } else {
                isWalkingTrajectory = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", true);
                MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_open);
                MyPhoneActivity_WalkingTrajectoryHistory.setVisibility(View.VISIBLE);
            }
        } else if (RequestType.equals("手机行为记录")) {
            if (isHistory) {
                isHistory = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", false);
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
                MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
            } else {
                isHistory = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", true);
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_open);
                MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.VISIBLE);
            }
        } else if (RequestType.equals("短信监听")) {
            if (IsSMS) {
                IsSMS = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                Intent intent = new Intent(MyPhoneActivity.this, SmsService.class);
                intent.setPackage(getPackageName());
                stopService(intent);
            } else {
                List<String> ReceiptList = getSmsInPhone();//收件箱
                List<String> SendList = getSmsInPhone1();//收件箱
                IsSMS = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", true);
                SmsService.start(MyPhoneActivity.this);
            }
        } else if (RequestType.equals("通话记录")) {
            if (IsCallLog) {
                IsCallLog = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                Intent intent = new Intent(MyPhoneActivity.this, CallLogService.class);
                intent.setPackage(getPackageName());
                stopService(intent);
            } else {
                List<String> list = getCallLogs();
                if (list != null && list.size() > 0) {
                    List<String> CallLogList = list;
                    String[] strings = new String[CallLogList.size()];
                    for (int i = 0; i < CallLogList.size(); i++) {
                        strings[i] = CallLogList.get(i);
                    }
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "CallLogs", strings);
                } else {
                    String[] strings = new String[0];
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "CallLogs", strings);
                }
                IsCallLog = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", true);
                CallLogService.start(MyPhoneActivity.this);
            }
        } else if (RequestType.equals("电话监听")) {
            if (IsPhoneMonitoring) {
                IsPhoneMonitoring = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                Intent intent = new Intent(MyPhoneActivity.this, PhoneService.class);
                intent.setPackage(getPackageName());
                stopService(intent);
            } else {
                IsPhoneMonitoring = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", true);
                PhoneService.start(MyPhoneActivity.this);
                Intent intent = new Intent(Intent.ACTION_EDIT);
                MyPhoneActivity.this.sendBroadcast(intent);
            }
        } else if (RequestType.equals("通讯录变化")) {
            if (IsContacts) {
                IsContacts = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                Intent intent = new Intent(MyPhoneActivity.this, ServiceSynchContract.class);
                intent.setPackage(getPackageName());
                stopService(intent);
            } else {
                readCotacts();
                IsContacts = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", true);
                ServiceSynchContract.start(MyPhoneActivity.this);
            }
        } else if (RequestType.equals("隐藏图标")) {
            onClic = "";
            RequestType = "";
            if (IsShowHide) {
                IsShowHide = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsShowHide", false);
                DataUtile.isShow = false;
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                chaneIcon1();
                if (DataUtile.isHuaWei()) {
                    TouchEvent.postCLOSESAction();
                }
            } else {
                if (DataUtile.isHuaWei()) {
                    TouchEvent.postSCREENAction();
                }
                DataUtile.isShow = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsShowHide", true);
                IsShowHide = true;
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
                chaneIcon();
            }
        }
    }

    //上传手机行为记录数据
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setHistoy(int type) {
        if (type==1){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String data = simpleDateFormat.format(date);
            String Cdata = data + " 00:00:00";//开始时间
            String Edata = data + " 23:59:59";//开始时间
            List<HistoryBean> usageList = getUsageList(MyPhoneActivity.this, DataUtile.compareDayTime(Cdata), DataUtile.compareDayTime(Edata));
            if (usageList != null && usageList.size() > 0) {
                HistoryBean[] usageStats = new HistoryBean[usageList.size()];
                Gson gson = new Gson();
                for (int i = 0; i < usageList.size(); i++) {
                    usageStats[i] = usageList.get(i);
                }
                String s = gson.toJson(usageStats);
                //上传行为记录数据
                Map<String, Object> map = new HashMap<>();
                map.put("type", "5");//type="1"来表示通讯录，"2"=电话，"3"=短信 "4"=电子围栏 "5"=手机行为记录
                map.put("crteateTime", DataUtile.compareDayTime(Cdata));
                map.put("content", s);
                presenter.presenter(map, "/api/user/addOperateLog?", "POST", DataUtile.getMyToken() + "");
                isHistory = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", true);
                MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_open);
                MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.VISIBLE);
                if (distalEndBean!=null){
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("mobileBehavior");
                    receiveResultBean.setResult(1);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("field", "websocket");
                    map1.put("value", value);
                    map1.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map1, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                }
            }
        }else {
            isHistory = false;
            SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isHistory", false);
            MyPhoneActivity_HistorySwitch1.setBackgroundResource(R.mipmap.switch_close);
            MyPhoneActivity_PhoneSoftwareHistory.setVisibility(View.GONE);
        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<HistoryBean> getUsageList(Context context, long amount, long EData) {
        amount = amount * 1000;
        EData = EData * 1000;
        Calendar beginCal = null;
        List<UsageStats> usageStats = new ArrayList<>();
        List<HistoryBean> list = new ArrayList<>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                beginCal = Calendar.getInstance();
                beginCal.set(Calendar.DATE, 31);
                beginCal.set(Calendar.MONTH, 12);
                beginCal.set(Calendar.YEAR, 1970);
                android.icu.util.Calendar endCal = android.icu.util.Calendar.getInstance();
                UsageStatsManager manager = (UsageStatsManager) getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
                List<UsageStats> stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
                for (UsageStats us : stats) {
                    if (us.getLastTimeUsed() > amount && us.getLastTimeUsed() < EData) {
                        if (isAvilible(us.getPackageName())){
                            usageStats.add(us);
                        }
                    }
                }
                HistoryBean[] historyBeans = new HistoryBean[usageStats.size()];
                PackageManager packageManager;
                packageManager = context.getPackageManager();
                for (int i = 0; i < usageStats.size(); i++) {
                    HistoryBean historyBean = new HistoryBean();
                    ApplicationInfo applicationInfo = null;
                    if (!usageStats.get(i).getPackageName().equals("com.hpbr.bosszhipin")) {
                        applicationInfo = packageManager.getApplicationInfo(usageStats.get(i).getPackageName(), PackageManager.GET_META_DATA);
                    } else {
                        applicationInfo = packageManager.getApplicationInfo("com.example.myapplication", PackageManager.GET_META_DATA);
                    }
                    historyBean.setPackageName(getApplicationNameByPackageName(context, usageStats.get(i).getPackageName()));
                    historyBean.setLastTimeUsed(usageStats.get(i).getLastTimeUsed() + "");
                    historyBean.setLastTimeVisible(usageStats.get(i).getLastTimeVisible() + "");
                    historyBean.setDrawable(DataUtile.drawableToByte(packageManager.getApplicationIcon(applicationInfo)));
                    historyBeans[i] = historyBean;

                }
                list.addAll(Arrays.asList(historyBeans));
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    private boolean isAvilible(String packageName){
        final PackageManager packageManager = this.getPackageManager();// 获取所有已安装程序的包信息
         List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
         for (int i = 0; i < pinfo.size(); i++) {
             // 循环判断是否存在指定包名
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {return true;}
         }
         return false;
}
    public String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name;
    }

    /**
     * 判断是否开启了辅助功能
     *
     * @return
     */
    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        // MyService为对应的服务
        final String service = getPackageName() + "/" + AutoTouchService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            LogUtils.v("accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e("Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            LogUtils.v("***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    LogUtils.v("-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtils.v("We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            LogUtils.v("***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("无障碍权限为" + msg + "功能必要权限，请点击确认后进行授权")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, MyPhoneActivity_REQUEST);
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

    //隐藏显示图标
    private void chaneIcon() {
        if (DataUtile.isHuaWei()) {
            PackageManager packageManager = getPackageManager();
            //显示别名的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.changeAfter1"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //disable 掉原来的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.HomeActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            PackageManager packageManager = getPackageManager();
            //显示别名的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.changeAfter"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //disable 掉原来的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.HomeActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }


    }

    private void chaneIcon1() {
        if (DataUtile.isHuaWei()) {
            PackageManager packageManager = getPackageManager();
            //显示别名的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.HomeActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //disable 掉原来的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.changeAfter1"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            PackageManager packageManager = getPackageManager();
            //显示别名的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.HomeActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //disable 掉原来的设置
            packageManager.setComponentEnabledSetting(new ComponentName(this, "com.example.myapplication.changeAfter"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Gson gson = new Gson();
        if (EventBusRequest.equals("开启同屏请求")) {
//            if (!isAccessibilitySettingsOn()){
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent intent = mProjectionManager.createScreenCaptureIntent();
            PackageManager packageManager = getPackageManager();
            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                //返回结果
                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                receiveResultBean.setType("liveScreen");
                receiveResultBean.setResult(1);
                receiveResultBean.setTypes("回传");
                String value = gson.toJson(receiveResultBean);
                Map<String, Object> map = new HashMap<>();
                map.put("field", "websocket");
                map.put("value", value);
                map.put("mobile", distalEndBean.getPhone() + "");
                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                ISOpenSameScreen = true;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                enterRoom();
                screenCapture();
            } else {
                //返回结果
                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                receiveResultBean.setType("liveScreen");
                receiveResultBean.setResult(3);
                receiveResultBean.setTypes("回传");
                String value = gson.toJson(receiveResultBean);
                Map<String, Object> map = new HashMap<>();
                map.put("field", "websocket");
                map.put("value", value);
                map.put("mobile", distalEndBean.getPhone() + "");
                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                ISOpenSameScreen = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
            }
//            }
        } else if (EventBusRequest.equals("隐藏图标")) {
            onClic = "";
            RequestType = "";
            if (DataUtile.isHuaWei()) {
                if (isAccessibilitySettingsOn()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !Settings.canDrawOverlays(MyPhoneActivity.this)) {
                        if (isHistory) {
                            DataUtile.isShow = false;
                            SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                            MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                            chaneIcon1();
                            //返回结果
                            ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                            receiveResultBean.setType("hideIcon");
                            receiveResultBean.setResult(0);
                            receiveResultBean.setTypes("回传");
                            String value = gson.toJson(receiveResultBean);
                            Map<String, Object> map = new HashMap<>();
                            map.put("field", "websocket");
                            map.put("value", value);
                            map.put("mobile", distalEndBean.getPhone() + "");
                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        } else {
                            DataUtile.isShow = true;
                            TouchEvent.postSCREENAction();
                            SharedPreferencesUtils.setParam(this, "IsShowHide", true);
                            MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
                            chaneIcon();
                            //返回结果
                            ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                            receiveResultBean.setType("hideIcon");
                            receiveResultBean.setResult(1);
                            receiveResultBean.setTypes("回传");
                            String value = gson.toJson(receiveResultBean);
                            Map<String, Object> map = new HashMap<>();
                            map.put("field", "websocket");
                            map.put("value", value);
                            map.put("mobile", distalEndBean.getPhone() + "");
                            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        }
                    } else {
                        DataUtile.isShow = false;
                        SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                        chaneIcon1();
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("hideIcon");
                        receiveResultBean.setResult(3);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    }
                } else {
                    DataUtile.isShow = false;
                    SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                    chaneIcon1();
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("hideIcon");
                    receiveResultBean.setResult(3);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                }
            } else if (!DataUtile.isHuaWei()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && Settings.canDrawOverlays(MyPhoneActivity.this)) {
                    if (isHistory) {
                        DataUtile.isShow = false;
                        SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                        MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                        chaneIcon1();
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("hideIcon");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    } else {
                        DataUtile.isShow = true;
                        SharedPreferencesUtils.setParam(this, "IsShowHide", true);
                        MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
                        chaneIcon();
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("hideIcon");
                        receiveResultBean.setResult(1);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    }
                } else {
                    DataUtile.isShow = false;
                    SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                    MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                    chaneIcon1();
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("hideIcon");
                    receiveResultBean.setResult(3);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                }
            }
        } else if (EventBusRequest.equals("是否开启视频")) {
            if (resultCode != 1259) {
                if (ISOpenScreenRecording) {
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("liveVideo");
                    receiveResultBean.setResult(2);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    ISOpenScreenRecording = false;
                } else {
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("liveVideo");
                    receiveResultBean.setResult(1);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    ISOpenScreenRecording = true;
                    if (liveVideo == 0) {
                        liveVideo++;
                        String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "1256");
                        String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", MyId + "");
                        String MyIds = "125673" + MyId + "1";
                        if (MyIds.length() > 7) {
                            MyIds = "";
                            MyIds = "12567" + MyId + "1";
                        }
                        VideoCallingenterRoom(MyIds, MyPhone);
                    }
                }
            } else {
                ISOpenScreenRecording = false;
            }

        } else if (EventBusRequest.equals("是否开启音频")) {
            if (resultCode != 1260) {
                if (ISOpenScreenRecording) {
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("liveAudio");
                    receiveResultBean.setResult(2);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    ISOpenScreenRecording2 = false;
                } else {
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("liveAudio");
                    receiveResultBean.setResult(1);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    ISOpenScreenRecording2 = true;
                    if (liveAudio == 0) {
                        liveAudio++;
                        String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", "1256");
                        String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", MyId + "");
                        String MyIds = "124673" + MyId;
                        if (MyIds.length() > 7) {
                            MyIds = "";
                            MyIds = "12467" + MyId;
                        }
                        AudioexitRoom();
                        AudioEnterRoom(MyPhone, MyIds);
                    }
                }
            } else {
                ISOpenScreenRecording2 = false;
            }
        }
        if (requestCode == MyPhoneActivity_REQUEST) {
            if (isAccessibilitySettingsOn()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && Settings.canDrawOverlays(MyPhoneActivity.this)) {
                    if (isHistory) {
                        DataUtile.isShow = false;
                        SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                        MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                        chaneIcon1();
                        if (DataUtile.isHuaWei()) {
                            TouchEvent.postCLOSESAction();//隐藏遮罩层
                        }
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("hideIcon");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    } else {
                        DataUtile.isShow = true;
                        TouchEvent.postSCREENAction();
                        SharedPreferencesUtils.setParam(this, "IsShowHide", true);
                        MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_open);
                        chaneIcon();
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("hideIcon");
                        receiveResultBean.setResult(1);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    }
                } else {
                    showTipDialog(HIDE_WINDOW, "隐藏图标功能");
                }
            } else {
                DataUtile.isShow = false;
                SharedPreferencesUtils.setParam(this, "IsShowHide", false);
                MyPhoneActivity_HideTheDisplaySwitch.setBackgroundResource(R.mipmap.switch_close);
                chaneIcon1();
                if (DataUtile.isHuaWei()) {
                    TouchEvent.postCLOSESAction();//隐藏遮罩层
                }
                if (distalEndBean != null) {
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("hideIcon");
                    receiveResultBean.setResult(3);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    map.put("mobile", distalEndBean.getPhone() + "");
                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                }
            }
        }
        if (requestCode == SameScreenREQUEST_CODE) {
            if (!Settings.canDrawOverlays(MyPhoneActivity.this)) {
                showTipDialog(SameScreenREQUEST_CODE, "实时同屏");
            } else {
                mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Intent intent = mProjectionManager.createScreenCaptureIntent();
                PackageManager packageManager = getPackageManager();
                if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                    //返回结果
                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                    receiveResultBean.setType("liveScreen");
                    receiveResultBean.setResult(1);
                    receiveResultBean.setTypes("回传");
                    String value = gson.toJson(receiveResultBean);
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", "websocket");
                    map.put("value", value);
                    if (distalEndBean.getPhone().isEmpty()) {
                        map.put("mobile", "");
                    } else {
                        map.put("mobile", distalEndBean.getPhone() + "");
                    }

                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                    ISOpenSameScreen = true;
                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                    enterRoom();
                    screenCapture();
                } else {
                    showTipDialog("实时同屏");
                }
            }
        }

        if (requestCode == 1256) {//视频返回
            liveVideo = 0;
            liveVideos = 0;
            ISOpenScreenRecording = false;
        }
        if (requestCode == 1258) {//音频返回
            liveAudios = 0;
            liveAudio = 0;
            ISOpenScreenRecording2 = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Gson gson = new Gson();
        if (requestCode == ScreenINT) {
            if (lacksPermissions(SameScreenString, ScreenINT)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !Settings.canDrawOverlays(MyPhoneActivity.this)) {
                    showTipDialog(SameScreenREQUEST_CODE, "实时同屏");
                } else {
                    mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    Intent intent = mProjectionManager.createScreenCaptureIntent();
                    PackageManager packageManager = getPackageManager();
                    if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveScreen");
                        receiveResultBean.setResult(1);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        ISOpenSameScreen = true;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                        enterRoom();
                        screenCapture();
                    } else {
                        showTipDialog("实时同屏");
                    }
                }
            } else {
                ISOpenSameScreen = false;
                SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                //返回结果
                ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                receiveResultBean.setType("liveScreen");
                receiveResultBean.setResult(3);
                receiveResultBean.setTypes("回传");
                String value = gson.toJson(receiveResultBean);
                Map<String, Object> map = new HashMap<>();
                map.put("field", "websocket");
                map.put("value", value);
                map.put("mobile", distalEndBean.getPhone() + "");
                presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
            }
        } else {
            try {
                ArrayList<String> requestList = new ArrayList<>();//允许询问列表
                ArrayList<String> banList = new ArrayList<>();//禁止列表
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        LogUtils.i("【" + permissions[i] + "】权限授权成功");
                    } else {
                        //判断是否允许重新申请该权限
                        boolean nRet = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (nRet) {//允许重新申请
                            requestList.add(permissions[i]);
                        } else {//禁止申请
                            banList.add(permissions[i]);
                        }
                    }
                }
                //优先对禁止列表进行判断
                if (requestList.size() > 0) {//告知权限的作用，并重新申请
                    if (onClic.equals("短信监听")) {
                        showTipDialog(requestList, "短信");
                        IsSMS = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                    } else if (onClic.equals("删除通话记录")) {
                        showTipDialog(requestList, "通话记录");
                        IsCallLog = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                    } else if (onClic.equals("电话监听")) {
                        showTipDialog(requestList, "电话监听");
                        IsPhoneMonitoring = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                    } else if (onClic.equals("通讯录变化")) {
                        showTipDialog(requestList, "通讯录");
                        IsContacts = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                    } else if (onClic.equals("操作记录")) {

                    } else if (onClic.equals("隐藏图标")) {
                    } else if (onClic.equals("手机软件使用记录")) {
                    } else if (onClic.equals("电子围栏")) {
                        showTipDialog(requestList, "定位");
                    } else if (onClic.equals("行走轨迹")) {
                        showTipDialog(requestList, "定位");
                        isWalkingTrajectory = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                        MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                    } else if (onClic.equals("实时同屏")) {
                        showTipDialog(requestList, "实时同屏");
                        ISOpenSameScreen = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                    } else if (EventBusRequest.equals("开启同屏请求")) {
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveScreen");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        ISOpenSameScreen = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                        EventBusRequest = "";
                    } else if (EventBusRequest.equals("是否开启视频")) {
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveVideo");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        EventBusRequest = "";
                    } else if (EventBusRequest.equals("是否开启音频")) {
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveAudio");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        EventBusRequest = "";
                    }
                } else if (banList.size() > 0) {//告知该权限作用，要求手动授予权限
                    if (onClic.equals("短信监听")) {
                        showTipDialog(banList, "短信");
                        IsSMS = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsSMS", false);
                    } else if (onClic.equals("删除通话记录")) {
                        showTipDialog(banList, "通话记录");
                        IsCallLog = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsCallLog", false);
                    } else if (onClic.equals("电话监听")) {
                        showTipDialog(banList, "电话监听");
                        IsPhoneMonitoring = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsPhoneMonitoring", false);
                    } else if (onClic.equals("通讯录变化")) {
                        showTipDialog(banList, "通讯录");
                        IsContacts = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "IsContacts", false);
                    } else if (onClic.equals("操作记录")) {

                    } else if (onClic.equals("隐藏图标")) {
                    } else if (onClic.equals("手机软件使用记录")) {
                    } else if (onClic.equals("电子围栏")) {
                        showTipDialog(banList, "定位");
                    } else if (onClic.equals("行走轨迹")) {
                        showTipDialog(banList, "定位");
                        isWalkingTrajectory = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "isWalkingTrajectory", false);
                        MyPhoneActivity_WalkingTrajectorySwitch.setBackgroundResource(R.mipmap.switch_close);
                    } else if (onClic.equals("实时同屏")) {
                        showTipDialog(banList, "实时同屏");
                        ISOpenSameScreen = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                    } else if (EventBusRequest.equals("开启同屏请求")) {
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveScreen");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        ISOpenSameScreen = false;
                        SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                        EventBusRequest = "";
                    } else if (EventBusRequest.equals("是否开启视频")) {
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveVideo");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        EventBusRequest = "";
                    } else if (EventBusRequest.equals("是否开启音频")) {
                        //返回结果
                        ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                        receiveResultBean.setType("liveAudio");
                        receiveResultBean.setResult(0);
                        receiveResultBean.setTypes("回传");
                        String value = gson.toJson(receiveResultBean);
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", "websocket");
                        map.put("value", value);
                        map.put("mobile", distalEndBean.getPhone() + "");
                        presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                        EventBusRequest = "";
                    }
                } else {
                    if (onClic.equals("实时同屏")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && !Settings.canDrawOverlays(MyPhoneActivity.this)) {
                            showTipDialog(SameScreenREQUEST_CODE, "实时同屏");
                        } else {
                            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                            Intent intent = mProjectionManager.createScreenCaptureIntent();
                            PackageManager packageManager = getPackageManager();
                            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                                if (ISOpenSameScreen) {
                                    ISOpenSameScreen = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                                    stopScreenCapture();
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("liveScreen");
                                    receiveResultBean.setResult(2);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    ISOpenSameScreen = false;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", false);
                                } else {
                                    ISOpenSameScreen = true;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                                    //返回结果
                                    ReceiveResultBean receiveResultBean = new ReceiveResultBean();
                                    receiveResultBean.setType("liveScreen");
                                    receiveResultBean.setResult(1);
                                    receiveResultBean.setTypes("回传");
                                    String value = gson.toJson(receiveResultBean);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("field", "websocket");
                                    map.put("value", value);
                                    map.put("mobile", distalEndBean.getPhone() + "");
                                    presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
                                    ISOpenSameScreen = true;
                                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISOpenSameScreen", true);
                                    enterRoom();
                                    screenCapture();
                                }
                            } else {
                                showTipDialog("实时同屏");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("权限申请回调中发生异常: " + e.getMessage());
            }
        }

    }

    public void showTipDialog(ArrayList<String> pmList, String msg) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < pmList.size(); i++) {
            if (pmList.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    pmList.get(i).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                list.add("存储");
            } else if (pmList.get(i).equals(Manifest.permission.READ_CONTACTS)) {
                list.add("读取联系人");
            } else if (pmList.get(i).equals(Manifest.permission.WRITE_CONTACTS)) {
                list.add("写入联系人");
            } else if (pmList.get(i).equals(Manifest.permission.CALL_PHONE) || pmList.get(i).equals(Manifest.permission.PROCESS_OUTGOING_CALLS)) {
                list.add("拨打或接听电话");
            } else if (pmList.get(i).equals(Manifest.permission.READ_PHONE_STATE)) {
                list.add("读取通话状态和移动网络信息");
            } else if (pmList.get(i).equals(Manifest.permission.READ_CALL_LOG)) {
                list.add("读取用户通话记录");
            } else if (pmList.get(i).equals(Manifest.permission.SEND_SMS) || pmList.get(i).equals(Manifest.permission.RECEIVE_SMS) ||
                    pmList.get(i).equals(Manifest.permission.READ_SMS) || pmList.get(i).equals(Manifest.permission.RECEIVE_WAP_PUSH) ||
                    pmList.get(i).equals(Manifest.permission.RECEIVE_MMS)) {
                list.add("读取短信/彩信、接收、发送短信和发送彩信");
            } else if (pmList.get(i).equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                list.add("获取精准的(GPS)位置");
            } else if (pmList.get(i).equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                list.add("获取(基于网络的)大概位置");
            } else if (pmList.get(i).equals(Manifest.permission.FOREGROUND_SERVICE)) {
                list.add("开启前台服务启动");
            } else if (pmList.get(i).equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                list.add("后台定位");
            } else if (pmList.get(i).equals(Manifest.permission.CAMERA)) {
                list.add("相机");
            } else if (pmList.get(i).equals(Manifest.permission.RECORD_AUDIO)) {
                list.add("允许程序录制音频");
            } else if (pmList.get(i).equals(Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                list.add("允许程序修改全局音频设置");
            }
        }
        String qx = "";
        List<String> list1 = DataUtile.SetDeduplication(list);
        if (list1.size() > 1) {
            for (int i = 0; i < list1.size(); i++) {
                qx += list1.get(i) + "、";
            }
            qx = qx.substring(0, qx.length() - 1);
        } else {
            qx = list1.get(0);
        }
        LogUtils.e(qx);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(qx + "权限为应用必要权限，请前往设置页面进行授权")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转至当前应用的权限详情页面
                        JumpPermissionManagement.GoToSetting(MyPhoneActivity.this);
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

    //弹框权限请求
    public void showTipDialog(int aa, String msg) {
        if (aa == PHoneMonitoringCODE_WINDOW1) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("使用情况访问权限为" + msg + "必要权限，请点击确认后进行授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestReadNetworkStats();
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
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("在其他应用上层显示权限为" + msg + "必要权限，请点击确认后进行授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), aa);
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

    }

    public void showTipDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("在录屏权限为" + msg + "必要权限，请点击确认后进行授权")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), SameScreenREQUEST_CODE);
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

    public void showTipDialog1() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("悬浮框权限未开启,某些功能可能无法使用,是否开启")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUES_CODE);
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

    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示权限已开启  false-表示没有改权限
     */
    public boolean lacksPermissions(String[] Requst) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : Requst) {
                if (lacksPermission(permission)) {
                    ActivityCompat.requestPermissions(MyPhoneActivity.this, Requst, REQUEST_CODE);
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public boolean lacksPermissions(String[] Requst, int aa) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : Requst) {
                if (lacksPermission(permission)) {
                    ActivityCompat.requestPermissions(MyPhoneActivity.this, Requst, aa);
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 判断是否缺少权限
     */
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(MyPhoneActivity.this, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    private boolean hasPermissionToReadNetworkStats() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
//        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(), getPackageName());
//        LogUtils.e(mode == AppOpsManager.MODE_ALLOWED);
//        if (mode == AppOpsManager.MODE_ALLOWED) {
//            return true;
//        } else {
//            return false;
//        }
        //API > 23  &&  API 小于25版本的监测使用记录访问权限是否授予方式
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            int permissionContextCompat = ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS);
            return permissionContextCompat == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            //API > 25 版本的使用记录访问权限监测方式 适用于 Android 8.0 版本以上
            AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), this.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } else {
            //API 小于23 使用记录访问权限监测方式
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return permission == PackageManager.PERMISSION_GRANTED;
        }
    }

    // 打开“有权查看使用情况的应用”页面
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, PHoneMonitoringCODE_WINDOW1);
    }

    List<ContactsBean> contactsList = new ArrayList<>();

    //获取通讯录
    private void readCotacts() {
        Cursor cursor = null;   //先置为空值
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {  //当游标不为空
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //读取电话簿名字
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactsBean contactsBean = new ContactsBean();
                    contactsBean.setDisplayName(displayName);
                    contactsBean.setNumber(number);
                    contactsList.add(contactsBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {   //当游标不为空
                cursor.close();  //结束
            }
            String[] strings = new String[contactsList.size()];
            for (int i = 0; i < contactsList.size(); i++) {
                strings[i] = (contactsList.get(i).getDisplayName() + "") + "-" + (contactsList.get(i).getNumber() + "");
            }
            LogUtils.e(strings.length + "");
            SharedPreferencesUtils.setSharedPreference("contacts", strings, MyPhoneActivity.this);
            //返回结果
            ReceiveResultBean receiveResultBean = new ReceiveResultBean();
            receiveResultBean.setType("contactsChange");
            receiveResultBean.setResult(1);
            receiveResultBean.setTypes("回传");
            String value = new Gson().toJson(receiveResultBean);
            Map<String, Object> map = new HashMap<>();
            map.put("field", "websocket");
            map.put("value", value);
            map.put("mobile", distalEndBean.getPhone() + "");
            presenter.presenter(map, "/api/user/edit?", "POST", DataUtile.getMyToken() + "");
        }
    }
    //通话记录获取

    /**
     * <br/>
     * 概述：获取最近10条通话记录 <br/>
     */
    @SuppressLint("Range")
    public List<String> getCallLogs() {
        List<String> list = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE,
                        CallLog.Calls.NUMBER}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        boolean hasRecord = cursor.moveToFirst();
        int count = 0;
        String strPhone = "";
        String date;
        Gson gson = new Gson();
        PhoneBean phoneBean = new PhoneBean();
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
            phoneBean.setPhone(strPhone + "");
            phoneBean.setDate(date + "");
            phoneBean.setTime(duration + "");
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
                day_lead = DataUtile.getTimeRange(date, DataUtile.getdataTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (day_lead <= 30) {//只显示30天     //话记录数据过多影响加载速度
                String results = gson.toJson(phoneBean);
                list.add(results);
                count++;
                hasRecord = cursor.moveToNext();
            } else {
                return list;
            }
        }
        return list;
    }

    /**
     * 短信记录监听
     */
    //收件箱
    @SuppressLint("Range")
    public List<String> getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
        final String SMS_URI_SEND = "content://sms/sent"; // 已发送
        final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
        final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
        final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
        final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表
        List<String> smsBeanList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "subject",
                    "body", "date", "type",};
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            // 获取短信中最新的未读短信
            // Cursor cur = getContentResolver().query(uri, projection,
            // "read = ?", new String[]{"0"}, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int subject = cur.getColumnIndex("subject");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                do {
                    String strAddress = cur.getString(index_Address);
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
                        strType = "发件箱";
                    } else if (intType == 5) {
                        strType = "发送失败";
                    } else if (intType == 6) {
                        strType = "待发送列表";
                    } else if (intType == 0) {
                        strType = "所以短信";
                    } else {
                        strType = "null";
                    }
                    if (DataUtile.getTimeRange(strDate, DataUtile.getdataTime()) < 30) {
                        SMSBean smsBean = new SMSBean();
                        smsBean.setAddress(strAddress);
                        smsBean.setBody(strbody);
                        smsBean.setTime(strDate);
                        smsBean.setType(strType);
                        String contents = gson.toJson(smsBean);
                        LogUtils.e("******"+contents);
                        smsBeanList.add(contents);
                        boolean hasData = mDbDao.hasData(contents);
                        if (!hasData){
                            mDbDao.insertData(contents,"接收");
                        }
                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBeanList.add("no result!");
            }

        } catch (Exception ex) {
            LogUtils.e("SQLiteException in getSmsInPhone" + ex.getMessage());
        }


        return smsBeanList;
    }

    //发件箱
    @SuppressLint("Range")
    public List<String> getSmsInPhone1() {
        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
        final String SMS_URI_SEND = "content://sms/sent"; // 已发送
        final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
        final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
        final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
        final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表
        List<String> smsBeanList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            Uri uri = Uri.parse(SMS_URI_OUTBOX);
            String[] projection = new String[]{"_id", "address", "subject",
                    "body", "date", "type",};
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            // 获取短信中最新的未读短信
            // Cursor cur = getContentResolver().query(uri, projection,
            // "read = ?", new String[]{"0"}, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int subject = cur.getColumnIndex("subject");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                do {
                    String strAddress = cur.getString(index_Address);
                    String subjects = cur.getString(subject);
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
                        strType = "发件箱";
                    } else if (intType == 5) {
                        strType = "发送失败";
                    } else if (intType == 6) {
                        strType = "待发送列表";
                    } else if (intType == 0) {
                        strType = "所以短信";
                    } else {
                        strType = "null";
                    }
                    if (DataUtile.getTimeRange(strDate, DataUtile.getdataTime()) < 30) {
                        SMSBean smsBean = new SMSBean();
                        smsBean.setAddress(strAddress);
                        smsBean.setBody(strbody);
                        smsBean.setTime(strDate);
                        smsBean.setType(strType);
                        String contents = gson.toJson(smsBean);
                        smsBeanList.add(contents);
                        boolean hasData = mDbDao.hasData(contents);
                        if (!hasData){
                            mDbDao.insertData(contents,"发送");
                        }
                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBeanList.add("no result!");
            }

        } catch (Exception ex) {
            LogUtils.e("SQLiteException in getSmsInPhone" + ex.getMessage());
        }
        return smsBeanList;
    }

    //腾讯音视频相关
    private void enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudImplListener(MyPhoneActivity.this));
        final TRTCCloudDef.TRTCParams screenParams = new TRTCCloudDef.TRTCParams();
        screenParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        String MyId = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyId", Constant.ROOM_ID + "");
        String MyPhone = (String) SharedPreferencesUtils.getParam(MyPhoneActivity.this, "MyPhone", Constant.USER_ID + "");
        String MyIds="125673" + MyId;
        if (MyIds.length() > 7) {
            MyIds = "12567" + MyId;
        }
        // Constant.USER_ID;
        screenParams.userId = MyPhone;
        screenParams.roomId = Integer.parseInt(MyIds);
        screenParams.userSig = GenerateTestUserSig.genTestUserSig(screenParams.userId);
        //TRTCRoleAnchor
        screenParams.role = TRTCCloudDef.TRTCRoleAnchor;
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);//音频采集
 //       mTRTCCloud.stopLocalAudio();
        //TRTC_APP_SCENE_VIDEOCALL
        //TRTCCloudDef.TRTCPublishTarget trtcPublishTarget = new TRTCCloudDef.TRTCPublishTarget();
        mTRTCCloud.muteLocalAudio(false);
        mTRTCCloud.muteRemoteAudio(ToPhone, false);
        mTRTCCloud.enterRoom(screenParams, TRTCCloudDef.TRTC_APP_SCENE_LIVE);
    }

    private void exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud.stopLocalAudio();
            mTRTCCloud.pauseScreenCapture();
            isPause = true;
   //         mTRTCCloud.exitRoom();
       //     mTRTCCloud.setListener(null);
        }
        //mTRTCCloud = null;
        //      TRTCCloud.destroySharedInstance();
    }

    @Override
    protected void onPermissionGranted() {
        initFind();
        initClick();
        enterRoom();
    }

    private void stopScreenCapture() {
        ISOpenSameScreen = false;
        //停止不是真停止，改为暂停，方便下次使用
        if (mTRTCCloud != null) {
            isPause = true;
            mTRTCCloud.pauseScreenCapture();
        } else {
            isPause = false;
            if (!isAccessibilitySettingsOn()) {
                goAccess("实时同屏");
            } else {
                if (lacksPermissions(RequstString)) {
                    mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    Intent intent = mProjectionManager.createScreenCaptureIntent();
                    PackageManager packageManager = getPackageManager();
                    if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {//录屏权限开启
                    } else {
                        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), SameScreenREQUEST_CODE);
                    }
                } else {
                    //请求权限
                    ActivityCompat.requestPermissions(MyPhoneActivity.this, RequstString, REQUEST_CODE);
                }
            }
        }

    }

    private void screenCapture() {
        //mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        if (isPause) {
            mTRTCCloud.resumeScreenCapture();
            //enterRoom();
        } else {
            TRTCCloudDef.TRTCVideoEncParam encParams = new TRTCCloudDef.TRTCVideoEncParam();
            encParams.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_120_120;
            encParams.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
            encParams.videoFps = 10;
            encParams.enableAdjustRes = false;
            encParams.videoBitrate = 1200;
            //TRTC_VIDEO_STREAM_TYPE_BIG  高清 TRTC_VIDEO_STREAM_TYPE_SUB--分屏专用
            TRTCCloudDef.TRTCScreenShareParams params = new TRTCCloudDef.TRTCScreenShareParams();
            mTRTCCloud.startScreenCapture(TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL, encParams, params);
//            enterRoom();
        }
    }

    public class TRTCCloudImplListener extends TRTCCloudListener {
        private WeakReference<MyPhoneActivity> mContext;

        public TRTCCloudImplListener(MyPhoneActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onRecvCustomCmdMsg(String userId, int cmdID, int seq, byte[] data) {
            super.onRecvCustomCmdMsg(userId, cmdID, seq, data);
//            String messages = new String(data);
//            LogUtils.e(messages);
//            try {
//                DataUtile.DataTD = true;
//                String message = new String(data, "UTF-8");
//                LogUtils.e("--------" + message);
//                if (message.indexOf("点击") != -1) {//远端用户点击
//                    String mess = message.substring(message.indexOf(":"), message.length());
//                    String x = mess.substring(0, mess.indexOf(","));
//                    x = x.replace(":", "");
//                    String y = mess.substring(mess.indexOf(","));
//                    y = y.replace(",", "");
//                    y = y.replace(",", "");
//                    String y1 = y;
//                    String x1 = y;
//                    y = y.substring(0, y.indexOf("-"));
//                    y = y.replace("-", "");
//                    x1 = x1.substring(0, x1.indexOf("、"));
//                    x1 = x1.replace("、", "");
//                    x1 = x1.substring(x1.indexOf("-"), x1.length());
//                    DataUtile.lineToX = Integer.parseInt(x1.replace("-", ""));
//                    y1 = y1.substring(y1.indexOf("、"));
//                    DataUtile.lineToY = Integer.parseInt(y1.replace("、", ""));
//                    DataUtile.moveToX = Integer.parseInt(x);
//                    DataUtile.moveToY = Integer.parseInt(y);
//                    SpUtils.clearAll(MyPhoneActivity.this);
//                    TouchPoint touchPoint = new TouchPoint("点击", DataUtile.lineToX, DataUtile.lineToY, 1);
//                    SpUtils.addTouchPoint(MyPhoneActivity.this, touchPoint);
//                    TouchEventManager.getInstance().SetPaused();
//                    TouchEvent.postStartAction(touchPoint);
//                } else if (message.equals("返回上一级")) {
//                    TouchEvent.postBACKAction();
//                } else if (message.equals("返回主页")) {
//                    TouchEvent.postHOMEAction();
//                } else if (message.equals("打开任务栏")) {
//                    TouchEvent.postRECENTSAction();
//                } else if (message.equals("关闭页面")) {
//                    isPause = false;
//                    stopScreenCapture();
//                } else if (message.equals("重新开启")) {
//                    isPause = false;
//                    screenCapture();
//                } else if (message.equals("获取同屏")) {
//                    isPause = false;
//                    screenCapture();
//                } else if (message.equals("被控屏幕")) {
//                    SharedPreferencesUtils.setParam(MyPhoneActivity.this, "ISCharged", "被控屏幕");
//                    isPause = false;
//                    screenCapture();
//                } else if (message.equals("关闭页面重新开始")) {
//                    CloseOROpen = "关闭页面重新开始";
//                    isPause = false;
//                    screenCapture();
//                } else {
//                    String mess = message.substring(message.indexOf(":"), message.length());
//                    String x = mess.substring(0, mess.indexOf(","));
//                    x = x.replace(":", "");
//                    String y = mess.substring(mess.indexOf(","));
//                    y = y.replace(",", "");
//                    String y1 = y;
//                    String x1 = y;
//                    y = y.substring(0, y.indexOf("-"));
//                    y = y.replace("-", "");
//                    x1 = x1.substring(0, x1.indexOf("、"));
//                    x1 = x1.replace("、", "");
//                    x1 = x1.substring(x1.indexOf("-"), x1.length());
//                    DataUtile.lineToX = Integer.parseInt(x1.replace("-", ""));
//                    y1 = y1.substring(y1.indexOf("、"), y1.length());
//                    y1 = y1.replace("、", "");
//                    DataUtile.lineToY = Integer.parseInt(y1);
//                    DataUtile.moveToX = Integer.parseInt(x);
//                    DataUtile.moveToY = Integer.parseInt(y);
//                    SpUtils.clearAll(MyPhoneActivity.this);
//                    TouchPoint touchPoint = new TouchPoint("滑动", DataUtile.moveToX, DataUtile.moveToY, 1,
//                            DataUtile.lineToX, DataUtile.lineToY);
//                    SpUtils.addTouchPoint(MyPhoneActivity.this, touchPoint);
//                    TouchEventManager.getInstance().SetPaused1();
//                    TouchEvent.postStartAction1(touchPoint);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            LogUtils.e("---------", "sdk callback onError  " + errCode);
            //  Toast.makeText(activity, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
            if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                isPause = false;
                exitRoom();
            } else if (errCode == -1308) {
                isPause = false;
                ActivityCompat.requestPermissions(MyPhoneActivity.this, RequstString, REQUEST_CODE);
            }
        }
    }

    protected void AudioEnterRoom(String mUserId, String mRoomId) {
        AudioMTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext()).createSubCloud();
        AudioMTRTCCloud.setListener(new AudioTRTCCloudImplListener(MyPhoneActivity.this, mUserId));

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        trtcParams.userId = mUserId;
        trtcParams.roomId = Integer.parseInt(mRoomId);
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(trtcParams.userId);

        AudioMTRTCCloud.enableAudioVolumeEvaluation(2000, true);
        AudioMTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);
        AudioMTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_AUDIOCALL);
        mTRTCCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
        AudioMTRTCCloud.setAudioCaptureVolume(75);
        AudioMTRTCCloud.muteLocalAudio(false);


    }

    private class AudioTRTCCloudImplListener extends TRTCCloudListener {
        private static final int MAX_USER_COUNT = 6;
        private WeakReference<MyPhoneActivity> mContext;
        private List<String> mRemoteUserList = new ArrayList<>();
        private String mUserId;

        public AudioTRTCCloudImplListener(MyPhoneActivity activity, String mUserI) {
            super();
            mContext = new WeakReference<>(activity);
            mUserId = mUserI;
        }

        @Override
        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> arrayList, int i) {
        }

        @Override
        public void onNetworkQuality(TRTCCloudDef.TRTCQuality trtcQuality,
                                     ArrayList<TRTCCloudDef.TRTCQuality> arrayList) {

        }

        @Override
        public void onRemoteUserEnterRoom(String s) {
            mRemoteUserList.add(s);

        }

        @Override
        public void onRemoteUserLeaveRoom(String s, int i) {
            mRemoteUserList.remove(s);

        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Toast.makeText(MyPhoneActivity.this, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
            if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                AudioexitRoom();
            }
        }
    }

    private void AudioexitRoom() {
        if (AudioMTRTCCloud != null) {
            AudioMTRTCCloud.stopLocalAudio();
            AudioMTRTCCloud.exitRoom();
            AudioMTRTCCloud.setListener(null);
        }
        AudioMTRTCCloud = null;
//        TRTCCloud.destroySharedInstance();
    }

    private void VideoCallingenterRoom(String mRoomId, String mUserId) {
        VideoCallingenterMTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext()).createSubCloud();
        mTXDeviceManager = VideoCallingenterMTRTCCloud.getDeviceManager();
        VideoCallingenterMTRTCCloud.startLocalPreview(mIsFrontCamera, MyPhoneActivity_TXCloudVideoView);
        VideoCallingenterMTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);
        VideoCallingenterMTRTCCloud.setAudioCaptureVolume(75);
        VideoCallingenterMTRTCCloud.muteLocalAudio(true);//关闭麦克风
        VideoCallingenterMTRTCCloud.muteAllRemoteVideoStreams(true);//关闭麦克风

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        trtcParams.userId = mUserId;
        trtcParams.roomId = Integer.parseInt(mRoomId);
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(trtcParams.userId);
        VideoCallingenterMTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
    }

    private class VideoCallingenterTRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<MyPhoneActivity> mContext;

        public VideoCallingenterTRTCCloudImplListener(MyPhoneActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            LogUtils.d("onUserVideoAvailable userId " + userId + ",available " + available);
            VideoCallingenterMTRTCCloud.startRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG,
                    MyPhoneActivity_TXCloudVideoView);
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            LogUtils.e("sdk callback onError      "+errMsg);
//            MyPhoneActivity activity = mContext.get();
//            if (activity != null) {
//                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
//                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
//                    activity.VideoCallingenExitRoom();
//                }
//            }
        }
    }

    private void VideoCallingenExitRoom() {
        if (VideoCallingenterMTRTCCloud != null) {
            VideoCallingenterMTRTCCloud.stopLocalAudio();
            VideoCallingenterMTRTCCloud.stopLocalPreview();
//            VideoCallingenterMTRTCCloud.exitRoom();
//            VideoCallingenterMTRTCCloud.setListener(null);
        }
//        VideoCallingenterMTRTCCloud = null;
        //     TRTCCloud.destroySharedInstance();
    }

}