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
 * 操作记录Bean
 */
public class RecordofoperationsBean {

    @SerializedName("code")
    public Integer code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public DataDTO data;

    public static RecordofoperationsBean objectFromData(String str) {

        return new Gson().fromJson(str, RecordofoperationsBean.class);
    }

    public static RecordofoperationsBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), RecordofoperationsBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<RecordofoperationsBean> arrayRecordofoperationsBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<RecordofoperationsBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<RecordofoperationsBean> arrayRecordofoperationsBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<RecordofoperationsBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class DataDTO {
        @SerializedName("count")
        public Integer count;
        @SerializedName("pageNo")
        public Integer pageNo;
        @SerializedName("pageSize")
        public Integer pageSize;
        @SerializedName("lists")
        public List<ListsDTO> lists;
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
            @SerializedName("id")
            public Integer id;
            @SerializedName("userId")
            public Integer userId;
            @SerializedName("type")
            public Integer type;
            @SerializedName("content")
            public String content;
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
