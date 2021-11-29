package com.sun.base.net;


import com.sun.base.bean.StrongReference;
import com.sun.base.net.exception.ApiException;

import io.reactivex.observers.DefaultObserver;

/**
 * @author: Harper
 * @date: 2021/11/16
 * @note:
 */

public abstract class MvpSafetyObserver<T> extends DefaultObserver<T> {

    private StrongReference mView;

    public MvpSafetyObserver(StrongReference view) {
        mView = view;
    }

    @Override
    public void onNext(T t) {
        if (mView.get() == null) {
            return;
        }
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        if (mView.get() == null) {
            return;
        }
        onError((ApiException) e);
    }

    @Override
    public void onComplete() {
        if (mView.get() == null) {
            return;
        }
        onDone();
    }

    protected abstract void onDone();

    protected abstract void onSuccess(T t);

    public abstract void onError(ApiException e);
}
