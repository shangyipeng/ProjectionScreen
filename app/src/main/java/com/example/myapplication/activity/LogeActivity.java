package com.example.myapplication.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.BaseBean;
import com.example.myapplication.bean.LogeBean;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.service.WebSocketService;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录页
 */
public class LogeActivity extends BaseActivity implements ContractInterface.View {
    private LinearLayout incloud_finish;//返回
    private TextView incloud_title;//头部标题
    private AppCompatEditText et_phone;
    private AppCompatEditText et_pwd;
    private AppCompatButton btn_login;
    private LogeActivity ctx;
    private CheckBox cb;
    private String phone;
    private String pwdStr;
    private TextView tv_regist;
    ContractInterface.Presenter presenter;
    int REQUEST_CODE = 186;


    @Override
    public int getLayout() {
        return R.layout.activity_loge;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        String token= (String) SharedPreferencesUtils.getParam(LogeActivity.this,"MyToken","");
        if (!token.equals("")){
            Intent intent=new Intent(LogeActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
        ctx = this;
        initUI();
        initData();
    }

    private void initData() {
        tv_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ctx, RegistActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的用户名和密码
                phone = et_phone.getText().toString().trim();
                pwdStr = et_pwd.getText().toString().trim();
                UserBean userBean = new UserBean();
                userBean.phone = phone;
                userBean.password = pwdStr;
                //判断用户输入的用户名和密码是否为空
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(pwdStr)) {
                    Toast.makeText(ctx, "手机号或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SharedPreferencesUtils.setParam(ctx, "MyPhone", phone);
                    SharedPreferencesUtils.setParam(ctx, "Mypwd", pwdStr);
                    SharedPreferencesUtils.setParam(ctx, "MyPhone1", phone);
                    //判断记住密码是否选中
                    if (cb.isChecked()) {
                        //选中之后对用户输入的密码进行保存
                        SharedPreferencesUtils.setParam(ctx, "MyPhone1", phone);
                        SharedPreferencesUtils.setParam(ctx, "Mypwd1", pwdStr);

                    } else {
                        SharedPreferencesUtils.setParam(ctx, "MyPhone1", "");
                        SharedPreferencesUtils.setParam(ctx, "Mypwd1", "");
                    }
                    //如果用户名和密码都不为空连接服务器进行判断用户名和密码是否正确;
                    if (checkNetwork()) {
                        String ToPhone=DataUtile.getToPhone();
                        if (!ToPhone.equals("")){
                            if (ToPhone.equals(phone)){
                                Toast.makeText(LogeActivity.this,"账号重复登录",Toast.LENGTH_SHORT).show();
                                et_phone.setText("");
                                et_pwd.setText("");
                                SharedPreferencesUtils.setParam(ctx, "MyPhone1", "");
                                SharedPreferencesUtils.setParam(ctx, "Mypwd1", "");
                            }else {
                                Map<String, Object> map = new HashMap<>();
                                map.put("username", phone);
                                map.put("password", pwdStr);
                                map.put("scene", "account");
                                presenter.presenter(map, "api/login/check?","POST","");
                            }
                        }else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("username", phone);
                            map.put("password", pwdStr);
                            map.put("scene", "account");
                            presenter.presenter(map, "api/login/check?","POST","");
                        }

                    } else {
                        Toast.makeText(ctx, "断网了", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    private void initUI() {
        presenter = new MyPresenter(LogeActivity.this);
        et_phone = findViewById(R.id.et_phone);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);
        cb = findViewById(R.id.cb);
        tv_regist = findViewById(R.id.tv_regist);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText("登录账号");
        String phone = (String) SharedPreferencesUtils.getParam(ctx, "MyPhone1", "");
        String pwd = (String) SharedPreferencesUtils.getParam(ctx, "Mypwd1", "");
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
            et_phone.setText(phone);
            et_pwd.setText(pwd);
            cb.setChecked(true);
        }
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogeActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Bundle bundle = data.getExtras(); // 继续获取intent携带的数据
            if (bundle != null) {
                String Phone = bundle.getString("Phone");
                String password = bundle.getString("password");
                et_phone.setText(Phone);
                et_pwd.setText(password);
            }
        }
    }

    //数据返回
    @Override
    public void View(String o) {
        Gson gson=new Gson();
        BaseBean baseBean=gson.fromJson(o, BaseBean.class);
        if (baseBean.code==200){
            LogeBean bean = gson.fromJson(o, LogeBean.class);
            SharedPreferencesUtils.setParam(LogeActivity.this,"MyToken",bean.data.token+"");
            SharedPreferencesUtils.setParam(LogeActivity.this,"MyId",bean.data.id+"");
            Intent intent=new Intent(LogeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else {
            SharedPreferencesUtils.setParam(LogeActivity.this,"MyToken","");
            SharedPreferencesUtils.setParam(LogeActivity.this,"MyId","");
            showFinishedDialog(baseBean.msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(LogeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showFinishedDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}