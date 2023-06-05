package com.example.myapplication.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 登录结果返回bean
 */
public class LogeBean {
    @SerializedName("code")
    public Integer code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public DataDTO data;
    public static class DataDTO {
        @SerializedName("id")
        public Integer id;
        @SerializedName("isBindMobile")
        public Boolean isBindMobile;
        @SerializedName("token")
        public String token;

    }
}
