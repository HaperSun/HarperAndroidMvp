package com.sun.base.net.core;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 网络接口返回结果数据结构接口
 */
public interface INetResult<T> {
    /**
     * 获取状态码
     *
     * @return 状态码
     */
    int getStatus();

    /**
     * 获取提示信息
     *
     * @return 提示信息
     */
    String getInfo();

    /**
     * 是否请求成功
     *
     * @return 是否成功
     */
    boolean isSuccess();

    /**
     * 获取数据
     *
     * @return 请求数据
     */
    T getData();
}
