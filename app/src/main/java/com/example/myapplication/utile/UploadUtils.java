package com.example.myapplication.utile;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;

public class UploadUtils {
    public static void upload(File file, OnFileUploadListener listener) {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        LogUtils.e( file.getAbsoluteFile() + "   123");

    }

    //  0远程拍照1远程录音2远程截屏3远程摄像4远程录屏5获取相册6获取录音7时间录屏录音8程序录屏录音
    public static void uploadRecordFile(String name, String fileUrl, String type, OnFileUploadListener listener) {
        long timecurrentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String createtime = sdfTwo.format(timecurrentTimeMillis);
        LogUtils.d("ret", "yangjie    下载的地址====" + createtime);
    }

    public interface OnFileUploadListener {
        void onUploadSuccess(String localPath, String netPath);

        void onUploadFailed(String message);
    }


    public interface OnFileUploadListener1 {
        void onUploadSuccess(String localPath, String netPath, String time);

        void onUploadFailed(String message);
    }

    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("TAG", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Log.e("TAG", "删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            Log.e("TAG", "删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }
}
