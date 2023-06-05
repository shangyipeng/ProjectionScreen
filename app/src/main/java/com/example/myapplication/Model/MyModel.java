package com.example.myapplication.Model;

import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.ApplicTion;
import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.activity.LogeActivity;
import com.example.myapplication.bean.BaseBean;
import com.example.myapplication.bean.LogeBean;
import com.example.myapplication.utile.Api;
import com.example.myapplication.utile.DataUtile;
import com.example.myapplication.utile.RetrofitUtil;
import com.example.myapplication.utile.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者:今夕何夕
 * 时间:${data}
 * Description:这个是注释
 */
public class MyModel {
    Api api = RetrofitUtil.getUtil().gets(Api.class);
    public void Model(Map<String, Object> map,String Url, String Type,final MyCallBreak callBreak,String token) {
        if (Type.equals("GET")){
            if (map!=null){
                api.GET(Url,map,token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ResponseBody>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.e(e.toString());
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    String json = responseBody.string();
                                    callBreak.sressco(json);
                                } catch (Exception e) {
                                    LogUtils.e("打印错误信息 "+e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
            }else {
                api.GET1(Url,token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ResponseBody>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.e(e.toString());
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    String json = responseBody.string();
                                    callBreak.sressco(json);
                                } catch (Exception e) {
                                    LogUtils.e("打印错误信息 "+e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
            }

        }else {
            RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
            if (!token.equals("")){
                api.Post1(Url,requestBody,token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ResponseBody>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.e(e.toString());
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    String json = responseBody.string();
                                    callBreak.sressco(json);
                                } catch (Exception e) {
                                    LogUtils.e("打印错误信息 "+e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
            }else {
                api.Post1(Url,requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ResponseBody>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.e(e.toString());
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    String json = responseBody.string();
                                    callBreak.sressco(json);
                                } catch (Exception e) {
                                    LogUtils.e("打印错误信息 "+e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }
    }
    //设置接口
    public interface MyCallBreak {
        void sressco(Object o);
    }

}
