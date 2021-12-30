package com.sun.base.bean;

import android.net.ConnectivityManager;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 网络状态改变监听事件
 */
public class NetworkChangeState {

    /**
     * 当前是否连接
     */
    private final boolean isConnected;
    /**
     * 当前网络类型 {@link ConnectivityManager}
     */
    private final int type;

    public NetworkChangeState(boolean isConnected, int type) {
        this.isConnected = isConnected;
        this.type = type;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getType() {
        return type;
    }
}
