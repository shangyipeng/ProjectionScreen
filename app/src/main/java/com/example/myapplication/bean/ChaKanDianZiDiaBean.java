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
 * 电子围栏数据返回bean
 */
public class ChaKanDianZiDiaBean {

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
    public DataDTO data;

    public static ChaKanDianZiDiaBean objectFromData(String str) {

        return new Gson().fromJson(str, ChaKanDianZiDiaBean.class);
    }

    public static ChaKanDianZiDiaBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), ChaKanDianZiDiaBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ChaKanDianZiDiaBean> arrayChaKanDianZiDiaBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<ChaKanDianZiDiaBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<ChaKanDianZiDiaBean> arrayChaKanDianZiDiaBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<ChaKanDianZiDiaBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class DataDTO {
        /**
         * count
         */
        @SerializedName("count")
        public Integer count;
        /**
         * pageNo
         */
        @SerializedName("pageNo")
        public Integer pageNo;
        /**
         * pageSize
         */
        @SerializedName("pageSize")
        public Integer pageSize;
        /**
         * lists
         */
        @SerializedName("lists")
        public List<ListsDTO> lists;
        /**
         * extendData
         */
        @SerializedName("extendData")
        public ExtendDataDTO extendData;

        public static DataDTO objectFromData(String str) {

            return new Gson().fromJson(str, DataDTO.class);
        }

        public static DataDTO objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataDTO.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataDTO> arrayDataDTOFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataDTO>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataDTO> arrayDataDTOFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataDTO>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public static class ExtendDataDTO {
            public static ExtendDataDTO objectFromData(String str) {

                return new Gson().fromJson(str, ExtendDataDTO.class);
            }

            public static ExtendDataDTO objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), ExtendDataDTO.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<ExtendDataDTO> arrayExtendDataDTOFromData(String str) {

                Type listType = new TypeToken<ArrayList<ExtendDataDTO>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<ExtendDataDTO> arrayExtendDataDTOFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<ExtendDataDTO>>() {
                    }.getType();

                    return new Gson().fromJson(jsonObject.getString(str), listType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new ArrayList();


            }
        }

        public static class ListsDTO {
            /**
             * id
             */
            @SerializedName("id")
            public Integer id;
            /**
             * userId
             */
            @SerializedName("userId")
            public Integer userId;
            /**
             * type
             */
            @SerializedName("type")
            public Integer type;
            /**
             * content
             */
            @SerializedName("content")
            public String content;
            /**
             * createTime
             */
            @SerializedName("createTime")
            public String createTime;

            public static ListsDTO objectFromData(String str) {

                return new Gson().fromJson(str, ListsDTO.class);
            }

            public static ListsDTO objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), ListsDTO.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<ListsDTO> arrayListsDTOFromData(String str) {

                Type listType = new TypeToken<ArrayList<ListsDTO>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<ListsDTO> arrayListsDTOFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<ListsDTO>>() {
                    }.getType();

                    return new Gson().fromJson(jsonObject.getString(str), listType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new ArrayList();


            }
        }
    }
}
