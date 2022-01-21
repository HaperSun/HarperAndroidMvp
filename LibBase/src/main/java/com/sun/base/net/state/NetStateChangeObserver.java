package com.sun.base.net.state;

/**
 * @author Harper
 * @date 2022/1/21
 * note:网络状态观察者
 */
public interface NetStateChangeObserver {
    /**
     * 网络断开
     */
    void onNetDisconnected();

    /**
     * 网络连接中
     *
     * @param networkType 网络类型
     */
    void onNetConnected(String networkType);
}
