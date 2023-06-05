package com.example.myapplication.utile;

import android.os.Environment;

public class AppConfig {
    public static int BD_TRACE_SERVICE_ID =236981 ;//服务轨迹id236976
    public static int BD_STAY_TIME = 60;//停留时间
    public static int BD_STAY_RADIUS = 20;//停留半径
    public static final String SOCKET_HOST = "wss://www.yousin.cn/websocket/";
    public static AppConfig INSTANCE;
    public static final int SOCKET_PORT = 443;
    public static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/ysxsoft/tjj";
    public static final String RECORD_AUDIO_PATH = BASE_PATH + "/record_audio/";
    public static final String PHOTO_PATH = BASE_PATH + "/image/";
    public static synchronized AppConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppConfig();
        }
        return INSTANCE;
    }
}
