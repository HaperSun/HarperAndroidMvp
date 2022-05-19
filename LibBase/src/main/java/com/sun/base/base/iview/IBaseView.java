package com.sun.base.base.iview;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 公共View接口
 * IBaseView中封装了常用的View操作,如Toast,进度条等等,并通过BaseActivity实现,
 * 所以建议所有Activity都继承自BaseActivity以便更好的使用本框架
 */
public interface IBaseView {

    /**
     * 返回上下问题
     *
     * @return 上下文
     */
    Context getContext();

    /**
     * 添加依附视图的一次性访问对象（方便销毁网络请求等异步操作）
     *
     * @param disposable 一次性访问对象
     */
    void addDisposable(Disposable disposable);

    /**
     * 根据资源文件id弹出toast
     *
     * @param resId 资源文件id
     */
    void showToast(int resId);

    /**
     * 根据字符串弹出toast
     *
     * @param msg 提示内容
     */
    void showToast(String msg);

    /**
     * 根据字符串弹出toast
     *
     * @param msg 提示内容
     */
    void showLongToast(String msg);

    /**
     * 结束当前页面
     */
    void close();

}