package com.example.myapplication.service;

import static com.example.myapplication.utile.ImageUtils.compress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.R;
import com.example.myapplication.activity.MyPhoneActivity;
import com.example.myapplication.bean.ReceiveResultBean;
import com.example.myapplication.service.base.SingleService;
import com.example.myapplication.utile.AppConfig;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.DisplayUtils;
import com.example.myapplication.utile.ForegroundServiceUtils;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.example.myapplication.utile.UploadUtils;
import com.google.gson.Gson;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.debug.Constant;
import com.tencent.trtc.debug.GenerateTestUserSig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScreenCutService extends SingleService {
    private final String name = "ScreenCutService";
    private VirtualDisplay virtualDisplay;
    private MediaProjection mediaProjection;
    private static byte[] imageByte;
    private ImageReader imageReader;
    String fromId = "";
    private static TRTCCloud mTRTCCloud = TRTCCloud.sharedInstance(ApplicTion.mContext);
    public ScreenCutService() {
        super("ScreenCutService");
    }

    public ScreenCutService(String str) {
        super(str);
    }

    @Override
    public void create() {
        Log.e("tag", "create");
    }

    @Override
    public void execute(Intent intent) {

        if (intent.getExtras() != null) {
            fromId = intent.getExtras().getString("fromId");
        }
        initStartCut();
    }

    private void initStartCut() {
        if (ApplicTion.screenIntent != null) {
            Log.e("tag", "开始截屏");
            ScreenCutService.imageByte = null;
            screenCut(fromId);
        } else {
            try {
                Log.e("tag", "启动授权");
                Intent intent2 = new Intent(context, ScreenCutService.ScreenUI.class);
                intent2.putExtra("fromId", fromId);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Intent intent) {
    }

    @SuppressLint("WrongConstant")
    public void screenCut(String fromId) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                //1.创建录制对象
//                mediaProjection = ((MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(ApplicTion.resultCode, ApplicTion.screenIntent);
//                Bitmap bitmap = screenShot(mediaProjection);
//                File file = compressImage(bitmap);
//                upload(file, fromId);
////                int width = DisplayUtils.getScreenWidth(context);
////                int height = DisplayUtils.getScreenHeight(context);
////                int dpi = (int) DisplayUtils.getScreenDensity(context);
//                //1.创建镜像
////                if (getOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
////                    imageReader = ImageReader.newInstance(width, height, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, 2);
////                    virtualDisplay = mediaProjection.createVirtualDisplay("screencut", width, height, dpi, 16, imageReader.getSurface(), null, null);
////                } else {
////                    imageReader = ImageReader.newInstance(height, width, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, 2);
////                    virtualDisplay = mediaProjection.createVirtualDisplay("screencut", height, width, dpi, 16, imageReader.getSurface(), null, null);
////                }
//                //2.开始截图
////                new Handler(Looper.getMainLooper()).post(new Runnable() {
////                    @Override
////                    public void run() {
////                        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
////                            public void onImageAvailable(ImageReader reader) {
////                                Log.e("tag", "截屏第一帧 onImageAvailable");
////                                Bitmap bitmap;
////                                try {
////                                    if (ScreenCutService.imageByte == null) {
////                                        Image acquireLatestImage = imageReader.acquireLatestImage();
////                                        int width = acquireLatestImage.getWidth();
////                                        int height = acquireLatestImage.getHeight();
////                                        Image.Plane[] planes = acquireLatestImage.getPlanes();
////                                        ByteBuffer buffer = planes[0].getBuffer();
////                                        int pixelStride = planes[0].getPixelStride();
////                                        Bitmap createBitmap = Bitmap.createBitmap(((planes[0].getRowStride() - (pixelStride * width)) / pixelStride) + width, height, Bitmap.Config.ARGB_8888);
////                                        createBitmap.copyPixelsFromBuffer(buffer);
////                                        Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap, 0, 0, width, height);
////                                        createBitmap2 = appendStr(createBitmap2);
////                                        File file = new File(AppConfig.PHOTO_PATH);
////                                        if (!file.exists()) {
////                                            file.mkdirs();
////                                        }
////                                        String filename = System.currentTimeMillis() + ".png";
////                                        boolean result = BitmapUtils.saveBitmap(createBitmap2, AppConfig.PHOTO_PATH + filename);
////
////                                        acquireLatestImage.close();
////                                        byte[] Bitmap2StrByBase64Png = ImageUtils.Bitmap2StrByBase64Png(createBitmap2);
////                                        if (Bitmap2StrByBase64Png != null) {
////                                            ScreenCutService.imageByte = Bitmap2StrByBase64Png;
////                                        }
////                                        if (createBitmap2 != null) {
////                                            createBitmap2.recycle();
////                                        }
////                                        if (result) {
////                                            //保存成功
////                                            Log.e("tag", "保存成功");
////                                            upload(new File(AppConfig.PHOTO_PATH + filename), fromId);
////                                            return;
////                                        } else {
////                                            //保存失败
////                                            Log.e("tag", "保存失败");
////                                        }
////                                        imageReader.close();
////                                    }
////                                } catch (Throwable th) {
////                                    th.printStackTrace();
////                                }
////                            }
////                        }, null);
////                    }
////                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        enterRoom();
        screenCapture();
    }

    private void enterRoom() {
        //mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
       // mTRTCCloud.setListener(new MyPhoneActivity.TRTCCloudImplListener());
        final TRTCCloudDef.TRTCParams screenParams = new TRTCCloudDef.TRTCParams();
        screenParams.sdkAppId = GenerateTestUserSig.SDKAPPID;
        String MyId = (String) SharedPreferencesUtils.getParam(ApplicTion.mContext, "MyId", Constant.ROOM_ID + "");
        String MyPhone = (String) SharedPreferencesUtils.getParam(ApplicTion.mContext, "MyPhone", Constant.USER_ID + "");
        MyId = "125673" + MyId;
        if (MyId.length() > 7) {
            MyId = "12567" + MyId;
        }
        // Constant.USER_ID;
        screenParams.userId = MyPhone;
        screenParams.roomId = Integer.parseInt(MyId);
        screenParams.userSig = GenerateTestUserSig.genTestUserSig(screenParams.userId);
        //TRTCRoleAnchor
        screenParams.role = TRTCCloudDef.TRTCRoleAnchor;
        //   mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);//音频采集
        mTRTCCloud.stopLocalAudio();
        //TRTC_APP_SCENE_VIDEOCALL
        //TRTCCloudDef.TRTCPublishTarget trtcPublishTarget = new TRTCCloudDef.TRTCPublishTarget();
        mTRTCCloud.enterRoom(screenParams, TRTCCloudDef.TRTC_APP_SCENE_LIVE);
        mTRTCCloud.muteLocalAudio(false);
        mTRTCCloud.muteRemoteAudio("18625196127", false);
    }

    private void exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud.stopLocalAudio();
            mTRTCCloud.stopLocalPreview();
            //     mTRTCCloud.exitRoom();
            mTRTCCloud.setListener(null);
        }
        //mTRTCCloud = null;
        //      TRTCCloud.destroySharedInstance();
    }
    private void screenCapture() {
            TRTCCloudDef.TRTCVideoEncParam encParams = new TRTCCloudDef.TRTCVideoEncParam();
            encParams.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_120_120;
            encParams.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
            encParams.videoFps = 10;
            encParams.enableAdjustRes = false;
            encParams.videoBitrate = 1200;
            //TRTC_VIDEO_STREAM_TYPE_BIG  高清 TRTC_VIDEO_STREAM_TYPE_SUB--分屏专用
            TRTCCloudDef.TRTCScreenShareParams params = new TRTCCloudDef.TRTCScreenShareParams();
            mTRTCCloud.startScreenCapture(TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL, encParams, params);
    }
    public static class ScreenUI extends Activity {
        private String fromId;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getIntent() != null) {
                fromId = getIntent().getStringExtra("fromId");
            }
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

        @SuppressLint({"NewApi"})
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
            Log.e("tag", "授权结果");
            if (requestCode == 1 && resultCode == -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("tag", "授权成功");
                        ApplicTion.screenIntent = intent;
                        ApplicTion.resultCode = resultCode;
                        intent.setAction("SAVE_SCREEN_BROADCAST");
                        sendBroadcast(intent);

                        Intent recordIntent = new Intent(ApplicTion.mContext, ScreenCutService.class);
                        Bundle recordBundle = new Bundle();
                        recordBundle.putString("fromId", fromId);
                        recordIntent.putExtras(recordBundle);
                        ForegroundServiceUtils.startService(ApplicTion.mContext, recordIntent);
                    }
                }, 1000);
                //屏幕录制权限授权成功

            }
            finish();
        }

        public void onDestroy() {
            super.onDestroy();
        }
    }

    public static int getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection.stop();
            }
            mediaProjection = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
//        ForegroundServiceUtils.startService(context, new Intent(context, GetLocationService.class));
    }

    @Override
    protected WindowManager getWindowManager() {
        return this.getWindowManager();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Bitmap screenShot(MediaProjection mediaProjection) {
        int width = DataUtile.screenWidth;
        int height =  DataUtile.screenHeight;
        Objects.requireNonNull(mediaProjection);
        @SuppressLint("WrongConstant")
        ImageReader imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 60);
        VirtualDisplay virtualDisplay = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            virtualDisplay = mediaProjection.createVirtualDisplay("screen", width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
        }
        SystemClock.sleep(1000);
        //取最新的图片
        Image image = imageReader.acquireLatestImage();
        // Image image = imageReader.acquireNextImage();
        //释放 virtualDisplay,不释放会报错
        virtualDisplay.release();
        return image2Bitmap(image);
    }


    //将Image转为Bitmap
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap image2Bitmap(Image image) {
        if (image == null) {
            System.out.println("image 为空");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        //截取图片
        // Bitmap cutBitmap = Bitmap.createBitmap(bitmap,0,0,width/2,height/2);
        //压缩图片
        // Matrix matrix = new Matrix();
        // matrix.setScale(0.5F, 0.5F);
        // System.out.println(bitmap.isMutable());
        // bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        image.close();
        return bitmap;
    }

    public File compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 20) {  //循环判断如果压缩后图片是否大于20kb,大于继续压缩 友盟缩略图要求不大于18kb
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        //图片名
        String filename = format.format(date);

        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        Log.d("=-=-=-=-=-", "compressImage: " + file);
        // recycleBitmap(bitmap);
        return file;
    }
}
