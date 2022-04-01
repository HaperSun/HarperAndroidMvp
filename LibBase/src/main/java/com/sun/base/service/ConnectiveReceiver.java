package com.sun.base.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sun.base.bean.NetworkChangeState;
import com.sun.base.util.LogHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 网络状态改变监听广播接收器
 */
public class ConnectiveReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectiveReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogHelper.d(TAG, "onReceive action=" + action);
        NetworkInfo network = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        boolean isConnected = network.isConnected();
        int type = network.getType();
        LogHelper.d(TAG, "onReceive network isConnected=" + isConnected + ",type=" + type);
        EventBus.getDefault().post(new NetworkChangeState(isConnected, type));
    }
}
