package com.sun.demo2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sun.base.bean.Parameter;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.LogHelper;

/**
 * @author Harper
 * @date 2022/11/2
 * note:
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String s = intent.getStringExtra(Parameter.TYPE);
            ToastHelper.showCustomToast(s);
        }
        LogHelper.e(TAG, "MyBroadcastReceiver 广播接收者");

//        中断广播
//        abortBroadcast();
    }
}
