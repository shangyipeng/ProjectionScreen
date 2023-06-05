package com.example.myapplication;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.activity.BaseActivity;
import com.example.myapplication.activity.LogeActivity;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.activity.MyPhone.RecordofoperationsActivity;
import com.example.myapplication.activity.MyPhoneActivity;
import com.example.myapplication.adapter.PopupwindowAdapter;
import com.example.myapplication.bean.ReceiveResultBean;
import com.example.myapplication.service.AutoTouchService;
import com.example.myapplication.service.MusicService;
import com.example.myapplication.service.WalkingTrajectoryService;
import com.example.myapplication.service.WebSocketService;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.JumpPermissionManagement;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.example.myapplication.utile.WindowUtils;
import com.google.gson.Gson;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.tencent.trtc.debug.GenerateTestUserSig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
public class HomeActivity extends BaseActivity {
    private RelativeLayout HomeActivity_isLoginRelativeLayout, HomeActivity_RelativeLayout;
    private LinearLayout HomeActivity_PlusLinearLayout;
    private TextView HomeActivity_isLoginTextView;
    private String token = "";
    private List<String> plusList = new ArrayList<>();
    private RelativeLayout HomeActivityTitle_RelativeLayout;
    private int REQUES_CODE=1;
    private String[] RequstString = new String[]{
            Manifest.permission.CALL_PHONE,//电话权限申请
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS, //联系人权限
            Manifest.permission.WRITE_CONTACTS,//联系人权限
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.RECORD_AUDIO, //允许程序录制音频
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.CAMERA,//相机权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.MODIFY_AUDIO_SETTINGS,//允许程序修改全局音频设置
    };
    private String[] RequstString1 = new String[]{
            Manifest.permission.CALL_PHONE,//电话权限申请
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS, //联系人权限
            Manifest.permission.WRITE_CONTACTS,//联系人权限
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECORD_AUDIO, //允许程序录制音频
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.CAMERA,//相机权限
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.MODIFY_AUDIO_SETTINGS,//允许程序修改全局音频设置
    };
    //定位
   private String[] ACCESSRequstString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.ACCESS_COARSE_LOCATION,//这个权限用于进行网络定位
            Manifest.permission.ACCESS_FINE_LOCATION,    //这个权限用于访问GPS定位
            Manifest.permission.ACCESS_BACKGROUND_LOCATION

    };
    private final int REQUEST_CODE = 1;
    private final int ACCESSREQUEST_CODE=11;
    private final int ACCESSREQUEST_CODEs=21;
    private static final int PHoneMonitoringCODE_WINDOW1 = 31;
    private static final int BatteryCode=41;
    private static List<String>  perMissions=new ArrayList<>();
    @Override
    public int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        if (!lacksPermissions(RequstString1)){
            ActivityCompat.requestPermissions(HomeActivity.this, RequstString1, REQUEST_CODE);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(HomeActivity.this)){
                if (dialogs==null){
                    showTipDialog1(ACCESSREQUEST_CODEs, "定位");
                }
            }else {
                MusicService.start(this);
                WalkingTrajectoryService.start(this);
                WebSocketService.start(HomeActivity.this);
                WebSocketService.start(this);
                if (!hasPermissionToReadNetworkStats()){
                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                }
            }
        }
        token = (String) SharedPreferencesUtils.getParam(HomeActivity.this, "MyToken", "");
        DataUtile.screenWidth = WindowUtils.getScreenWidth(HomeActivity.this);
        DataUtile.screenHeight = WindowUtils.getScreenHeight(HomeActivity.this);
        if (DataUtile.isHuaWei()) {
            if (getIntent().getSourceBounds() != null) {
                if (getIntent().getSourceBounds() != null) {
                    String SourceBounds = getIntent().getSourceBounds().toString();
                    LogUtils.e(SourceBounds);
                    SourceBounds = SourceBounds.replace("Rect", "");
                    SourceBounds = SourceBounds.replace("(", "");
                    SourceBounds = SourceBounds.replace(")", "");
                    String SourceBounds1 = SourceBounds.substring(0, SourceBounds.indexOf("-"));
                    SourceBounds1 = SourceBounds1.replace(" ", "");
                    int x = Integer.parseInt(SourceBounds1.substring(0, SourceBounds1.indexOf(",")));
                    LogUtils.e(x);
                    if (x != 0) {
                        SharedPreferencesUtils.setParam(HomeActivity.this, "ScreenX", Integer.parseInt(SourceBounds1.substring(0, SourceBounds1.indexOf(","))));
                        SharedPreferencesUtils.setParam(HomeActivity.this, "ScreenY", Integer.parseInt((SourceBounds1.substring(SourceBounds1.indexOf(",")).
                                replace(",", ""))));
                    }
                    String SourceBounds2 = SourceBounds.substring(SourceBounds.indexOf("-"));
                    SourceBounds2 = SourceBounds2.replace("-", "");
                    SourceBounds2 = SourceBounds2.replace(" ", "");//去除空字符串
                    int x1 = Integer.parseInt(SourceBounds2.substring(0, SourceBounds2.indexOf(",")));
                    if (x1 != 0) {
                        SharedPreferencesUtils.setParam(HomeActivity.this, "ScreenX1", Integer.parseInt(SourceBounds2.substring(0, SourceBounds2.indexOf(","))));
                        SharedPreferencesUtils.setParam(HomeActivity.this, "ScreenY1", Integer.parseInt((SourceBounds2.substring(SourceBounds2.indexOf(",")).
                                replace(",", ""))));
                    }
                } else {
                    DataUtile.ScreenX1 = 0;
                    DataUtile.ScreenY1 = 0;
                }
            }
        }
        initFind();
        initDate();
        if (!DataUtile.getMyPhone().isEmpty()){
            WebSocketService.initSocketClient(DataUtile.getMyPhone());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!lacksPermissions(RequstString1)){
            ActivityCompat.requestPermissions(HomeActivity.this, RequstString1, REQUEST_CODE);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(HomeActivity.this)){
                if (dialogs==null){
                    showTipDialog1(ACCESSREQUEST_CODEs, "定位");
                }
            }else {
                MusicService.start(this);
                WalkingTrajectoryService.start(this);
                WebSocketService.start(HomeActivity.this);
                if (!hasPermissionToReadNetworkStats()){
                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                }
            }
        }
        if (perMissions.size()>0){
            for (int i = 0; i < perMissions.size(); i++) {
                if (perMissions.equals("其他应用上层显示权限")){
                    showTipDialog1(ACCESSREQUEST_CODEs, "定位");
                }else if (perMissions.equals("使用情况访问权限")){
                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                }else if (perMissions.equals("电池优化权限")){
                    showTipDialog2();
                }else if (perMissions.equals("无障碍权限")){
                    goAccess("应用");
                }else if (perMissions.equals("部分权限未通过")){
                    ActivityCompat.requestPermissions(HomeActivity.this, RequstString1, REQUEST_CODE);
                }
            }
        }
    }
    private  AlertDialog dialog2;
    public void showTipDialog(int aa, String msg) {
        if (dialog2==null){
            dialog2 = new AlertDialog.Builder(this)
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
                            perMissions.add("使用情况访问权限");
                            dialog.dismiss();
                            if (!isIgnoringBatteryOptimizations()){
                                showTipDialog2();
                            }
                        }
                    })
                    .create();
            dialog2.show();
        }
    }

    // 打开“有权查看使用情况的应用”页面
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, PHoneMonitoringCODE_WINDOW1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,BatteryCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }
    public void showTipDialog2() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("电池优化权限为应用必要权限,为避免功能无法使用,请手动打开")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestIgnoreBatteryOptimizations();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        perMissions.add("电池优化权限");
                        if (!isAccessibilitySettingsOn()){
                            goAccess("应用");
                        }
                    }
                })
                .create();
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("requestCode   onActivityResult    "+requestCode);
        if (requestCode==REQUEST_CODE){
            if (!lacksPermissions(RequstString)){
                showTipDialog1();
            }
        }else if (requestCode==ACCESSREQUEST_CODEs){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && Settings.canDrawOverlays(HomeActivity.this)){
                MusicService.start(this);
                WalkingTrajectoryService.start(this);
                WebSocketService.start(HomeActivity.this);
                if (!hasPermissionToReadNetworkStats()){
                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                }
            }
        } else if  (requestCode==PHoneMonitoringCODE_WINDOW1){
            if (!isIgnoringBatteryOptimizations()){
                showTipDialog2();
            }
        }else if (requestCode==BatteryCode){
            if (DataUtile.isHuaWei()){
                if (!isAccessibilitySettingsOn()){
                    goAccess("应用");
                }
            }
        }
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
                .setMessage("无障碍权限为" + msg + "必要权限，请点击确认后进行授权")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        perMissions.add("无障碍权限");
                    }
                })
                .create();
        dialog.show();
    }
    public void showTipDialog1() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("部分权限未通过,某些功能可能无法使用,请手动开启")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 跳转至当前应用的权限详情页面
                        JumpPermissionManagement.GoToSetting(HomeActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (lacksPermissions(ACCESSRequstString)){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                    && !Settings.canDrawOverlays(HomeActivity.this)){
                                if (dialogs==null){
                                    showTipDialog1(ACCESSREQUEST_CODEs, "定位");
                                }
                            }else {
                                MusicService.start(HomeActivity.this);
                                WalkingTrajectoryService.start(HomeActivity.this);
                                WebSocketService.start(HomeActivity.this);
                                if (!hasPermissionToReadNetworkStats()){
                                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                                }
                            }
                        }else {
                            ActivityCompat.requestPermissions(HomeActivity.this, ACCESSRequstString, ACCESSREQUEST_CODE);
                        }
                    }
                })
                .create();
        dialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            try {
                ArrayList<String> requestList = new ArrayList<>();//允许询问列表
                ArrayList<String> banList = new ArrayList<>();//禁止列表
                int permissionSize=0;
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
                    if (permissions.length-1==i){
                        permissionSize=i;
                    }
                }
                //优先对禁止列表进行判断
                if (requestList.size() > 0) {//告知权限的作用，并重新申请
                    showTipDialog(requestList);
                } else if (banList.size() > 0) {//告知该权限作用，要求手动授予权限
                    showTipDialog(banList);
                }else {
                    if (permissionSize!=0){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && !Settings.canDrawOverlays(HomeActivity.this)){
                            if (dialogs==null){
                                showTipDialog1(ACCESSREQUEST_CODEs, "定位");
                            }
                        }else {
                            MusicService.start(this);
                            WalkingTrajectoryService.start(this);
                            WebSocketService.start(HomeActivity.this);
                            WebSocketService.start(this);
                            if (!hasPermissionToReadNetworkStats()){
                                showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("权限申请回调中发生异常: " + e.getMessage());
            }
    }
    //操作
    private void initDate() {
        if (!token.equals("")) {
            String phones = (String) SharedPreferencesUtils.getParam(HomeActivity.this, "phones", "已登录");
            HomeActivity_isLoginTextView.setText(phones + "");
        }
        //本机登录
        HomeActivity_isLoginRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!token.equals("")){
                    Intent intent = new Intent(HomeActivity.this, MyPhoneActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(HomeActivity.this, LogeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //查找他人
        HomeActivity_RelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!token.equals("")){
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(HomeActivity.this,"请登录本机账号",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击出现弹框
        HomeActivity_PlusLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductDialog();
            }
        });
    }

    /**
     * 点击加号产生弹框
     */
    private void showProductDialog() {
        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popupwindow, null);
        // 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
        ListView lsvMore = view.findViewById(R.id.lsvMore);
        PopupwindowAdapter popupwindowAdapter = new PopupwindowAdapter(plusList, HomeActivity.this);
        lsvMore.setAdapter(popupwindowAdapter);
        // 创建PopupWindow对象，指定宽度和高度
        PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setWidth(300);
        // 设置动画
        //  window.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        // 设置可以获取焦点
        window.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // 更新popupwindow的状态
        window.update();
        // 以下拉的方式显示，并且可以设置显示的位置
//  window.showAsDropDown(tvProduct, 0, 20);
        window.showAsDropDown(HomeActivityTitle_RelativeLayout, 0, 0);
        lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e(plusList.get(position));
                if (plusList.get(position).equals("操作记录")) {
                    Intent intent = new Intent(HomeActivity.this, RecordofoperationsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //初始化
    private void initFind() {
        HomeActivityTitle_RelativeLayout = findViewById(R.id.HomeActivityTitle_RelativeLayout);
        HomeActivity_isLoginRelativeLayout = findViewById(R.id.HomeActivity_isLoginRelativeLayout);
        HomeActivity_RelativeLayout = findViewById(R.id.HomeActivity_RelativeLayout);
        HomeActivity_isLoginTextView = findViewById(R.id.HomeActivity_isLoginTextView);
        HomeActivity_PlusLinearLayout = findViewById(R.id.HomeActivity_PlusLinearLayout);
        plusList.clear();
        plusList.add("操作记录");
        plusList.add("操作提醒");
    }
    private AlertDialog dialog1;
    public void showTipDialog(ArrayList<String> pmList) {
        if (dialog1==null){
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
            dialog1 = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(qx + "权限为应用必要权限，请前往设置页面进行授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转至当前应用的权限详情页面
                            JumpPermissionManagement.GoToSetting(HomeActivity.this);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                    && !Settings.canDrawOverlays(HomeActivity.this)){
                                if (dialogs==null){
                                    showTipDialog1(ACCESSREQUEST_CODEs, "定位");
                                }
                            }else {
                                MusicService.start(HomeActivity.this);
                                WalkingTrajectoryService.start(HomeActivity.this);
                                WebSocketService.start(HomeActivity.this);
                                if (!hasPermissionToReadNetworkStats()){
                                    showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                                }
                            }
                        }
                    })
                    .create();
            dialog1.show();
        }
    }
    private AlertDialog dialogs;
    public void showTipDialog1(int aa, String msg) {
             dialogs = new AlertDialog.Builder(this)
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
                            perMissions.add("其他应用上层显示权限");
                            if (!hasPermissionToReadNetworkStats()){
                                showTipDialog(PHoneMonitoringCODE_WINDOW1, "手机软件使用记录功能");
                            }
                        }
                    })
                    .create();
            dialogs.show();
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
                    ActivityCompat.requestPermissions(HomeActivity.this, Requst, REQUEST_CODE);
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
        return ContextCompat.checkSelfPermission(HomeActivity.this, permission) ==
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

}