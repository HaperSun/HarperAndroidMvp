package com.sun.base.base.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 懒加载的Fragment
 */
public abstract class LazyBaseMvpFragment extends BaseMvpFragment {

    /**
     * 检测声明周期中，是否已经构建视图
     */
    private boolean mViewCreated = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mViewCreated = true;
        if (mUserVisible) {
            realLoad();
        }
        return view;
    }

    private boolean mUserVisible = false;

    @Override
    public final void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mUserVisible = isVisibleToUser;
        if (mUserVisible && mViewCreated) {
            realLoad();
        }
    }

    /**
     * 判断是否已经加载
     */
    private boolean mLoaded = false;

    /**
     * 控制只允许加载一次
     */
    private void realLoad() {
        if (mLoaded) {
            return;
        }
        mLoaded = true;
        onRealViewLoaded();
    }

    @Override
    public void onDestroyView() {
        mViewCreated = false;
        mLoaded = false;
        super.onDestroyView();
    }

    @Override
    public void initView() {

    }

    /**
     * 当视图真正加载时调用
     */
    protected abstract void onRealViewLoaded();

}
