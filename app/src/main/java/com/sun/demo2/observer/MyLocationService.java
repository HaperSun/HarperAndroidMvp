package com.sun.demo2.observer;

import androidx.lifecycle.LifecycleService;

/**
 * @author Harper
 * @date 2022/3/26
 * note:
 */
public class MyLocationService extends LifecycleService {

    public MyLocationService() {
        MyLocationObserver observer = new MyLocationObserver(this);
        getLifecycle().addObserver(observer);
    }
}
