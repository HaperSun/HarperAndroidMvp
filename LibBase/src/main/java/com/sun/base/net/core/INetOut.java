package com.sun.base.net.core;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 接口返回结果回调
 */
public interface INetOut<T> {

    void onSuccess(T result);

    void onFailure(int status, String info);
}
