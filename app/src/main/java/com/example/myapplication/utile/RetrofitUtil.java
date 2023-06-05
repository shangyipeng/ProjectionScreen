package com.example.myapplication.utile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.common.util.ByteConstants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者:今夕何夕
 * 时间:${data}
 * Description:网络请求
 */
public class RetrofitUtil {
    OkHttpClient okHttpClient;
    Retrofit retrofit;
    static RetrofitUtil util;
    //私有构造方法
    private RetrofitUtil(){
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request=chain.request();
                Response response=chain.proceed(request);
                return response;
            }
        });
        /*
         **打印retrofit信息部分
         */
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                LogUtils.d("retrofitBack = "+message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);//日志打印
        builder.cache(new Cache(new File("com.example.cache"),200*ByteConstants.MB));//缓存
        builder.readTimeout(3, TimeUnit.MINUTES).connectTimeout(3, TimeUnit.MINUTES).writeTimeout(3, TimeUnit.MINUTES); //设置超时

        okHttpClient=builder.build();
        retrofit=new Retrofit.Builder()
                .baseUrl(DataUtile.getUrl())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

    }
    public static synchronized RetrofitUtil getUtil(){
        if (util==null){
            util=new RetrofitUtil();
        }
        return util;
    }
    public <T>T gets(Class<T> tClass){
        return retrofit.create(tClass);
    }

    public static boolean isNetWork(Context context){
        if(context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null){
                return info.isConnected();
            }
        }
        return false;
    }

}
