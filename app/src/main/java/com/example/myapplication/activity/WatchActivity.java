package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.example.basic.TRTCBaseActivity;
import com.example.myapplication.R;
import com.example.myapplication.utile.DataUtile;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.tencent.trtc.debug.Constant;
import com.tencent.trtc.debug.GenerateTestUserSig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

/**
 * 观看分享的录屏
 */
public class WatchActivity extends TRTCBaseActivity {

    private TRTCCloud mTRTCCloud;
    private TXCloudVideoView mScreenShareView;
    private LinearLayout mLLRoomInfo;
    private TXCloudVideoView       mTXCloudPreviewView;
    private String isSw="点击:";
    private LinearLayout Distal_back,Distal_home,Distal_Taskbar;
    private int width=0;
    private int height=0;
    private LinearLayout LinearLayout_operate;
    private int ActualHeight=0;
    private String ussid;
    PopupWindow popupWindow;
    RelativeLayout WatchActivity_RelativeLayout;
    public boolean availables=false;
    Handler handler1=new Handler();
    Handler handler2=new Handler();
    private String roomId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        EventBus.getDefault().register(this);
        ussid=getIntent().getStringExtra("ussid");
        roomId=getIntent().getStringExtra("roomId");
        if (checkPermission()) {
            initView();
            enterRoom();
        }

    }

    private void initView() {
        LinearLayout_operate=findViewById(R.id.LinearLayout_operate);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
//            获取的是实际显示区域指定包含系统装饰的内容的显示部分
            width = getWindowManager().getCurrentWindowMetrics().getBounds().width();
            height = getWindowManager().getCurrentWindowMetrics().getBounds().height();
            Insets insets = getWindowManager().getCurrentWindowMetrics().getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            width=width - insets.right - insets.left;
            height=height - insets.bottom - insets.top;
        } else {
            //获取减去系统栏的屏幕的高度和宽度
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
        ActualHeight=height-LinearLayout_operate.getHeight();

        mTXCloudPreviewView=findViewById(R.id.txcvv_main_local);
        mScreenShareView = findViewById(R.id.live_cloud_remote_screenshare);
        mLLRoomInfo = findViewById(R.id.ll_room_info);
        findViewById(R.id.trtc_ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow!=null){
                    popupWindow.dismiss();
                }
                availables=false;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("isReload", "退出页面");
                setResult(10086, resultIntent);
                finish();
            }
        });
        //下发返回上一级命令
        Distal_back=findViewById(R.id.Distal_back);
        Distal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOperate("返回上一级");
            }
        });
        //下发返回主页
        Distal_home=findViewById(R.id.Distal_home);
        Distal_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOperate("返回主页");
            }
        });
        //下发返回任务栏
        Distal_Taskbar=findViewById(R.id.Distal_Taskbar);
        Distal_Taskbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOperate("打开任务栏");
            }
        });
        WatchActivity_RelativeLayout=findViewById(R.id.WatchActivity_RelativeLayout);

    }
    protected void enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudImplListener(WatchActivity.this));
        TRTCCloudDef.TRTCParams mTRTCParams = new TRTCCloudDef.TRTCParams();
        mTRTCParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        mTRTCParams.userId = ussid;
        mTRTCParams.roomId = Integer.parseInt(roomId);
        mTRTCParams.userSig = GenerateTestUserSig.genTestUserSig(mTRTCParams.userId);
        //TRTCRoleAudience TRTCCloudDef.TRTCRoleAnchor
        mTRTCParams.role = TRTCCloudDef.TRTCRoleAnchor ;

        mTRTCCloud.startLocalPreview(false, mTXCloudPreviewView);
        mTRTCCloud.stopLocalAudio();
        //TRTC_APP_SCENE_LIVE
        mTRTCCloud.enterRoom(mTRTCParams,TRTCCloudDef.TRTC_APP_SCENE_LIVE );
        mTRTCCloud.muteLocalAudio(true);//flase关闭本地音频
        handler1.postDelayed(new Runnable(){
            public void run() {
                //TODO
                if (!availables){
                    getpopupwindow(WatchActivity.this,WatchActivity_RelativeLayout);
                }
            }
        }, 1000);//2秒

        handler2.postDelayed(new Runnable(){
            public void run() {
                //TODO
                if (!availables){
                    Log.e("------------------","两秒重启");
                    if (popupWindow!=null){
                        popupWindow.dismiss();
                    }
                    exitRoom();
                    enterRoom();
                    sendOperate("关闭页面重新开始");
                }
            }
        }, 10000);//2秒
    }
    @Override
    protected void onDestroy() {
        if (popupWindow!=null){
            popupWindow.dismiss();
        }
        handler1.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
        exitRoom();
        super.onDestroy();
    }
    private class TRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<WatchActivity> mContext;

        public TRTCCloudImplListener(WatchActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onFirstVideoFrame(String s, int i, int i1, int i2) {
            super.onFirstVideoFrame(s, i, i1, i2);

        }
        @Override
        public void onEnterRoom(long result) {
            super.onEnterRoom(result);
            //房间进入失败重新进入
            if (result<0){
                sendOperate("关闭页面重新开始");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("isReload", "isReload");
                setResult(10089, resultIntent);
                finish();
            }
        }
        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            LogUtils.e( "onUserVideoAvailable userId " + userId + ",available " + available);
            availables=available;
            if (popupWindow!=null){
                popupWindow.dismiss();
            }
            if (available) {
                handler1.removeCallbacksAndMessages(null);
                handler2.removeCallbacksAndMessages(null);
                mTRTCCloud.startRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL, mScreenShareView);
                sendOperate("被控屏幕");
            } else {
                mTRTCCloud.stopRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL);
                availables=false;
                sendOperate("关闭页面重新开始");
            }
            super.onUserSubStreamAvailable(userId, available);
        }
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            LogUtils.d( "sdk callback onError");
            WatchActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    sendOperate("重新开启");
                }
            }
        }
    }
    private void exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud.stopLocalAudio();
            mTRTCCloud.stopLocalPreview();
            mTRTCCloud.exitRoom();
            mTRTCCloud.setListener(null);
        }
        mTRTCCloud = null;
        TRTCCloud.destroySharedInstance();
    }
    @Override
    protected void onPermissionGranted() {
        initView();
        enterRoom();
    }
    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            DataUtile.isWatch=true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //发送自定义消息的示例代码
    public void sendOperate(String operate) {
        try {
            // 自定义消息命令字, 这里需要根据业务定制一套规则，这里以0x1代表发送文字广播消息为例
            int cmdID = 0x1;
            String hello = operate;
            byte[] data = hello.getBytes("UTF-8");
            // reliable 和 ordered 目前需要一致，这里以需要保证消息按发送顺序到达为例
            if (mTRTCCloud!=null){
                mTRTCCloud.sendCustomCmdMsg(cmdID, data, false, false);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (mTRTCCloud!=null){
            mTRTCCloud.sendSEIMsg(operate.getBytes(), 1);
        }

    }
    /**
     * 成功效果，动图
     */
    public void getpopupwindow(Activity activity, View view) {
        //弹出选择对比列表的框
        if (popupWindow != null) {
            popupWindow = null;
        }
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.successpopupwindow, null);
        ImageView successpopupwindow1_image=(ImageView) customView.findViewById(R.id.successpopupwindow1_image);
        try {
            Glide.with(WatchActivity.this).asGif().load(R.drawable.lodings).into(successpopupwindow1_image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        popupWindow = new PopupWindow(customView, 300, 300);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0x00);
        //为了让pop弹出来点击外部不消失且不会穿透下部，把pop传递给activity的dispatchTouchEvent事件去消费点击事件
        WatchActivity.this.setComparePop(popupWindow);
    }
    public void setComparePop(PopupWindow pop) {
        this.popupWindow = pop;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReciverTouchEvent(String  event) {
        if (event.equals("完成")){
            int x= DataUtile.moveToX;
            int y=DataUtile.moveToY;
            int x1=DataUtile.lineToX;
            int y1=DataUtile.lineToY;
            if (Math.abs(x-x1)>200|Math.abs(y-y1)>200){
                isSw="滑动:";
            }else {
                isSw="点击:";
            }
            sendOperate(isSw+x+","+y+"-"+x1+"、"+y1);
        }

    }

}
