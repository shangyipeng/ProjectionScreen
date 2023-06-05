package com.tencent.trtc.videocall.base;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

public abstract class BaseSingleService extends Service {
    private volatile Looper looper;
    private volatile ServiceHandler handler;
    private String tag;
    private boolean mode;

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public abstract void onHandleIntent(@Nullable Intent intent);

    /* access modifiers changed from: private */
    public final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            BaseSingleService.this.onHandleIntent((Intent) message.obj);
            BaseSingleService.this.stopSelf();
        }
    }

    public BaseSingleService(String tag) {
        this.tag = tag;
    }

    public void setIntentRedelivery(boolean mode) {
        this.mode = mode;
    }

    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("IntentService[" + this.tag + "]");
        handlerThread.start();
        this.looper = handlerThread.getLooper();
        this.handler = new ServiceHandler(this.looper);
    }

    public void onStart(@Nullable Intent intent, int i) {
        if (i == 1) {
            Message obtainMessage = this.handler.obtainMessage();
            obtainMessage.arg1 = i;
            obtainMessage.obj = intent;
            this.handler.sendMessage(obtainMessage);
        }
    }

    public int onStartCommand(@Nullable Intent intent, int i, int i2) {
        onStart(intent, i2);
        return this.mode?START_REDELIVER_INTENT:START_NOT_STICKY;
    }

    public void onDestroy() {
        this.looper.quit();
    }
}
