package com.example.myapplication.utile;

import android.Manifest;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DataUtile {
    public static String getUrl(){
        if (0 != (ApplicTion.mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)){
            //Debug 模式是打开状态  http://192.168.0.111:8302/
            return  "http://t2.yousin.cn:8302/";//内网域名
        }else {
            return  "http://t2.yousin.cn:8302/";//外网测试域名
        }
    }
    public static int screenWidth=0;//屏幕宽度
    public static int screenHeight=0;//屏幕高度
    public static int moveToX=0;//起点X轴
    public static int moveToY=0;//起点Y轴
    public static int lineToX=0;//终点X轴
    public static int lineToY=0;//终点Y轴
    public static boolean isWatch=true;//true命令下发 false命令接收
    public static int messageX=0;
    public static int messageY=0;
    public static boolean DataTD=false;
    public static GestureDetector detector;
    public static GestureDescription detector1;
    public static int ScreenX=0;//app在桌面的位置
    public static int ScreenX1=0;//app在桌面的位置
    public static int ScreenY=0;//app在桌面的位置
    public static int ScreenY1=0;//app在桌面的位置

    public static String  packageNames="";//界面变化后包名
    public static String  packageoperate="";//记录app前一个应用名称，根据名称判断是否显示遮罩层
    public static boolean  isShow=false;//判断是否隐藏

    public static void printRunningService(Context context){
        Log.e("tag","*****************************");
        Log.e("tag","***当前运行中服务");
        Log.e("tag","*****************************");
        ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services=activityManager.getRunningServices(1000);
        for (int i = 0; i <services.size(); i++) {
            ActivityManager.RunningServiceInfo info=services.get(i);
            Log.e("tag",""+info.service.getClassName());
        }
        Log.e("tag","*****************************");
        Log.e("tag","***任务结束");
        Log.e("tag","*****************************");
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static String getPhoneBrand() {

        String manufacturer = Build.MANUFACTURER;
        if (manufacturer != null && manufacturer.length() > 0) {
            return manufacturer.toLowerCase();
        } else {
            return "unknown";
        }

    }

    public static boolean isHuaWei(){
        String phoneBrand = getPhoneBrand();
        return phoneBrand.contains("HUAWEI") || phoneBrand.contains("OCE")
                || phoneBrand.contains("huawei") || phoneBrand.contains("honor");
    }
    /**
     * 获取当前时间
     */
    public static String getdataTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        //获取年月日
        return simpleDateFormat.format(date);
    }
    /**
     * 获取当前时间
     */
    public static String getdataTime1(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        //获取年月日
        return simpleDateFormat.format(date);
    }

    /**
     *
     * 日期格式字符串转换时间戳
     */
    public static long compareDayTime(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(date).getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     *
     * 时间戳转换日期格式字符串
     */
    public static String TimeDayCompare(long dates) {
        Date date = new Date(dates);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return format.format(date);
    }
    public static String TimeDayCompare1(long dates) {
        Date date = new Date(dates);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    /**
     * 获取时间差值 毫秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int getTimeRange(String startTime, String endTime) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*计算时间差*/
        Date begin = simpleDateFormat.parse(startTime);
        Date end = simpleDateFormat.parse(endTime);
        long diff = end.getTime() - begin.getTime();
        /*计算天数*/
        long days = diff / (1000 * 60 * 60 * 24);
        if (days<=0){
            days=1;
        }
        if (days > 0) {//天
            return (int) days ;
        }
        return 0;
    }
    //去重
    public static List<String> SetDeduplication(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }
    //获取我的token
    public static String  getMyToken() {
       String token= (String) SharedPreferencesUtils.getParam(ApplicTion.mContext,"MyToken","");
        LogUtils.e("getMyToken--  "+token);
       return token;
    }
    //获取对方token
    public static String  getTOToken() {
        String token= (String) SharedPreferencesUtils.getParam(ApplicTion.mContext,"TOToken","");
        LogUtils.e("getTOToken  "+token);
        return token;
    }
    //获取我的id
    public static String  getMyID() {
        String token= (String) SharedPreferencesUtils.getParam(ApplicTion.mContext,"MyId","");
        LogUtils.e("getMyId--  "+token);
        return token;
    }
    //获取我的手机号码
    public static String getMyPhone(){
        String Phone= (String) SharedPreferencesUtils.getParam(ApplicTion.mContext,"MyPhone","");
        LogUtils.e("getMyPhone--  "+Phone);
        return Phone;
    }
    //获取我的密码
    public static String getMyPwd(){
        String Mypwd= (String) SharedPreferencesUtils.getParam(ApplicTion.mContext,"Mypwd","");
        LogUtils.e("Mypwd--  "+Mypwd);
        return Mypwd;
    }
    //获取对方手机号码
    public static String getToPhone(){
        String Phone= (String) SharedPreferencesUtils.getParam(ApplicTion.mContext,"TOPhone","");
        LogUtils.e("getToPhone--  "+Phone);
        return Phone;
    }
    public static synchronized Drawable byteToDrawable(String icon) {

        byte[] img=Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img,0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);

            return drawable;
        }
        return null;

    }
    public static synchronized  String drawableToByte(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();

            String icon= Base64.encodeToString(imagedata, Base64.DEFAULT);
            return icon;
        }
        return null;
    }
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
    //重复点击
        //    两次点击按钮之间的点击间隔不能少于1000毫秒
        private static final int MIN_CLICK_DELAY_TIME = 1000;
        private static long lastClickTime;

        public static boolean isFastClick() {
            boolean flag = false;
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                flag = true;
            }
            lastClickTime = curClickTime;
            return true;
        }
    /**
     * 成功效果，动图
     */
    public static PopupWindow popupWindow;
    public static void getpopupwindow(Activity activity, View view) {
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
            Glide.with(activity).asGif().load(R.drawable.lodings).into(successpopupwindow1_image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        popupWindow = new PopupWindow(customView, 300, 300);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0x00);
        //为了让pop弹出来点击外部不消失且不会穿透下部，把pop传递给activity的dispatchTouchEvent事件去消费点击事件
        setComparePop(popupWindow);
    }
    public static void setComparePop(PopupWindow pop) {
        popupWindow = pop;
    }
    public static void dissePopup(){
        if (popupWindow!=null){
            popupWindow.dismiss();
        }
    }
}
