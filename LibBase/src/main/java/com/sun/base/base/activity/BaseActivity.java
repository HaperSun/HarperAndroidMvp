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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.sun.base.base.iview.IBaseActivity;
import com.sun.base.base.iview.IBaseView;
import com.sun.base.base.widget.LoadingDialog;
import com.sun.base.util.CommonUtil;
import com.sun.base.toast.CustomToast;
import com.sun.base.toast.ToastHelper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2021/11/16
 * @note: activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseView, IBaseActivity {

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
    }

    /**
     * 点击空白  隐藏输入法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                //判断是否快速点击，避免重复点击
                if (CommonUtil.isFastDoubleClick()) {
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
            ToastHelper.showToast(getString(resId));
        }
    }

    @Override
    public void showToast(String s) {
        if (!isFinishing()) {
            ToastHelper.showToast(s);
        }
    }

    @Override
    public void showLongToast(String s) {
        if (!isFinishing()) {
            ToastHelper.showToast(s, Toast.LENGTH_LONG);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param resId
     */
    public void showSuccessToast(int resId) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(resId, CustomToast.CORRECT);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param s
     */
    public void showSuccessToast(String s) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(s, CustomToast.CORRECT);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param s
     */
    public void showLongSuccessToast(String s) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(s, CustomToast.CORRECT, Toast.LENGTH_LONG);
        }
    }

    /**
     * 显示Toast，感叹号类型
     *
     * @param resId
     */
    public void showFailToast(int resId) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(resId, CustomToast.WARNING);
        }
    }

    /**
     * 显示Toast，感叹号类型
     *
     * @param s
     */
    public void showFailToast(String s) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(s, CustomToast.WARNING);
        }
    }

    /**
     * 显示Toast，感叹号类型
     *
     * @param s
     */
    public void showLongFailToast(String s) {
        if (!isFinishing()) {
            ToastHelper.showCustomToast(s, CustomToast.WARNING, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void close() {
        ActivityCompat.finishAfterTransition(this);
    }


    @Override
    public void beforeSetContentView(Bundle savedInstanceState) {
        //暂不做任何事，重写是因为不是每个界面都需要在设置布局之前有操作
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

    @Override
    protected void onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }
}
