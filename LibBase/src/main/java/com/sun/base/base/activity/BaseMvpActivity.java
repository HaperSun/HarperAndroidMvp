package com.sun.base.base.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.githang.statusbar.StatusBarCompat;
import com.sun.base.R;
import com.sun.base.base.iview.IAddPresenterView;
import com.sun.base.databinding.ActivityBaseBinding;
import com.sun.base.presenter.BasePresenter;
import com.sun.base.status.StatusBarUtil;
import com.sun.base.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 基于MVP模式对BaseActivity进行封装
 */
public abstract class BaseMvpActivity<VDB extends ViewDataBinding> extends BaseActivity implements IAddPresenterView {

    protected final String TAG = this.getClass().getSimpleName();
    private Set<BasePresenter> mPresenters;
    protected ActivityBaseBinding baseBind;
    protected VDB vdb;
    protected int mStatusBarColor;
    protected int mTitleColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (enableKeepScreenBright()) {
            //让屏幕保持不暗不关闭
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        beforeSetContentView(savedInstanceState);
        initBinding();
        //android6.0以后可以对状态栏文字颜色和图标进行修改
        initStatusBarColor();
        //处理activity的Intent
        initIntent();
        //是否接收EventBus消息
        if (enableEventBus()) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
        //设置不可以多点点击
        if (!enableMultiClick()) {
            CommonUtil.setMotionEventSplittingEnabled(findViewById(android.R.id.content), false);
        }
    }

    private void initBinding() {
        //获取ViewDataBinding
        baseBind = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false);
        vdb = DataBindingUtil.inflate(LayoutInflater.from(this), layoutId(), null, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        vdb.getRoot().setLayoutParams(params);
        FrameLayout container = (FrameLayout) baseBind.getRoot().findViewById(R.id.container);
        container.addView(vdb.getRoot());
        getWindow().setContentView(baseBind.getRoot());
    }

    private void initStatusBarColor() {
        if (!enableDarkStatusBarAndSetTitle()) {
            if (StatusBarUtil.isSupportLightStatusBar()) {
                //将StatusBar的文字和图片设置成深色的
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //设置了这个属性，状态栏的图标以深色绘制
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        } else {
            try {
                if (mStatusBarColor != 0) {
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, mStatusBarColor));
                }
            } catch (Exception e) {
                showToast("mStatusBarColor赋值异常~");
            }
        }
        initTitleTheme();
    }

    private void initTitleTheme() {
        //<注意：></>此处如果要使用基类封装的title，就给mTitleColor赋值，否则不会显示基类的title
        if (mTitleColor == 0) {
            baseBind.title.setVisibility(View.GONE);
        } else {
            baseBind.title.setVisibility(View.VISIBLE);
            baseBind.title.initView(mTitleColor);
        }
    }

    /**
     * 子类每次new一个presenter的时候，请调用此方法
     *
     * @param presenter presenter
     */
    @Override
    public void addPresenter(BasePresenter presenter) {
        if (mPresenters == null) {
            mPresenters = new HashSet<>();
        }
        mPresenters.add(presenter);
    }

    protected void initIntent() {

    }

    /**
     * 当前页面是否开启屏幕常亮，默认开启
     *
     * @return boolean
     */
    protected boolean enableKeepScreenBright() {
        return true;
    }

    /**
     * 当前应用theme的style默认配置：StatusBar是白色背景，文字和图片默认是白色的
     * 如果需要StatusBar是深色的，则需要给当前activity配置一个深色的theme,且重写该方法返回true
     * <p>
     * <注意：></>此处如果要使用基类封装的title，就给mTitleColor赋值，否则不会显示基类的title
     *
     * @return boolean
     */
    protected boolean enableDarkStatusBarAndSetTitle() {
        return false;
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

    @Subscribe
    public void eventBusDefault(Object object) {
        //为了防止activity注册了eventBus，但没有加有@Subscribe注解的方法导致崩溃
    }

    @Override
    protected void onDestroy() {
        if (enableKeepScreenBright()) {
            //让屏幕保持不暗不关闭
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (enableEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (mPresenters != null) {
            for (BasePresenter presenter : mPresenters) {
                presenter.clearView();
            }
            mPresenters = null;
        }
        if (vdb != null) {
            vdb.unbind();
        }
        super.onDestroy();
    }
}
