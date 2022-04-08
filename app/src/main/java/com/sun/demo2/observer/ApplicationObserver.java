package com.sun.demo2.observer;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.sun.common.toast.ToastHelper;

/**
 * @author Harper
 * @date 2022/3/26
 * note:
 */
public class ApplicationObserver implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(){
        //只要有一个activity是活跃的，就会被调用，且只会调用一次
        ToastHelper.showCommonToast("ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){
//        ToastHelper.showCommonToast("ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){
//        ToastHelper.showCommonToast("ON_RESUME");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){
//        ToastHelper.showCommonToast("ON_PAUSE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        //永远不会调用
        ToastHelper.showCommonToast("ON_DESTROY");
    }
}
