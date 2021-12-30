package com.sun.base.base.iview;

import android.os.Bundle;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */

public interface IBaseActivity {

    void beforeSetContentView(Bundle savedInstanceState);

    int layoutId();

    void initView();

    void initData();
}
