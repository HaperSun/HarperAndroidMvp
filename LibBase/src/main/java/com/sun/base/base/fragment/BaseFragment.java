package com.sun.base.base.fragment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.sun.base.base.iview.IBaseFragment;
import com.sun.base.base.iview.IBaseView;
import com.sun.base.base.widget.LoadingDialog;
import com.sun.base.dialog.BaseDialogFragment;
import com.sun.base.toast.CustomToast;
import com.sun.base.toast.ToastHelper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2022/5/18
 * @note: fragment 基类
 */
public abstract class BaseFragment extends BaseDialogFragment implements IBaseView, IBaseFragment {

    /**
     * 一次性对象容器
     */
    protected FragmentActivity mActivity;
    private CompositeDisposable mCompositeDisposable;
    private LoadingDialog mLoadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    /**
     * 获取当前Fragment状态
     *
     * @return true为正常 false为未加载或正在删除
     */
    private boolean getStatus() {
        return (isAdded() && !isRemoving());
    }

    protected void showToast(int resId) {
        if (getStatus()) {
            ToastHelper.showToast(getString(resId));
        }
    }

    @Override
    public void showToast(String s) {
        if (getStatus()) {
            ToastHelper.showToast(s);
        }
    }

    protected void showLongToast(String s) {
        if (getStatus()) {
            ToastHelper.showToast(s, Toast.LENGTH_LONG);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param resId 字符串id
     */
    protected void showSuccessToast(int resId) {
        if (getStatus()) {
            ToastHelper.showCustomToast(resId, CustomToast.CORRECT);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param s 字符串
     */
    protected void showSuccessToast(String s) {
        if (getStatus()) {
            ToastHelper.showCustomToast(s, CustomToast.CORRECT);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param s 字符串
     */
    protected void showLongSuccessToast(String s) {
        if (getStatus()) {
            ToastHelper.showCustomToast(s, CustomToast.CORRECT, Toast.LENGTH_LONG);
        }
    }

    /**
     * 显示Toast，感叹号类型
     *
     * @param resId 字符串id
     */
    protected void showFailToast(int resId) {
        if (getStatus()) {
            ToastHelper.showCustomToast(resId, CustomToast.WARNING);
        }
    }

    /**
     * 显示Toast，感叹号类型
     *
     * @param s 字符串
     */
    protected void showFailToast(String s) {
        if (getStatus()) {
            ToastHelper.showCustomToast(s, CustomToast.WARNING);
        }
    }

    /**
     * 显示Toast，感叹号类型
     *
     * @param s 字符串
     */
    protected void showLongFailToast(String s) {
        if (getStatus()) {
            ToastHelper.showCustomToast(s, CustomToast.WARNING, Toast.LENGTH_LONG);
        }
    }

    protected void close() {
        mActivity.finish();
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showLoadingDialog(CharSequence title) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog.Builder(getActivity())
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .build();
        }
        mLoadingDialog.setTitle(title);
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    protected void showLoadingDialog(@StringRes int titleResId) {
        showLoadingDialog(getString(titleResId));
    }

    @Override
    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
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
}
