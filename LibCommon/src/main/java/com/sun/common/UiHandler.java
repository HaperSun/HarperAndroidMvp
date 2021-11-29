package com.sun.common;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * @author Harper
 * @date 2021/11/11
 * note:
 */
public class UiHandler<T> extends Handler {

    protected WeakReference<T> ref;

    public UiHandler(T cla) {
        ref = new WeakReference<>(cla);
    }
    
    public T getRef() {
        return ref != null ? ref.get() : null;
    }
}
