package com.sun.base.base.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sun.base.base.iview.IAddPresenterView;
import com.sun.base.presenter.BasePresenter;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 基础MVP Service
 */
public class BaseMvpService extends Service implements IAddPresenterView {

    private Set<BasePresenter> mPresenters;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void addPresenter(BasePresenter presenter) {
        if (mPresenters == null) {
            mPresenters = new HashSet<>();
        }
        if (!mPresenters.contains(presenter)) {
            mPresenters.add(presenter);
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenters != null) {
            for (BasePresenter presenter : mPresenters) {
                presenter.clearView();
            }
            mPresenters = null;
        }
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addDisposable(Disposable disposable) {

    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showLongToast(String msg) {

    }

    @Override
    public void close() {

    }
}
