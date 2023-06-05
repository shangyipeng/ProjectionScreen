package com.example.myapplication.utile;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtiles {
    //需要申请权限的数组
    private static String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //读权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//写权限
            Manifest.permission.READ_CONTACTS, //联系人权限
            Manifest.permission.WRITE_CONTACTS,//联系人权限
            Manifest.permission.CALL_PHONE,//电话权限申请
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
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
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    //保存真正需要去申请的权限

    public static void checkPermissions(Activity activity, int RequestCode,int Scode,int Ecode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList=new ArrayList<>();
            for (int i = Scode; i < Ecode; i++) {
                permissionList.add(permissions[i]);
            }
            for (int i = 0; i < permissionList.size(); i++) {
                LogUtils.e(permissionList.get(i));
            }
            //有需要去动态申请的权限
            if (permissionList.size() > 0) {
                requestPermission(activity,RequestCode,permissionList);
            }
        }
    }
    //去申请的权限
    public static void requestPermission(Activity activity,int RequestCode,List<String > permissionList) {
        ActivityCompat.requestPermissions(activity,permissionList.toArray(new String[permissionList.size()]),RequestCode);
    }
}
