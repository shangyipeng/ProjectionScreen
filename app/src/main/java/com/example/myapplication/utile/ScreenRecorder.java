package com.example.myapplication.utile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Surface;

import java.io.File;

public class ScreenRecorder {
    private static ScreenRecorder instance;
    private final String a = "ScreenRecorder";
    private String filepath;
    private MediaRecorder mediaRecorder;
    private Context context;

    public static synchronized ScreenRecorder getInstance(Context context) {
        ScreenRecorder screenRecorder;
        synchronized (ScreenRecorder.class) {
            if (instance == null) {
                synchronized (ScreenRecorder.class) {
                    if (instance == null) {
                        instance = new ScreenRecorder(context);
                    }
                }
            }
            screenRecorder = instance;
        }
        return screenRecorder;
    }

    public ScreenRecorder(Context context) {
        this.context = context;
        this.filepath = AppConfig.RECORD_AUDIO_PATH+System.currentTimeMillis()+".mp4";
    }

    @SuppressLint({"NewApi"})
    private boolean createMediaRecorder(int width, int height, int needAudio, int bitRate) {
        try {
            if (this.mediaRecorder != null) {
                this.mediaRecorder.release();
                this.mediaRecorder=null;
            }
            this.mediaRecorder = new MediaRecorder();
            if (needAudio == 1) {
                this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }
            this.mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            this.mediaRecorder.setOutputFile(new File(filepath).getAbsolutePath());
            this.mediaRecorder.setVideoSize(width,height);
            this.mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            if (needAudio == 1) {
                this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            }
            this.mediaRecorder.setVideoEncodingBitRate(bitRate);
            this.mediaRecorder.setVideoFrameRate(10);
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    /**
     * 创建画布
     * @param width     宽
     * @param height    高
     * @param needAudio 是否需要音频录制  audio1
     * @param bitRate   设置录制的视频编码比特率
     * @return
     */
    public boolean create(int width, int height, int needAudio, int bitRate) {

//        RootUtils.chmod("777", this.filepath);
//        File f=new File(AppConfig.RECORD_AUDIO_PATH);
//        if(!f.exists()){
//            f.mkdirs();
//        }
//        if (new File(this.filepath).exists()) {
//            new File(this.filepath).delete();
//            try {
//                new File(this.filepath).createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        RootUtils.chmod("777", filepath);
        File file=new File(AppConfig.RECORD_AUDIO_PATH);
        if(!file.getParentFile().exists()){
            file.mkdirs();
        }
        if (new File(filepath).exists()) {
            new File(filepath).delete();
            try {
                new File(filepath).createNewFile();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }else{
            //不存在就创建文件
            try {
                new File(filepath).createNewFile();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (!createMediaRecorder(width, height, needAudio, bitRate) || this.mediaRecorder == null) {
            return false;
        }
        try {
            mediaRecorder.prepare();
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean start() {
        try {
            this.mediaRecorder.start();
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public void stop() {
        if(mediaRecorder==null){
            return;
        }
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
    }

    public Surface getSurface() {
        MediaRecorder mediaRecorder = this.mediaRecorder;
        if (mediaRecorder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return mediaRecorder.getSurface();
            }
        }
        return null;
    }

    public String getFilePath(){
        return filepath;
    }
}
