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
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 *  解决 Can not perform this action  after onSaveInstanceState
 * 参考类 ResolveShowBugDialogFragment
 */
public abstract class BaseFragment extends BaseDialogFragment implements IBaseView, IBaseFragment {


    protected final String TAG = getClass().getSimpleName();

    protected BaseActivity mActivity;

    // 一次性对象容器
    private CompositeDisposable mCompositeDisposable;


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
    public void showProgress(boolean flag, String message) {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.showProgress(flag, message);
            }
        }
    }

    @Override
    public void showProgress(String message) {
        showProgress(true, message);
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void showProgress(boolean flag) {
        showProgress(flag, "正在处理，请稍后...");
    }

    @Override
    public void hideProgress() {
        if (getStatus()) {
            BaseActivity activity = getBaseActivity();
            if (activity != null) {
                activity.hideProgress();
            }
        }
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
    protected void showToastSucType(int resId) {
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
    protected void showToastSucType(String msg) {
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
    protected void showLongToastSucType(String msg) {
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

    protected LoadingDialog mDefaultLoadingDialog;

    protected void dismissDefaultLoadingDialog() {
        if (mDefaultLoadingDialog != null && mDefaultLoadingDialog.isShowing()) {
            mDefaultLoadingDialog.dismiss();
        }
    }

    protected void showDefaultLoadingDialog(CharSequence title) {
        if (mDefaultLoadingDialog == null) {
            mDefaultLoadingDialog = new LoadingDialog.Builder(getActivity())
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .build();
        }
        mDefaultLoadingDialog.setTitle(title);
        mDefaultLoadingDialog.show();
    }

    protected void showDefaultLoadingDialog(@StringRes int titleResId) {
        showDefaultLoadingDialog(getString(titleResId));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 放弃异步请求，可根据需求修改代码执行位置
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
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
