package com.sun.base.base.iview;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public interface IBaseFragment {

    /**
     * 布局id
     *
     * @return 布局id
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
