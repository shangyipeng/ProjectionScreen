package com.tencent.trtc.audiocall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import java.io.File;

public class HideActivity1 extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String Filepath=getIntent().getStringExtra("path");
        //开启另一个页面
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        File file = new File(Filepath);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "video/*");
//        startActivity(intent);
//        finish();
        File file = new File(Filepath);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //7.0以上跳转系统文件需用FileProvider，参考链接：https://blog.csdn.net/growing_tree/article/details/71190741
        Uri uri = FileProvider.getUriForFile(HideActivity1.this,"com.example.myapplication",file);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,200);
        finish();
    }

}
