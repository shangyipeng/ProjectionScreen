package com.example.myapplication.activity;


import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utile.AppIconUtil;
import com.example.myapplication.utile.DataUtile;

public class AliasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataUtile.isHuaWei()){
            // 先禁用AliasMainActivity组件，启用alias组件
            AppIconUtil.set(AliasActivity.this, "com.example.myapplication.activity.AliasActivity", "com.example.myapplication.changeAfter1");
            // 10.0以下禁用alias后，透明图标就不存在了，10.0的必须开启，不然会显示主应用图标，10.0会有一个透明的占位图
            if (Build.VERSION.SDK_INT < 29) {
                // 禁用Alias1Activity
                AppIconUtil.disableComponent(this,".changeAfter1");
            }
        }else {
            // 先禁用AliasMainActivity组件，启用alias组件
            AppIconUtil.set(AliasActivity.this, "com.example.myapplication.activity.AliasActivity", "com.example.myapplication.changeAfter");
            // 10.0以下禁用alias后，透明图标就不存在了，10.0的必须开启，不然会显示主应用图标，10.0会有一个透明的占位图
            if (Build.VERSION.SDK_INT < 29) {
                // 禁用Alias1Activity
                AppIconUtil.disableComponent(this,".changeAfter");
            }
        }
        Window window = getWindow();
        // 设置窗口位置在左上角
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);
        finish();
    }
}
