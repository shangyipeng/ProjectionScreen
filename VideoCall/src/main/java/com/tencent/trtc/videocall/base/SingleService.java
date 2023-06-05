package com.tencent.trtc.videocall.base;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.tencent.trtc.videocall.ForegroundServiceUtils;


public abstract class SingleService extends BaseSingleService{
    public static String appoint;
    public Context context;

    public abstract void create();

    public abstract void execute(Intent intent);

    public abstract void start(Intent intent);

    @Override
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

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        try {
            ForegroundServiceUtils.start(this.context, this);
            start(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e2) {
            e2.printStackTrace();
        }
        return super.onStartCommand(intent, i, i2);
    }

    public SingleService(String str) {
        super(str);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        try {
            execute(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ForegroundServiceUtils.stop(this);
    }

    protected abstract WindowManager getWindowManager();
}
