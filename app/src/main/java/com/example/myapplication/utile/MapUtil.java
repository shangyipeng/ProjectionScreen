package com.example.myapplication.utile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.Uri;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.example.myapplication.R;

public class MapUtil {
    public final static String BAIDU_PKG = "com.baidu.BaiduMap"; //百度地图的包名

    public final static String GAODE_PKG = "com.autonavi.minimap";//高德地图的包名

    public static BitmapDescriptor bmArrowPoint = null;

    public static BitmapDescriptor bmStart = null;

    public static BitmapDescriptor bmEnd = null;

    public static BitmapDescriptor bmGeo = null;

    public static BitmapDescriptor bmGcoding = null;

    public static BitmapDescriptor bmCs = null;

    public static BitmapDescriptor bmJsc = null;

    public static BitmapDescriptor bmJzw = null;

    public static BitmapDescriptor bmStay = null;

    /**
     * 创建bitmap，在MainActivity onCreate()中调用
     */
    public static void init() {
        bmArrowPoint = BitmapDescriptorFactory.fromResource(R.mipmap.icon_point);
        bmStart = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start);
        bmEnd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_end);
        bmGeo = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);
        bmGcoding = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        bmCs = BitmapDescriptorFactory.fromResource(R.mipmap.icon_sc);
        bmJsc = BitmapDescriptorFactory.fromResource(R.mipmap.icon_jsc);
        bmJzw = BitmapDescriptorFactory.fromResource(R.mipmap.icon_jzw);
        bmStay = BitmapDescriptorFactory.fromResource(R.mipmap.icon_stay);
    }

    /**
     * 回收bitmap，在MainActivity onDestroy()中调用
     */
    public static void clear() {
        bmArrowPoint.recycle();
        bmStart.recycle();
        bmEnd.recycle();
        bmGeo.recycle();
        bmCs.recycle();
        bmJsc.recycle();
        bmJzw.recycle();
        bmStay.recycle();
    }

    public static BitmapDescriptor getMark(Context context, int index) {
        Resources res = context.getResources();
        int resourceId;
        if (index <= 10) {
            resourceId = res.getIdentifier("icon_mark" + index, "mipmap", context.getPackageName());
        } else {
            resourceId = res.getIdentifier("icon_markx", "mipmap", context.getPackageName());
        }
        return BitmapDescriptorFactory.fromResource(resourceId);
    }

    public static void openBaidu(Context context, double latitude, double longitude) {
        Intent i1 = new Intent();
        double[] position = GPSUtil.gcj02_To_Bd09(latitude, longitude);
        i1.setData(Uri.parse("baidumap://map/geocoder?location=" + position[0] + "," + position[1]));
        context.startActivity(i1);
    }

    /**
     * 启动高德App进行导航
     */
    public static void openGaoDe(Context context, double lat, double lng, String end) {
        Uri uri = Uri.parse("amapuri://route/plan/?dlat=" + lat + "&dlon=" + lng + "&dname=" + end + "&dev=0&t=0");
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addCategory("android.intent.category.DEFAULT");
        context.startActivity(intent);
    }


    /**
     * 检测地图应用是否安装
     *
     * @param context
     * @param packagename
     * @return
     */
    public static boolean checkMapAppsIsExist(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
}
