package com.sun.base.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;


import com.sun.base.presenter.BasePresenter;
import com.sun.base.ui.IAddPresenterView;
import com.sun.base.util.CommonUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 新建Presenter 需调用{@link #addPresenter(BasePresenter)} <br/>
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 */
public abstract class BaseMvpFragment extends BaseFragment implements IAddPresenterView {

    private Set<BasePresenter> mPresenters;

    /**
     * 子类每次new一个presenter的时候，请调用此方法
     *
     * @param presenter
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

    protected View mRootView;

    public BaseMvpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getBaseActivity();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(mActivity).inflate(layoutId(), container, false);
        initView();
        initEvent();
        if (!setMotionEventSplittingEnabled() && mRootView instanceof ViewGroup) {
            //设置不可以多点点击
            CommonUtils.setMotionEventSplittingEnabled((ViewGroup) mRootView, false);
        }
        return mRootView;
    }

    @Override
    public void onDestroy() {
        if (mPresenters != null) {
            for (BasePresenter presenter : mPresenters) {
                presenter.detachView();
            }
            mPresenters = null;
        }
        super.onDestroy();
    }

    public <T extends View> T $(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    /**
     * 是否可以多点点击 子类可以复写该方法 默认不可多点点击
     *
     * @return
     */
    protected boolean setMotionEventSplittingEnabled() {
        return false;
    }
}
