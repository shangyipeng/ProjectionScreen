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
 * 电子围栏bean
 */
public class DetailListBean {  /**
 * detailList
 */
@SerializedName("detailList")
public List<DetailListDTO> detailList;
    /**
     * fenceName
     */
    @SerializedName("fenceName")
    public String fenceName;
    @SerializedName("id")
    public int id;
    @SerializedName("userId")
    public int userId;
    @SerializedName("type")
    public int type;
    /**
     * isNotify
     */
    @SerializedName("isNotify")
    public String isNotify;

    public static DetailListBean objectFromData(String str) {

        return new Gson().fromJson(str, DetailListBean.class);
    }

    public static DetailListBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), DetailListBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<DetailListBean> arrayDetailListBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<DetailListBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<DetailListBean> arrayDetailListBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<DetailListBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class DetailListDTO {
        /**
         * latitude
         */
        @SerializedName("latitude")
        public String latitude;
        /**
         * longitude
         */
        @SerializedName("longitude")
        public String longitude;

        public static DetailListDTO objectFromData(String str) {

            return new Gson().fromJson(str, DetailListDTO.class);
        }

        public static DetailListDTO objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DetailListDTO.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DetailListDTO> arrayDetailListDTOFromData(String str) {

            Type listType = new TypeToken<ArrayList<DetailListDTO>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DetailListDTO> arrayDetailListDTOFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DetailListDTO>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }
    }

}
