package com.sun.base.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.githang.statusbar.StatusBarCompat;
import com.sun.base.R;
import com.sun.base.presenter.BasePresenter;
import com.sun.base.ui.IAddPresenterView;
import com.sun.base.ui.widget.BaseHeader;
import com.sun.base.util.CommonUtils;
import com.sun.base.util.StatusBarUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */

public abstract class BaseMvpActivity extends BaseActivity implements IAddPresenterView {

    protected BaseHeader mHeader;
    protected @ColorInt
    //状态栏背景色
    int mStatusBarColor;
    private Set<BasePresenter> mPresenters;
    //默认状态栏背景色
    private int mDefaultStatusBarColor;
    private ViewDataBinding binding;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        beforeSetContentView(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, layoutId());
        if (needSetStatusBar()) {
            initStatusBarColor();
            StatusBarCompat.setStatusBarColor(this, mStatusBarColor);
        }
        initView();
        initData();
        initEvent();
        if (!setMotionEventSplittingEnabled()) {
            //设置不可以多点点击
            CommonUtils.setMotionEventSplittingEnabled(findViewById(android.R.id.content), false);
        }
    }

    /**
     * 获取ViewDataBinding
     *
     * @return null 或者binding对象
     */
    public ViewDataBinding getDataBinding() {
        return binding;
    }

    public void initStatusBarColor() {
        boolean isLightStatusBarSupported = StatusBarUtil.isLightStatusBarSupported();
        if (isLightStatusBarSupported) {
            mDefaultStatusBarColor = getResources().getColor(R.color.white);
            mStatusBarColor = setStatusBarColorIfSupport();
        } else {
            mStatusBarColor = getResources().getColor(R.color.color_app_status_bar);
        }
    }

    @Override
    public void initEvent() {
        if (mHeader != null && mHeader.getOnTitleClickListener() == null) {
            mHeader.setOnTitleClickListener(new BaseHeader.OnTitleClickListener() {
                @Override
                public void onLeftClick(View view) {
                    finish();
                }

                @Override
                public void onRightClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mHeader != null) {
            mHeader.setTitle(title);
        } else {
            super.setTitle(title);
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenters != null) {
            for (BasePresenter presenter : mPresenters) {
                presenter.detachView();
            }
            mPresenters = null;
        }
        super.onDestroy();
    }

    /**
     * 子类如果需要布局延伸到状态栏，请复写改方法返回false
     *
     * @return
     */
    protected boolean needSetStatusBar() {
        //状态栏不在屏幕顶部
        if (StatusBarUtil.isStatusBarPositionNotOnTop()) {
            return false;
        }
        return true;
    }

    /**
     * 子类需要修改状态栏背景色的话，请复写改方法返回对应的颜色
     *
     * @return
     */
    public @ColorInt
    int setStatusBarColorIfSupport() {
        return mDefaultStatusBarColor;
    }

    public int getStatusBarColor() {
        return mStatusBarColor;
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
