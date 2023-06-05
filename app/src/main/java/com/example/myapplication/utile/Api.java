package com.example.myapplication.utile;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @Author：lenovo
 * @E-mail： 1003195060@163.com
 * @Date：2019/5/10 19:28
 * @Description：拼接接口
 */
public interface Api {

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> Post(@Url String url, @FieldMap Map<String, String> map);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST
    Observable<ResponseBody> Post1(@Url String url, @Body RequestBody route, @Header("token") String token);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST
    Observable<ResponseBody> Post1(@Url String url, @Body RequestBody route);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @GET
    Observable<ResponseBody> GET(@Url String url, @QueryMap Map<String ,Object> route, @Header("token") String token);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @GET
    Observable<ResponseBody> GET1(@Url String url, @Header("token") String token);
}
