package com.example.myapplication.videocall;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.basic.TRTCBaseActivity;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.activity.BaseActivity1;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.bean.DistalEndBean;
import com.example.myapplication.utile.DataUtile;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCallingActivity extends BaseActivity1 implements View.OnClickListener , ContractInterface.View {

    private static final String TAG = "VideoCallingActivity";
    private static final int    OVERLAY_PERMISSION_REQ_CODE = 1234;

    private TXCloudVideoView mTXCVVLocalPreviewView;
    private ImageView           mButtonMuteVideo;
    private Button mButtonMuteAudio;
    private ImageView           mButtonSwitchCamera;
    private ImageView           mButtonAudioRoute;

    private TRTCCloud              mTRTCCloud;
    private TXDeviceManager        mTXDeviceManager;
    private boolean                mIsFrontCamera  = false;
    private int                    mUserCount      = 0;
    private String                 mRoomId;
    private String                 mUserId;
    private boolean                mAudioRouteFlag = true;
    private String Type="";
    private ContractInterface.Presenter presenter;
    private LinearLayout incloud_finish;
    private TextView incloud_title;

    @Override
    public int getLayout() {
        return R.layout.videocall_activity_calling;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        handleIntent();
        presenter=new MyPresenter(this);
        if (checkPermission()) {
            initView();
            enterRoom();
        }
    }
    private void handleIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.getStringExtra(Constant.USER_ID) != null) {
                mUserId = intent.getStringExtra(Constant.USER_ID);
            }
            if (intent.getStringExtra(Constant.ROOM_ID) != null) {
                mRoomId = intent.getStringExtra(Constant.ROOM_ID);
            }
            Type=getIntent().getStringExtra("Type");
        }
    }

    private void initView() {
        SharedPreferencesUtils.setParam(VideoCallingActivity.this,"JWebSocketClient1","");
        SharedPreferencesUtils.setParam(VideoCallingActivity.this,"JWebSocketClient2","");
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText("获取视频");
        mTXCVVLocalPreviewView = findViewById(R.id.txcvv_main);
        mButtonMuteVideo = findViewById(R.id.btn_mute_video1);
        mButtonMuteAudio = findViewById(R.id.btn_mute_audio);
        mButtonSwitchCamera = findViewById(R.id.btn_switch_camera);
        mButtonAudioRoute = findViewById(R.id.btn_audio_route);

        incloud_finish.setOnClickListener(this);
        mButtonMuteVideo.setOnClickListener(this);
        mButtonMuteAudio.setOnClickListener(this);
        mButtonSwitchCamera.setOnClickListener(this);
        mButtonAudioRoute.setOnClickListener(this);
    }

    private void enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudImplListener(VideoCallingActivity.this));
        mTXDeviceManager = mTRTCCloud.getDeviceManager();

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        trtcParams.userId = mUserId;
        trtcParams.roomId = Integer.parseInt(mRoomId);
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(trtcParams.userId);

        mTRTCCloud.startLocalPreview(mIsFrontCamera, mTXCVVLocalPreviewView);
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);
        mTRTCCloud.setAudioCaptureVolume(75);
        mTRTCCloud.muteLocalAudio(true);//关闭麦克风
        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);

    }

    @Override
    protected void onStop() {
        super.onStop();
        requestDrawOverLays();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitRoom();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.incloud_finish) {
            Intent resultIntent = new Intent();
            setResult(1259, resultIntent);
            finish();
        } else if (id ==R.id.btn_mute_video1) {
            muteVideo();
        } else if (id == R.id.btn_mute_audio) {
            muteAudio();
        } else if (id ==R.id.btn_switch_camera) {
            switchCamera();
        } else if (id ==R.id.btn_audio_route) {
            audioRoute();
        } else if (id ==R.id.iv_return) {
            floatViewClick();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent resultIntent = new Intent();
            setResult(1259, resultIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void floatViewClick() {
        Intent intent = new Intent(this, VideoCallingActivity.class);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void muteVideo() {
        boolean isSelected = mButtonMuteVideo.isSelected();
        if (!isSelected) {
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("VIDEOCalling_muteVideo");
            distalEndBean.setValue("1");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=new Gson().toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
            mTRTCCloud.stopLocalPreview();
            mButtonMuteAudio.setBackgroundResource(R.drawable.btn_mute_video2);
        } else {
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("VIDEOCalling_muteVideo");
            distalEndBean.setValue("0");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=new Gson().toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
            mTRTCCloud.startLocalPreview(mIsFrontCamera, mTXCVVLocalPreviewView);
            mButtonMuteAudio.setBackgroundResource(R.drawable.btn_mute_video);
        }
        mButtonMuteVideo.setSelected(!isSelected);
    }

    private void muteAudio() {
        boolean isSelected = mButtonMuteAudio.isSelected();
        if (!isSelected) {
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("VIDEOCalling_muteAudio");
            distalEndBean.setValue("0");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=new Gson().toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
            mTRTCCloud.muteLocalAudio(true);
            mButtonMuteAudio.setBackgroundResource(R.drawable.btn_mute_audio2);
        } else {
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("VIDEOCalling_muteAudio");
            distalEndBean.setValue("1");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=new Gson().toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");

            mTRTCCloud.muteLocalAudio(false);
            mButtonMuteAudio.setBackgroundResource(R.drawable.btn_mute_audio1);
        }
        mButtonMuteAudio.setSelected(!isSelected);
    }

    private void switchCamera() {
        mIsFrontCamera = !mIsFrontCamera;
        mTXDeviceManager.switchCamera(mIsFrontCamera);
        if (mIsFrontCamera) {
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("VIDEOCalling_switchCamera");
            distalEndBean.setValue("0");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=new Gson().toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
            Toast.makeText(this, R.string.videocall_user_front_camera,Toast.LENGTH_SHORT).show();
            mButtonSwitchCamera.setBackgroundResource(R.drawable.btn_switch_camera);
        } else {
            DistalEndBean distalEndBean=new DistalEndBean();
            distalEndBean.setField("VIDEOCalling_switchCamera");
            distalEndBean.setValue("1");
            distalEndBean.setPhone(DataUtile.getMyPhone()+"");
            String value=new Gson().toJson(distalEndBean);
            Map<String ,Object> map=new HashMap<>();
            map.put("field","websocket");
            map.put("value",value);
            map.put("mobile",DataUtile.getToPhone()+"");
            presenter.presenter(map,"/api/user/edit?","POST",DataUtile.getTOToken()+"");
            Toast.makeText(this, R.string.videocall_user_back_camera,Toast.LENGTH_SHORT).show();
            mButtonSwitchCamera.setBackgroundResource(R.drawable.btn_switch_camera);
        }
    }

    private void audioRoute() {
        if (mAudioRouteFlag) {
            mAudioRouteFlag = false;
            mTXDeviceManager.setAudioRoute(TXDeviceManager.TXAudioRoute.TXAudioRouteEarpiece);
//            mButtonAudioRoute.setText(getString(R.string.videocall_use_speaker));
            mButtonAudioRoute.setBackgroundResource(R.drawable.btn_audio_route2);
        } else {
            mAudioRouteFlag = true;
            mTXDeviceManager.setAudioRoute(TXDeviceManager.TXAudioRoute.TXAudioRouteSpeakerphone);
//            mButtonAudioRoute.setText(getString(R.string.videocall_use_receiver));
            mButtonAudioRoute.setBackgroundResource(R.drawable.btn_audio_route1);
        }
    }

    @Override
    public void View(String o) {

    }

    private class TRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<VideoCallingActivity> mContext;

        public TRTCCloudImplListener(VideoCallingActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            LogUtils.d(TAG,
                    "onUserVideoAvailable userId " + userId + ", mUserCount " + mUserCount + ",available " + available);
            if (available) {
                mTXCVVLocalPreviewView.setVisibility(View.VISIBLE);
                mTRTCCloud.startRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG,
                        mTXCVVLocalPreviewView);
            }

        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Log.e(TAG, "sdk callback onError"+errMsg);
            VideoCallingActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }
        }
    }

    public void requestDrawOverLays() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && !Settings.canDrawOverlays(VideoCallingActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + VideoCallingActivity.this.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
    }
}
