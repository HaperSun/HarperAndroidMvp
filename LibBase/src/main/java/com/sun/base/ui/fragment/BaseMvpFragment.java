package com.sun.base.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;


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
    protected View mRootView;
    public ViewDataBinding mViewDataBinding;

    public BaseMvpFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getBaseActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
        mRootView = mViewDataBinding.getRoot();
        initView();
        initData();
        //设置不可以多点点击
        initMultiClick();
        return mRootView;
    }

    protected void initMultiClick(){
        if (!setMotionEventSplittingEnabled() && mRootView instanceof ViewGroup) {
            //设置不可以多点点击
            CommonUtils.setMotionEventSplittingEnabled((ViewGroup) mRootView, false);
        }
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
