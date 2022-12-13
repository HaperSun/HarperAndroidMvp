package com.sun.base.base.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sun.base.base.iview.IBaseView;
import com.sun.base.base.iview.IPresenterView;
import com.sun.base.presenter.BasePresenter;
import com.sun.base.toast.ToastHelper;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 基础MVP Service
 */
public class BaseMvpService extends Service implements IPresenterView {

    private Set<BasePresenter<? extends IBaseView>> mPresenters;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void addPresenter(BasePresenter<? extends IBaseView> presenter) {
        if (mPresenters == null) {
            mPresenters = new HashSet<>();
        }
        mPresenters.add(presenter);
    }

    @Override
    public void onDestroy() {
        if (mPresenters != null) {
            for (BasePresenter<? extends IBaseView> presenter : mPresenters) {
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
    public void showToast(String s) {
        ToastHelper.showToast(s);
    }

    @Override
    public void showLoadingDialog(CharSequence title) {

    }

    @Override
    public void dismissLoadingDialog() {

    }
}
