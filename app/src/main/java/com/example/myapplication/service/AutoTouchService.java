package com.example.myapplication.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.activity.MyPhoneActivity;
import com.example.myapplication.bean.TouchEvent;
import com.example.myapplication.bean.TouchPoint;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.example.myapplication.utile.TouchEventManager;
import com.example.myapplication.utile.WindowUtils;
import com.tencent.trtc.debug.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

/**
 * 无障碍服务-自动点击
 *
 * @date 2019/9/6 16:23
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class AutoTouchService extends AccessibilityService {

    private final String TAG = "AutoTouchService+++";
    //自动点击事件
    private TouchPoint autoTouchPoint;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper());
    private WindowManager windowManager;
    private TextView tvTouchPoint;
    //倒计时
    private float countDownTime;
    private DecimalFormat floatDf = new DecimalFormat("#0.0");
    //修改点击文本的倒计时
    private Runnable touchViewRunnable;

    @SuppressLint("WrongConstant")
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        handler = new Handler();
        EventBus.getDefault().register(this);
        windowManager = WindowUtils.getWindowManager(this);
        Constant.Message="辅助功能开启";
        EventBus.getDefault().post("辅助功能已开启");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = WindowUtils.getWindowManager(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReciverTouchEvent(TouchEvent event) {
        Log.d(TAG, "onReciverTouchEvent: " + event.toString());
        if (event.getTouchPoint() != null) {
            TouchEventManager.getInstance().setTouchAction(event.getAction());
            handler.removeCallbacks(autoTouchRunnable);
            if (event.getAction() == TouchEvent.ACTION_MOVE) {
                autoTouchPoint = event.getTouchPoint();
                onAutoMoveClick();
            } else {
                switch (event.getAction()) {
                    case TouchEvent.ACTION_START:
                        autoTouchPoint = event.getTouchPoint();
                        onAutoClick();
                        break;
                    case TouchEvent.ACTION_CONTINUE:
                        if (autoTouchPoint != null) {
                            onAutoClick();
                        }
                        break;
                    case TouchEvent.ACTION_PAUSE:
                        handler.removeCallbacks(autoTouchRunnable);
                        handler.removeCallbacks(touchViewRunnable);
                        break;
                    case TouchEvent.ACTION_STOP:
                        handler.removeCallbacks(autoTouchRunnable);
                        handler.removeCallbacks(touchViewRunnable);
                        autoTouchPoint = null;
                        break;
                }
            }
        }else if (event.getAction()>5){
            switch (event.getAction()){
                case TouchEvent.ACTION_BACK://返回上一级
                    this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    break;
                case TouchEvent.ACTION_HOME://返回主页
                    this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                    break;
                case TouchEvent.ACTION_RECENTS://打开任务栏
                    this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);//打开任务栏
                    break;
                case TouchEvent.ACTION_ALLOW://自动允许点击
                    send();
//                    //3，使用PostDelayed方法，调用此Runnable对象
//                    handlers.postDelayed(runnable, 1000);
                    break;
                case TouchEvent.ACTION_SCREEN://检测屏幕
                    if (DataUtile.isHuaWei()){
                        removeTouchView();
                        showTouchView();
                    }
                    break;
                    case TouchEvent.ACTION_CLOSESCREEN://取消遮罩层
                        if (DataUtile.isHuaWei()){
                            LogUtils.e("取消遮罩层   ACTION_CLOSESCREEN");
                            removeTouchView();
                        }
                        break;
            }
        }
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (DataUtile.isHuaWei()){
            if (accessibilityEvent.getEventType()== AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                String className = accessibilityEvent.getClassName().toString();
                String packageName = accessibilityEvent.getPackageName().toString();
                LogUtils.e(className+"    "+packageName);
                boolean a= (boolean) SharedPreferencesUtils.getParam(this,"IsShowHide",false);
                if (!a){
                    removeTouchView();
                }else {
                    if (packageName.equals("com.huawei.android.launcher")||packageName.equals("com.example.myapplication")){
                        if (className.equals("com.example.myapplication.activity.MyPhoneActivity")||className.equals("com.example.myapplication.HomeActivity")||
                                className.equals("com.example.myapplication.HomeActivity1")){
                            removeTouchView();
                        }else {
                            LogUtils.e(tvTouchPoint.isAttachedToWindow());
                            if (!tvTouchPoint.isAttachedToWindow()){
                                showTouchView();
                            }
                            DataUtile.packageoperate="";
                        }
                    }
                    if (className.equals("com.example.myapplication.activity.MyPhoneActivity")){
                        removeTouchView();
                    }
                }
            }
        }

    }
    private void send1() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        } else {
            recycle1(nodeInfo);
        }
    }
    public void recycle1(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if (info.getText()!=null){
                if (info.getText().toString().equals("蓝色守护")&& DataUtile.isShow==true){
                    DataUtile.packageoperate=info.getText().toString();
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle1(info.getChild(i));
                }
            }
        }
    }

    /**
     * 执行自动点击
     */
    private void onAutoClick() {
        if (autoTouchPoint != null) {
            handler.postDelayed(autoTouchRunnable, getDelayTime());
        }
    }

    /**
     * 执行滑动
     */
    private void onAutoMoveClick() {
        if (autoTouchPoint != null) {
            handler.postDelayed(autoTouchRunnable1, getDelayTime());
        }
    }

    private Runnable autoTouchRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "onAutoClick: " + "x=" + autoTouchPoint.getX() + " y=" + autoTouchPoint.getY());
            Path path = new Path();
            path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(
                            new GestureDescription.StrokeDescription(path, 0, 100))
                    .build();
            dispatchGesture(gestureDescription, new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d("AutoTouchService", "滑动结束" + gestureDescription.getStrokeCount());
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.d("AutoTouchService", "滑动取消");
                }
            }, null);
        }
    };

    private Runnable autoTouchRunnable1 = new Runnable() {
        @Override
        public void run() {
            LogUtils.d( "onAutoClick1: " + "x=" + autoTouchPoint.getX() + " y=" + autoTouchPoint.getY());
            LogUtils.d( "onAutoClick1: " + "x1=" + autoTouchPoint.getX1() + " y1=" + autoTouchPoint.getY1());
            Path path = new Path();//x轴是左右，Y轴是上下
            int x = autoTouchPoint.getX() - autoTouchPoint.getX1();
            int y = autoTouchPoint.getY() - autoTouchPoint.getY1();

            int leftX= DataUtile.screenWidth- (autoTouchPoint.getX()+Math.abs(autoTouchPoint.getX()-autoTouchPoint.getX1()));//剩余宽度
            if (leftX<(DataUtile.screenWidth/2)){
                if ((DataUtile.screenWidth/2)>autoTouchPoint.getX()){
                    path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());//起点
                    path.lineTo(autoTouchPoint.getX1(), autoTouchPoint.getY1());//终点
                }else {
                    path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());//起点
                    path.lineTo(autoTouchPoint.getX1(), autoTouchPoint.getY1());//终点
                }
            }else {
                path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());//起点
                path.lineTo(autoTouchPoint.getX1(), autoTouchPoint.getY1());//终点
            }
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(
                            new GestureDescription.StrokeDescription(path, 0, 200))
                    .build();
            dispatchGesture(gestureDescription, new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d("AutoTouchService", "滑动结束" + gestureDescription.getStrokeCount());
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.d("AutoTouchService", "滑动取消");
                }
            }, null);
        }
    };

    private long getDelayTime() {
        return 100;
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        removeTouchView();
    }

    private void removeTouchView() {
        if (windowManager != null && tvTouchPoint != null && tvTouchPoint.isAttachedToWindow()) {
            windowManager.removeView(tvTouchPoint);
        }
    }
    /**
     * 寻找窗体中的允许或者立即开始按钮，并且点击。
     */
    @SuppressLint("NewApi")
    private void send() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        } else {
            recycle(nodeInfo);
        }
    }

    //1，首先创建一个Handler对象
    Handler handlers=new Handler();
    //2，然后创建一个Runnable对像
    Runnable runnable=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            String handlersdelay= (String) SharedPreferencesUtils.getParam(AutoTouchService.this,"handlersdelay", "未点击");
            if (handlersdelay.equals("未点击")){
                handlers.postDelayed(this, 200);
            }else {
                send();
                handlers.postDelayed(this, 1000);
            }
        }
    };
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if (info.getText()!=null){
                int Maincodes= (int) SharedPreferencesUtils.getParam(this,"Maincodes",0);
                    if (info.getText().equals("立即开始")){
                        EventBus.getDefault().post("弹框已出现");
                        SharedPreferencesUtils.setParam(this,"handlersdelay", "已经选中");
                        if (Maincodes==0){
                            Maincodes++;
                            SharedPreferencesUtils.setParam(this,"Maincodes",Maincodes);
                        }else {
                            Maincodes++;
                            SharedPreferencesUtils.setParam(this,"Maincodes",Maincodes);
                            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    } else if (info.getText().equals("允许")){
                        EventBus.getDefault().post("弹框已出现");
                        SharedPreferencesUtils.setParam(this,"handlersdelay", "已经选中");
                        if (Maincodes==0){
                            Maincodes++;
                            SharedPreferencesUtils.setParam(this,"Maincodes",Maincodes);
                        }else {
                            Maincodes++;
                            SharedPreferencesUtils.setParam(this,"Maincodes",Maincodes);
                            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }

    //生成遮罩层，阻止点击
    /**
     * 设置遮罩层
     */
    private void showTouchView() {
        if (tvTouchPoint!=null&&tvTouchPoint.isAttachedToWindow()){
            windowManager.removeView(tvTouchPoint);
        }
            //创建触摸点View
            if (tvTouchPoint == null) {
                tvTouchPoint = (TextView) LayoutInflater.from(this).inflate(R.layout.window_touch_point, null);
            }
            //显示触摸点View
            if (windowManager != null && !tvTouchPoint.isAttachedToWindow()) {
                int ScreenX= (int) SharedPreferencesUtils.getParam(this,"ScreenX",0);
                int ScreenX1= (int) SharedPreferencesUtils.getParam(this,"ScreenX1",0);
                int ScreenY1= (int) SharedPreferencesUtils.getParam(this,"ScreenY",0);
                int ScreenY= (int) SharedPreferencesUtils.getParam(this,"ScreenY1",0);
                if (ScreenX==0){
                    ScreenX=292;
                    ScreenX1=544;
                    ScreenY1=1360;
                    ScreenY=1652;
                }
                LogUtils.e("----"+ScreenX+"-----"+ScreenX1);
                LogUtils.e("----"+ScreenY+"-----"+ScreenY1);
                int width =ScreenX-ScreenX1;
                int height = ScreenY1-ScreenY;
                WindowManager.LayoutParams params = WindowUtils.newWmParams(Math.abs(width), Math.abs(height));
                params.gravity = Gravity.START | Gravity.TOP;
                params.x = ScreenX;
                params.y = ScreenY+width+(width/2);
                tvTouchPoint.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });
                params.flags =  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                tvTouchPoint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                try {
                    windowManager.addView(tvTouchPoint, params);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
    }
}
