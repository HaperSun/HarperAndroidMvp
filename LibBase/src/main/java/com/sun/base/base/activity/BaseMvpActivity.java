package com.sun.base.base.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.sun.base.base.iview.IAddPresenterView;
import com.sun.base.presenter.BasePresenter;
import com.sun.base.util.CommonUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 基于MVP模式对BaseActivity进行封装
 */
public abstract class BaseMvpActivity extends BaseActivity implements IAddPresenterView {

    private Set<BasePresenter> mPresenters;
    public ViewDataBinding mViewDataBinding;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让屏幕保持不暗不关闭
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        beforeSetContentView(savedInstanceState);
        //android6.0以后可以对状态栏文字颜色和图标进行修改
        changeStatusBarTxtAndImgColor();
        mViewDataBinding = DataBindingUtil.setContentView(this, layoutId());
        initIntent();
        initView();
        initData();
        //设置不可以多点点击
        initMultiClick();
    }

    public void initIntent() {

    }

    private void changeStatusBarTxtAndImgColor() {
        if (!statusBarTxtIsDark() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 我们在theme中配置statusBar颜色后，如果配置的颜色黑暗的则在子类中复写该方法，并返回true
     *
     * @return boolean
     */
    public boolean statusBarTxtIsDark() {
        return false;
    }

    private void initMultiClick() {
        if (!setMotionEventSplittingEnabled()) {
            CommonUtils.setMotionEventSplittingEnabled(findViewById(android.R.id.content), false);
        }
    }

    /**
     * 获取ViewDataBinding
     *
     * @return null 或者binding对象
     */
    public ViewDataBinding getDataBinding() {
        return mViewDataBinding;
    }


    @Override
    protected void onDestroy() {
        if (mPresenters != null) {
            for (BasePresenter presenter : mPresenters) {
                presenter.clearView();
            }
            mPresenters = null;
        }
        super.onDestroy();
    }


    /**
     * 是否可以多点点击 子类可以复写该方法 默认不可多点点击
     *
     * @return boolean
     */
    protected boolean setMotionEventSplittingEnabled() {
        return false;
    }
}
