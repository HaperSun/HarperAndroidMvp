package com.sun.base.base.iview;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 公共View接口
 * IBaseView中封装了常用的View操作,如Toast,进度条等等
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
     * 根据字符串弹出toast
     *
     * @param s 提示内容
     */
    void showToast(String s);

    /**
     * 展示加载框
     *
     * @param c c
     */
    void showLoadingDialog(CharSequence c);

    /**
     * 隐藏加载框
     */
    void dismissLoadingDialog();
}