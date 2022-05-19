package com.sun.base.base.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.sun.base.base.iview.IBaseFragment;
import com.sun.base.base.iview.IBaseView;
import com.sun.base.base.activity.BaseActivity;
import com.sun.base.base.widget.LoadingDialog;
import com.sun.base.dialog.BaseDialogFragment;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2022/5/18
 * @note: fragment 基类
 */
public abstract class BaseFragment extends BaseDialogFragment implements IBaseView, IBaseFragment {

    protected final String TAG = getClass().getSimpleName();
    protected BaseActivity mActivity;
    // 一次性对象容器
    private CompositeDisposable mCompositeDisposable;
    protected LoadingDialog mLoadingDialog;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * 获取当前Fragment状态
     *
     * @return true为正常 false为未加载或正在删除
     */
    private boolean getStatus() {
        return (isAdded() && !isRemoving());
    }

    @Override
    public void showToast(int resId) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showToast(resId);
            }
        }
    }

    @Override
    public void showToast(String msg) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showToast(msg);
            }
        }
    }

    @Override
    public void showLongToast(String msg) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showLongToast(msg);
            }
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param resId
     */
    protected void showToastSuccess(int resId) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showToastSuccess(resId);
            }
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param msg
     */
    protected void showToastSuccess(String msg) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showToastSuccess(msg);
            }
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param msg
     */
    protected void showLongToastSuccess(String msg) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showLongToastSuccess(msg);
            }
        }
    }

    /**
     * 获取Activity
     *
     * @return
     */
    public BaseActivity getBaseActivity() {
        if (mActivity == null) {
            mActivity = (BaseActivity) getActivity();
        }
        return mActivity;
    }

    @Override
    public void close() {
    }

    @Subscribe
    public void eventBusDefault(Object o) {
        // do nothing
    }

    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void showLoadingDialog(CharSequence title) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog.Builder(getActivity())
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

    @Override
    public void onDestroyView() {
        // 放弃异步请求，可根据需求修改代码执行位置
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (enableEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }
}
