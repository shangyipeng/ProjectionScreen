package com.example.myapplication.activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Presenter.MyPresenter;
import com.example.myapplication.R;
import com.example.myapplication.bean.RegistBean;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.utile.DataUtile;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册页面
 */
public class RegistActivity extends BaseActivity implements ContractInterface.View{
    private LinearLayout incloud_finish;//返回
    private TextView incloud_title;//头部标题
    ContractInterface.Presenter presenter;
    private AppCompatEditText RegistActivity_phone;
    private AppCompatEditText RegistActivity_password;
    private AppCompatButton RegistActivity_tijiao;

    @Override
    public int getLayout() {
        return R.layout.activity_regist;
    }

    @Override
    public void setcCreate(Bundle savedInstanceState) {
        initFind();
    }
    //初始化
    private void initFind() {
        presenter=new MyPresenter(this);
        RegistActivity_password =  findViewById(R.id.RegistActivity_password);
        RegistActivity_phone =  findViewById(R.id.RegistActivity_phone);
        RegistActivity_tijiao=findViewById(R.id.RegistActivity_tijiao);
        incloud_finish=findViewById(R.id.incloud_finish);
        incloud_title=findViewById(R.id.incloud_title);
        incloud_title.setText("创建新账号");
        incloud_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Phone", "");
                resultIntent.putExtra("password", "");
                setResult(186, resultIntent);
                finish();
            }
        });
        RegistActivity_tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = RegistActivity_phone.getText().toString().trim();
                String password = RegistActivity_password.getText().toString().trim();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
                    return;
                } else {
                    UserBean userBean = new UserBean();
                    userBean.phone = phone;
                    userBean.password = password;
                    Map<String ,Object> map=new HashMap<>();
                    map.put("username",phone);
                    map.put("password",password);
                    map.put("client","6");
                    presenter.presenter(map,"/api/login/register?","POST", DataUtile.getMyToken()+"");
                }
            }
        });


    }
    //数据返回
    @Override
    public void View(String o) {
        LogUtils.e("数据返回： "+o);
        Gson gson=new Gson();
        RegistBean bean = gson.fromJson(o, RegistBean.class);
        if (bean.getCode()==200){
            Toast.makeText(RegistActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            UserBean userBean = new UserBean();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("Phone", userBean.phone);
            resultIntent.putExtra("password", userBean.password);
            setResult(186, resultIntent);
            finish();
        }else {
            Toast.makeText(RegistActivity.this,bean.getMsg()+"",Toast.LENGTH_SHORT).show();
        }
    }
}