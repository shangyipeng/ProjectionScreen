package com.example.myapplication.AudioCall;

import static com.example.myapplication.AudioCall.GlobalConfig.AUDIO_FORMAT;
import static com.example.myapplication.AudioCall.GlobalConfig.CHANNEL_CONFIG;
import static com.example.myapplication.AudioCall.GlobalConfig.SAMPLE_RATE_INHZ;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blankj.utilcode.util.LogUtils;
import com.example.basic.TRTCBaseActivity;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.R;
import com.example.myapplication.activity.BaseActivity1;
import com.example.myapplication.activity.HideActivity;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.tencent.trtc.debug.Constant;
import com.tencent.trtc.debug.GenerateTestUserSig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TRTC 语音通话的主页面
 *
 * 包含如下简单功能：
 * - 进入语音通话房间{@link AudioCallingActivity#enterRoom()}
 * - 退出语音通话房间{@link AudioCallingActivity#exitRoom()}
 * - 关闭/打开麦克风{@link AudioCallingActivity#()}
 * - 免提(听筒/扬声器切换){@link AudioCallingActivity#audioRoute()}
 *
 * - 详见接入文档{https://cloud.tencent.com/document/product/647/42047}
 *
 * Audio Call
 *
 * Features:
 * - Enter an audio call room: {@link AudioCallingActivity#enterRoom()}
 * - Exit an audio call room: {@link AudioCallingActivity#exitRoom()}
 * - Turn on/off the mic: {@link AudioCallingActivity#()}
 * - Switch between the speaker (hands-free mode) and receiver: {@link AudioCallingActivity#audioRoute()}
 *
 * - For more information, please see the integration document {https://cloud.tencent.com/document/product/647/42047}.
 */
public class AudioCallingActivity extends BaseActivity1 implements View.OnClickListener {

    private static final String TAG = "AudioCallingActivity";
    private static final int MAX_USER_COUNT = 6;

    //    private ImageView             mButtonMuteAudio;
    private ImageView mButtonAudioRoute;
    private ImageView mButtonHangUp;

    private TRTCCloud mTRTCCloud;
    private String mRoomId;
    private String mUserId;
    private boolean mAudioRouteFlag = true;
    private String Type = "";
    private TextView btn_RecordingDuration;
    private LinearLayout btn_recording;
    private boolean isDuration = false;//录音表示
    private boolean isRecording;
    private AudioRecord audioRecord;
    private String dateTimne;
    private TextView AudioCallingActivity_muserId;
    private LinearLayout incloud_finish;
    private TextView incloud_title;
    private TextView AudioCallingActivity_RecordingDuration;
    private boolean AUDIO=false;

    @Override
    public int getLayout() {
        return R.layout.audiocall_activity_calling;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        handleIntent();
        initView();
        enterRoom();
        handlers1.postDelayed(runnable1, 1000);
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
        }
    }

    protected void initView() {
        SharedPreferencesUtils.setParam(AudioCallingActivity.this,"JWebSocketClient1","");
        SharedPreferencesUtils.setParam(AudioCallingActivity.this,"JWebSocketClient2","");
        AudioCallingActivity_RecordingDuration=findViewById(R.id.AudioCallingActivity_RecordingDuration);
        AudioCallingActivity_muserId=findViewById(R.id.AudioCallingActivity_muserId);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText("获取音频");
        btn_RecordingDuration = findViewById(R.id.btn_RecordingDuration);
        btn_recording = findViewById(R.id.btn_recording);
//        mButtonMuteAudio = findViewById(R.id.btn_mute_audio);
        mButtonAudioRoute = findViewById(R.id.btn_audio_route);
        mButtonHangUp = findViewById(R.id.btn_hangup);
        Type = getIntent().getStringExtra("Type");
        mButtonAudioRoute.setSelected(mAudioRouteFlag);
        incloud_finish.setOnClickListener(this);
//        mButtonMuteAudio.setOnClickListener(this);
        mButtonAudioRoute.setOnClickListener(this);
        mButtonHangUp.setOnClickListener(this);
        btn_recording.setOnClickListener(this);
        AudioCallingActivity_muserId.setText(mUserId+"");

    }

    @Override
    protected void onPermissionGranted() {
        initView();
        enterRoom();
    }

    protected void enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudImplListener(AudioCallingActivity.this));

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        trtcParams.userId = mUserId;
        trtcParams.roomId = Integer.parseInt(mRoomId);
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(trtcParams.userId);

        mTRTCCloud.enableAudioVolumeEvaluation(2000, true);
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);

        mTRTCCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
        mTRTCCloud.setAudioCaptureVolume(75);
        mTRTCCloud.muteLocalAudio(true);
        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_AUDIOCALL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitRoom();
        aa1=0;
        aa=0;
        handlers.removeCallbacks(runnable);
        handlers1.removeCallbacks(runnable1);
    }

    private void exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud.stopLocalAudio();
            mTRTCCloud.exitRoom();
            mTRTCCloud.setListener(null);
        }
        mTRTCCloud = null;
        TRTCCloud.destroySharedInstance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.incloud_finish) {
            Intent resultIntent = new Intent();
            setResult(1260, resultIntent);
            finish();
        }
//        else if (id == R.id.btn_mute_audio) {
//            muteAudio();
//        }
        else if (id == R.id.btn_audio_route) {
            audioRoute();
        } else if (id == R.id.btn_hangup) {
            hangUp();
        } else if (id == R.id.btn_recording) {//录音
            if (isDuration) {
                stopRecord();
                isDuration = false;
                btn_RecordingDuration.setVisibility(View.GONE);
                handlers.removeCallbacks(runnable);
                aa = 0;
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
                File pcmFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), dateTimne + ".pcm");
                File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), dateTimne + ".wav");
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created");
                }
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AudioCallingActivity.this);
                Notification notification = setNotificationManager();
                notificationManager.notify(100, notification);
                deleteSingleFile(getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/" + dateTimne + ".pcm");
            } else {
                isDuration = true;
                aa = 0;
                handlers.postDelayed(runnable, 1000);
                btn_RecordingDuration.setVisibility(View.VISIBLE);
                startRecord();
            }
        }
    }

    private Notification setNotificationManager() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationChannelId = "notification_channel_id_01";
        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "TRTC Foreground Service Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("Channel description");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        //开启另一个页面
        Intent appIntent = new Intent(this, HideActivity.class);
        appIntent.setAction(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.putExtra("path", getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/" + dateTimne + ".wav");
        //设置启动模式
        PendingIntent contentIntent;
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentIntent = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
        builder.setContentIntent(contentIntent);//跳转activity
        builder.setSmallIcon(R.mipmap.app_icon);
        builder.setContentTitle("蓝色守护");
        builder.setContentText("点击可查看音频" + "\n" + dateTimne + ".wav");
        builder.setWhen(System.currentTimeMillis());
        return builder.build();
    }

    //1，首先创建一个Handler对象
    Handler handlers = new Handler();
    int aa = 0;
    //2，然后创建一个Runnable对像
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            aa++;
            String bb = "";
            if (aa >= 10 && aa < 60) {
                bb = "00:" + aa;
            } else if (aa >= 60) {
                int cc = aa / 60;
                aa=0;
                if (cc < 10) {
                    bb = "0" + cc + ":" + aa;
                } else {
                    bb = cc + ":" + aa;
                }
            } else if (aa < 10) {
                bb = "00:0" + aa;
            }
            btn_RecordingDuration.setText(bb);
            handlers.postDelayed(this, 1000);
        }
    };
    Handler handlers1 = new Handler();
    int aa1 = 0;
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            if (AUDIO){
                aa1++;
                String bb = "";
                if (aa1 >= 10 && aa1 <60) {
                    bb = "00:" + aa1;
                } else if (aa1 >= 60) {
                    int cc = aa1 / 60;
                    aa1=0;
                    if (cc < 10) {
                        bb = "0" + cc + ":" + aa1;
                    } else {
                        bb = cc + ":" + aa1;
                    }
                } else if (aa1 < 10) {
                    bb = "00:0" + aa1;
                }
                AudioCallingActivity_RecordingDuration.setText(bb);
            }
            handlers1.postDelayed(this, 1000);
        }
    };

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e(TAG, "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Log.e(TAG, "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            Log.e(TAG, "Copy_Delete.deleteSingleFile: 删除单个文件失败" + filePath$Name + "不存在！");
            return false;
        }
    }

    public void startRecord() {
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        //获取年月日
        dateTimne= simpleDateFormat.format(date);
        final byte data[] = new byte[minBufferSize];
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), dateTimne+".pcm");
        Log.e(TAG,getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString());
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }

        audioRecord.startRecording();
        isRecording = true;
        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != os) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Log.i(TAG, "run: close file output stream !");
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void stopRecord() {
        isRecording = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            //recordingThread = null;
        }
    }

    private void hangUp() {
        Intent resultIntent = new Intent();
        setResult(1260, resultIntent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent resultIntent = new Intent();
            setResult(1260, resultIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void audioRoute() {
        mAudioRouteFlag = !mAudioRouteFlag;
        mButtonAudioRoute.setSelected(mAudioRouteFlag);
        if (mAudioRouteFlag) {
            mTRTCCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
//            mButtonAudioRoute.setText(getString(R.string.audiocall_use_receiver));
            mButtonAudioRoute.setBackgroundResource(R.drawable.btn_audio_route1);
        } else {
            mTRTCCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
//            mButtonAudioRoute.setText(getString(R.string.audiocall_use_speaker));
            mButtonAudioRoute.setBackgroundResource(R.drawable.btn_audio_route2);

        }
    }

    private class TRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<AudioCallingActivity> mContext;

        public TRTCCloudImplListener(AudioCallingActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> arrayList, int i) {
        }

        @Override
        public void onNetworkQuality(TRTCCloudDef.TRTCQuality trtcQuality,
                                     ArrayList<TRTCCloudDef.TRTCQuality> arrayList) {
            LogUtils.d(TAG, "onNetworkQuality");
            AUDIO=true;

        }

        @Override
        public void onRemoteUserEnterRoom(String s) {
            Log.d(TAG, "onRemoteUserEnterRoom userId " + s);
        }

        @Override
        public void onRemoteUserLeaveRoom(String s, int i) {
            Log.d(TAG, "onRemoteUserLeaveRoom userId " + s);
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Log.d(TAG, "sdk callback onError");
            AudioCallingActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }
        }
    }

    public enum NetQuality {
        UNKNOW(0, "未定义"),
        EXCELLENT(1, "最好"),
        GOOD(2, "好"),
        POOR(3, "一般"),
        BAD(4, "差"),
        VBAD(5, "很差"),
        DOWN(6, "不可用");

        private int    code;
        private String msg;

        NetQuality(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public static String getMsg(int code) {
            for (NetQuality item : NetQuality.values()) {
                if (item.code == code) {
                    return item.msg;
                }
            }
            return "未定义";
        }
    }

}
