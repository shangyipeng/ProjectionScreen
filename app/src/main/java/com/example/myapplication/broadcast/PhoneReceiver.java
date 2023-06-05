package com.example.myapplication.broadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.service.PhoneService;

/**
 * 电话监听（拨打电话，接听电话）
 */
public class PhoneReceiver extends BroadcastReceiver implements ContractInterface.View {

    private static final String TAG = "message";
    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // 如果是拨打电话
            mIncomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            PhoneService.SetContens(phoneNumber,"拨打");
        } else {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话进来时   响铃!!!!
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    PhoneService.SetContens(mIncomingNumber,"响铃");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //接起电话时   摘机!!!!
                    if (mIncomingFlag) {
                        PhoneService.SetContens(mIncomingNumber,"接听");
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //无任何状态时    空闲状态!!!!
                    PhoneService.SetContens("null","空闲");
                    break;
            }
        }
    }

    @Override
    public void View(String o) {

    }
}
