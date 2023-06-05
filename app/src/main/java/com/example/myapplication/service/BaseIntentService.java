package com.example.myapplication.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.myapplication.utile.ForegroundServiceUtils;

public abstract class BaseIntentService extends IntentService {
    public Context context;

    public BaseIntentService(String str) {
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
        return super.onStartCommand(intent,i,i2);
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
