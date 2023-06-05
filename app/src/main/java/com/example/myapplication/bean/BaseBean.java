package com.example.myapplication.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 判断bean是否请求成功
 */
public class BaseBean {

    @SerializedName("code")
    public Integer code;
    @SerializedName("msg")
    public String msg;
}
