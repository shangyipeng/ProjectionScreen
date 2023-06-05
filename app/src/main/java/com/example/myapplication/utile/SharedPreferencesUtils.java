package com.example.myapplication.utile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.myapplication.ApplicTion;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * SharedPreferences 工具类
 */
public class SharedPreferencesUtils {


    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesUtils(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * save json string of data list to share preference
     *
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist)
            return;
        Gson gson = new Gson();
        //change datalist to json
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * get data List from share preferences
     *
     * @param tag share preferences data tag
     * @param cls target list element object class
     * @return list
     */
    public <T> List<T> getDataList(String tag, Class<T> cls) {
        List<T> datalist = new ArrayList<T>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        try {
            Gson gson = new Gson();
            JsonArray array = new JsonParser().parse(strJson).getAsJsonArray();
            for (JsonElement jsonElement : array) {
                datalist.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            Log.e("--sp保存data--", "Exception : " + e.getMessage());
        }
        return datalist;
    }

    /**
     * save json string of data to share preference
     *
     * @param tag
     * @param data object
     */
    public <T> void setData(String tag, T data) {
        if (null == data)
            return;

        Gson gson = new Gson();
        //change data to json
        String strJson = gson.toJson(data);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * get data from share preferences
     *
     * @param tag share preferences data tag
     * @param cls target object class
     * @return target object or null if error happyed
     */
    public <T> T getData(String tag, Class<T> cls) {
        T data = null;
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return null;
        }
        try {
            Gson gson = new Gson();
            JsonElement jsonElement = new JsonParser().parse(strJson);
            data = gson.fromJson(jsonElement, cls);
        } catch (Exception e) {
            Log.e("--sp获取data--", "Exception : " + e.getMessage());
        }
        return data;
    }
/**
 * 示例
 * private void saveConfig(Configs config) {
 *     DataSave data = new DataSave(this, "ConfigData");
 *     data.setData("config",config);
 * }
 *
 * private Configs loadConfig() {
 *     DataSave data = new DataSave(this, "ConfigData");
 *     return data.getData("config", Configs.class);
 * }
 */
public static String getUid() {
    return getUid(ApplicTion.mContext);
}
    public static String getUid(Context context) {
        String brand = Build.BRAND;
//        if ("OPPO".equals(brand)) {
//            return get(context).getString("uid", "isu3");
//        }
//        if ("Xiaomi".equals(brand)||"xiaomi".equals(brand)) {
//            return get(context).getString("uid", "isu2");
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if("SCM-W09".equals(ApplicationUtils.getBuildModel())){
//                return get(context).getString("uid", "isu1");
//            }
//        }
        return get(context).getString("uid", "");
    }
    public static SharedPreferences get(Context context) {
//		File file= new File("/data/data/"+getPackageName().toString()+"/shared_prefs","Activity.xml");
        SharedPreferences share = context.getSharedPreferences("ysx_ultimate", Context.MODE_PRIVATE);
        return share;
    }
    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context, String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context, String key, Object defaultObject) {
        try {
            String type = defaultObject.getClass().getSimpleName();
            SharedPreferences sp = null;
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            if ("String".equals(type)) {
                return sp.getString(key, (String) defaultObject);
            } else if ("Integer".equals(type)) {
                return sp.getInt(key, (Integer) defaultObject);
            } else if ("Boolean".equals(type)) {
                return sp.getBoolean(key, (Boolean) defaultObject);
            } else if ("Float".equals(type)) {
                return sp.getFloat(key, (Float) defaultObject);
            } else if ("Long".equals(type)) {
                return sp.getLong(key, (Long) defaultObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     *
     * @param context
     */
    public static void clearAll(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void setSharedPreference(String key, String[] values, Context context) {
        String regularEx = "#";
        String str = "";
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (values != null && values.length > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            SharedPreferences.Editor et = sp.edit();
            et.putString(key, str);
            et.commit();
        }
    }

    public static String[] getSharedPreference(String key, Context context) {
        String regularEx = "#";
        String[] str = null;
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, "");
        str = values.split(regularEx);

        return str;
    }
    /**
     * 保存当前电量
     *
     * @param context
     * @param value
     */
    public static void saveBatteryLevel(Context context, String value) {
        setParam(context, "batteryLevel", value);
    }

    public static String getBatteryLevel() {
        return (String) getParam(ApplicTion.mContext,"batteryLevel","");
    }
    /**
     * 上线时间
     *
     * @param context
     * @param value
     */
    public static void saveOnlineTime(Context context, String value) {
        setParam(context, "onlineTime", value);
    }

    public static String getOnlineTime() {
        return (String) getParam(ApplicTion.mContext,"onlineTime", "");
    }

}
