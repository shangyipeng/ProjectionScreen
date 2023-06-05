package com.example.myapplication.utile;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SPHelper {

    // 加密数据的key
    private static final String KEY = "BEN";
    // 默认xml文件名
    private static final String PREFS_NAME = "prefs_name";

    /**
     * 加密
     *
     * @param value
     * @return
     */
    private static String encoderStr(String value) {
        String md5Key = EncryptUtil.md5Digest(KEY);
        return EncryptUtil.desEncoder(value, md5Key);
    }

    /**
     * 解密
     *
     * @param value
     * @return
     */
    private static String decoderStr(String value) {
        String md5Key = EncryptUtil.md5Digest(KEY);
        return EncryptUtil.desDecoder(value, md5Key);
    }

    /**
     * 保存数据
     *
     * @param c
     * @param prefsName
     * @param key
     * @param value
     */
    public static void setString(Context c, String prefsName, String key, String value) {
        SharedPreferences sp = c.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, encoderStr(value));
        editor.commit();
    }

    /**
     * 保存数据
     *
     * @param c
     * @param key
     * @param value
     */
    public static void setString(Context c, String key, String value) {
        setString(c, PREFS_NAME, key, value);
    }

    /**
     * 读取数据
     *
     * @param c
     * @param prefsName
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context c, String prefsName, String key, String defValue) {
        SharedPreferences sp = c.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return defValue.equals(value) ? value : decoderStr(value);
    }

    /**
     * 读取数据
     *
     * @param c
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context c, String key, String defValue) {
        return getString(c, PREFS_NAME, key, defValue);
    }

    /**
     * 读取数据(默认值是"")
     *
     * @param c
     * @param key 读取的名称
     * @return 读取的值
     */
    public static String getString(Context c, String key) {
        return getString(c, PREFS_NAME, key, "");
    }

    /**
     * 保存int类型数据
     *
     * @param c
     * @param prefsName
     * @param key
     * @param value
     */
    public static void setInt(Context c, String prefsName, String key, int value) {
        setString(c, prefsName, key, String.valueOf(value));
    }

    /**
     * 保存int类型数据
     *
     * @param c
     * @param key
     * @param value
     */
    public static void setInt(Context c, String key, int value) {
        setInt(c, PREFS_NAME, key, value);
    }

    /**
     * 读取int类型数据
     *
     * @param c
     * @param prefsName
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context c, String prefsName, String key, int defValue) {
        return Integer.valueOf(getString(c, prefsName, key, String.valueOf(defValue)));
    }

    /**
     * 读取int类型数据
     *
     * @param c
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context c, String key, int defValue) {
        return getInt(c, PREFS_NAME, key, defValue);
    }

    /**
     * 保存boolean值
     *
     * @param c
     * @param prefsName
     * @param key
     * @param value
     */
    public static void setBoolean(Context c, String prefsName, String key, boolean value) {
        setString(c, prefsName, key, String.valueOf(value));
    }

    /**
     * 保存boolean值
     *
     * @param c
     * @param key
     * @param value
     */
    public static void setBoolean(Context c, String key, boolean value) {
        setBoolean(c, PREFS_NAME, key, value);
    }

    /**
     * 读取boolean值
     *
     * @param c
     * @param prefsName
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context c, String prefsName, String key, boolean defValue) {
        return Boolean.valueOf(getString(c, prefsName, key, String.valueOf(defValue)));
    }

    /**
     * 读取boolean值
     *
     * @param c
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context c, String key, boolean defValue) {
        return getBoolean(c, PREFS_NAME, key, defValue);
    }

    /**
     * 保存double类型数据
     *
     * @param c
     * @param prefsName
     * @param key
     * @param value
     */
    public static void setDouble(Context c, String prefsName, String key, double value) {
        setString(c, prefsName, key, String.valueOf(value));
    }

    /**
     * 保存double类型数据
     *
     * @param c
     * @param key
     * @param value
     */
    public static void setDouble(Context c, String key, double value) {
        setDouble(c, PREFS_NAME, key, value);
    }

    /**
     * 读取double类型数据
     *
     * @param c
     * @param prefsName
     * @param key
     * @param defValue
     * @return
     */
    public static double getDouble(Context c, String prefsName, String key, double defValue) {
        return Double.valueOf(getString(c, prefsName, key, String.valueOf(defValue)));
    }

    /**
     * 读取double类型数据
     *
     * @param c
     * @param key
     * @param defValue
     * @return
     */
    public static double getDouble(Context c, String key, double defValue) {
        return Double.valueOf(getString(c, PREFS_NAME, key, String.valueOf(defValue)));
    }


    private final static String PREFERENCES_NAME = "LOCATION_INFO";


    public static void saveObject(Context context, String key, Object obj) {

        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;

        try {
            SharedPreferences.Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();

            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);

            //将对象序列化写入byte缓存
            os.writeObject(obj);

            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());

            //保存该16进制数组
            sharedata.putString(key, bytesToHexString);
            sharedata.commit();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "保存失败");
        } finally {
            try {
                os.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object getObject(Context context, String key) {

        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;

        try {
            SharedPreferences sharedata = context.getSharedPreferences(PREFERENCES_NAME, 0);
            if (sharedata.contains(key)) {
                String string = sharedata.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    bis = new ByteArrayInputStream(stringToBytes);
                    is = new ObjectInputStream(bis);

                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                is.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //所有异常返回null
        return null;

    }


    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); //两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   // 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; // A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48);//  0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; // A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }
}
