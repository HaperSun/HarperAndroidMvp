package com.sun.base.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.sun.base.base.iview.IBaseActivity;
import com.sun.base.base.iview.IBaseView;
import com.sun.base.base.widget.LoadingDialog;
import com.sun.base.util.CommonUtils;
import com.sun.common.toast.CustomToast;
import com.sun.common.toast.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2021/11/16
 * @note: activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseView, IBaseActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected FragmentManager mFragmentManager;
    //如果点击空白处不需要立即隐藏键盘，则给该变量赋值false
    public boolean needClickHideSoftInput = true;
    //一次性对象容器
    private CompositeDisposable mCompositeDisposable;
    protected LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        if (enableEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 是否支持EventBus
     *
     * @return 支持EventBus
     */
    protected boolean enableEventBus() {
        return false;
    }

    /**
     * 点击空白  隐藏输入法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                //判断是否快速点击，避免重复点击
                if (CommonUtils.isFastDoubleClick()) {
                    return true;
                }
                View view = getCurrentFocus();
                if (isHideInput(view, ev)) {
                    hideSoftInput(view.getWindowToken());
                }
            }
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判定是否需要隐藏
     *
     * @param v
     * @param ev
     * @return
     */
    private boolean isHideInput(View v, MotionEvent ev) {
        // 如果不需要 则立即返回
        if (!needClickHideSoftInput) {
            return needClickHideSoftInput;
        }
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }

    /**
     * 隐藏软键盘
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void showToast(int resId) {
        if (!isFinishing()) {
            ToastHelper.showCommonToast(getString(resId));
        }
    }

    @Override
    public void showToast(String msg) {
        if (!isFinishing()) {
            ToastHelper.showCommonToast(msg);
        }
    }

    @Override
    public void showLongToast(String msg) {
        if (!isFinishing()) {
            ToastHelper.showCommonToast(msg, Toast.LENGTH_LONG);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param resId
     */
    public void showToastSuccess(int resId) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(resId, CustomToast.CORRECT);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param msg
     */
    public void showToastSuccess(String msg) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(msg, CustomToast.CORRECT);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param msg
     */
    public void showLongToastSuccess(String msg) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(msg, CustomToast.CORRECT, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void close() {
        finish();
    }


    @Override
    public void beforeSetContentView(Bundle savedInstanceState) {
        // 暂不做任何事，重写是因为不是每个界面都需要在设置布局之前有操作
    }

    @Subscribe
    public void eventBusDefault(Object o) {
        // do nothing
    }

    public <T extends View> T $(@IdRes int id) {
        return findViewById(id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //注释掉，解决内存不足回来一些奇怪问题
        if (!disableOnSaveInstanceState()) {
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * 默认禁止调用onSaveInstanceState，解决内存不足回来一些奇怪问题，子类可以复写该方法自行处理
     *
     * @return
     */
    protected boolean disableOnSaveInstanceState() {
        return true;
    }

    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void showLoadingDialog(CharSequence title) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog.Builder(this)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .build();
        }
        mLoadingDialog.setTitle(title);
        mLoadingDialog.show();
    }

    protected void showLoadingDialog(@StringRes int titleResId) {
        showLoadingDialog(getString(titleResId));
    }

    protected BaseActivity mThis() {
        return BaseActivity.this;
    }

    @Override
    protected void onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        if (enableEventBus()) {
            EventBus.getDefault().register(this);
        }
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }
}
