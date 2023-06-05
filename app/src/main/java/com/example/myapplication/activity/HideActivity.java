package com.example.myapplication.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.core.content.FileProvider;

import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.bean.TouchEvent;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.tencent.trtc.debug.Constant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HideActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String Filepath=getIntent().getStringExtra("path");
        //开启另一个页面
//        File file = new File(Filepath);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //7.0以上跳转系统文件需用FileProvider，参考链接：https://blog.csdn.net/growing_tree/article/details/71190741
//        Uri uri = FileProvider.getUriForFile(HideActivity.this,"com.example.myapplication",file);
//        intent.setData(uri);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent,200);
//        finish();
        if (Filepath.indexOf("/storage/emulated/0")!=-1){
            Filepath=Filepath.replace("/storage/emulated/0","");
        }
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:"
                + Filepath.replace("/", "%2f"));
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivity(intent);
        finish();
    }
}
