package com.sun.base.net.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harper
 * @date 2022/1/21
 * note:网络状态接收者
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private String mType = NetworkUtil.getNetworkType();

    private static class InstanceHolder {
        private static final NetworkStateChangeReceiver INSTANCE = new NetworkStateChangeReceiver();
    }

    private final List<NetStateChangeObserver> mObservers = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            String networkType = NetworkUtil.getNetworkType();
            notifyObservers(networkType);
        }
    }

    public static void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(InstanceHolder.INSTANCE, intentFilter);
    }

    public static void unRegisterReceiver(Context context) {
        context.unregisterReceiver(InstanceHolder.INSTANCE);
    }

    public static void registerObserver(NetStateChangeObserver observer) {
        if (observer == null) {
            return;
        }
        if (!InstanceHolder.INSTANCE.mObservers.contains(observer)) {
            InstanceHolder.INSTANCE.mObservers.add(observer);
        }
    }

    public static void unRegisterObserver(NetStateChangeObserver observer) {
        if (observer == null) {
            return;
        }
        InstanceHolder.INSTANCE.mObservers.remove(observer);
    }

    private void notifyObservers(String networkType) {
        if (TextUtils.equals(mType, networkType)) {
            return;
        }
        mType = networkType;
        if (TextUtils.equals(NetworkType.NETWORK_NO, networkType)) {
            for (NetStateChangeObserver observer : mObservers) {
                observer.onNetDisconnected();
            }
        } else {
            for (NetStateChangeObserver observer : mObservers) {
                observer.onNetConnected(networkType);
            }
        }
    }
}
