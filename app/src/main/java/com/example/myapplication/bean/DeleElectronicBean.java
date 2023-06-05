package com.example.myapplication.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 删除电子围栏bean
 */
public class DeleElectronicBean {
    /**
     * code
     */
    @SerializedName("code")
    public Integer code;
    /**
     * msg
     */
    @SerializedName("msg")
    public String msg;
    /**
     * data
     */
    @SerializedName("data")
    public List<?> data;

    public static DeleElectronicBean objectFromData(String str) {

        return new Gson().fromJson(str, DeleElectronicBean.class);
    }

    public static DeleElectronicBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), DeleElectronicBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<DeleElectronicBean> arrayDeleElectronicBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<DeleElectronicBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<DeleElectronicBean> arrayDeleElectronicBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<DeleElectronicBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
