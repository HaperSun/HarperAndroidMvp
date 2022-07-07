package com.sun.base.base.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.sun.base.base.iview.IAddPresenterView;
import com.sun.base.presenter.BasePresenter;
import com.sun.base.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Harper
 * @date: 2022/5/18
 * @note: 基于MVP模式对BaseFragment进行封装
 */
public abstract class BaseMvpFragment<VDB extends ViewDataBinding> extends BaseFragment implements IAddPresenterView {

    protected final String TAG = getClass().getSimpleName();
    private Set<BasePresenter> mPresenters;
    protected View mRootView;
    public VDB bind;

    public BaseMvpFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取ViewDataBinding
        bind = DataBindingUtil.inflate(inflater, layoutId(), container, false);
        mRootView = bind.getRoot();
        //处理fragment的Bundle
        initBundle();
        //是否接收EventBus消息
        if (enableEventBus()) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
        //设置不可以多点点击
        if (!enableMultiClick() && mRootView instanceof ViewGroup) {
            //设置不可以多点点击
            CommonUtil.setMotionEventSplittingEnabled((ViewGroup) mRootView, false);
        }
        return mRootView;
    }

    /**
     * 子类每次new一个presenter的时候，请调用此方法
     */
    @Override
    public void addPresenter(BasePresenter presenter) {
        if (mPresenters == null) {
            mPresenters = new HashSet<>();
        }
        if (!mPresenters.contains(presenter)) {
            mPresenters.add(presenter);
        }
    }

    public void initBundle() {

    }

    /**
     * 如果子类需要接收EventBus，返回true即可
     *
     * @return boolean
     */
    protected boolean enableEventBus() {
        return false;
    }

    /**
     * 默认不可多点点击，子类若要支持多点点击，返回true即可
     *
     * @return boolean
     */
    protected boolean enableMultiClick() {
        return false;
    }

    public <T extends View> T $(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    @Subscribe
    public void eventBusDefault(Object object) {
        //为了防止activity注册了eventBus，但没有加有@Subscribe注解的方法导致崩溃
    }

    @Override
    public void onDestroy() {
        if (enableEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (mPresenters != null) {
            for (BasePresenter presenter : mPresenters) {
                presenter.clearView();
            }
            mPresenters = null;
        }
        super.onDestroy();
    }
}
