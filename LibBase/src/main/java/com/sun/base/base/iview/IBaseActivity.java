package com.sun.base.base.iview;

import android.os.Bundle;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public interface IBaseActivity {

    /**
     * 在设置布局前执行一些操作
     *
     * @param savedInstanceState savedInstanceState
     */
    void beforeSetContentView(Bundle savedInstanceState);

    /**
     * 返回布局id
     *
     * @return id
     */
    int layoutId();

    /**
     * 初始化view
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();
}
