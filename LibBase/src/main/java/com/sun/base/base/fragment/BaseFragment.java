package com.sun.base.base.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.sun.base.base.activity.BaseActivity;
import com.sun.base.base.iview.IBaseFragment;
import com.sun.base.base.iview.IBaseView;
import com.sun.base.base.widget.LoadingDialog;
import com.sun.base.dialog.BaseDialogFragment;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author: Harper
 * @date: 2022/5/18
 * @note: fragment 基类
 */
public abstract class BaseFragment extends BaseDialogFragment implements IBaseView, IBaseFragment {

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
        mActivity = getBaseActivity();
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
            mActivity.showToast(resId);
        }
    }

    @Override
    public void showToast(String s) {
        if (getStatus()) {
            mActivity.showToast(s);
        }
    }

    @Override
    public void showLongToast(String s) {
        if (getStatus()) {
            mActivity.showLongToast(s);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param resId 字符串id
     */
    protected void showToastSuccess(int resId) {
        if (getStatus()) {
            mActivity.showToastSuccess(resId);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param s 字符串
     */
    protected void showToastSuccess(String s) {
        if (getStatus()) {
            mActivity.showToastSuccess(s);
        }
    }

    /**
     * 显示Toast，对勾类型
     *
     * @param s 字符串
     */
    protected void showLongToastSuccess(String s) {
        if (getStatus()) {
            mActivity.showLongToastSuccess(s);
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
        mActivity.close();
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
