package com.tencent.trtc.videocall.base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.tencent.trtc.videocall.ForegroundServiceUtils;


public abstract class BaseIntentStackService extends IntentService{
    public Context context;

    public BaseIntentStackService(String str) {
        super(str);
    }

    public void onCreate() {
        this.context = this;
        try {
            ForegroundServiceUtils.start(this.context, this);
            create();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e2) {
            e2.printStackTrace();
        }
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        try {
            ForegroundServiceUtils.start(this.context, this);
            start(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e2) {
            e2.printStackTrace();
        }
        return START_STICKY;
    }

    public void onHandleIntent(Intent intent) {
        try {
            execute(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e2) {
            e2.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        ForegroundServiceUtils.stop(this);
    }

    public abstract void create();
    public abstract void execute(Intent intent);
    public abstract void start(Intent intent);
}
