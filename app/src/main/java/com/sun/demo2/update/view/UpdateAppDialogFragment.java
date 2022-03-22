package com.sun.demo2.update.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sun.base.dialog.BaseDialogFragment;
import com.sun.base.util.PermissionUtil;
import com.sun.common.bean.MagicInt;
import com.sun.common.toast.ToastHelper;
import com.sun.demo2.R;
import com.sun.demo2.update.UpdateService;

import java.util.Objects;


/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 有更新的弹框提示 需要用户点击下载 带有更新进度条
 */
public class UpdateAppDialogFragment extends BaseDialogFragment implements UpdateService.OnDownloadListener, View.OnClickListener {

    private static final String EXTRA_VERSION_NAME = "version_name";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_FORCE_UPDATE = "force_update";

    private static final int TYPE_INSTALL = 1;
    private static final int TYPE_UPDATE = 2;

    private String mVersionName;
    private String mMessage;
    private int mType;
    private boolean isForceUpdate;

    private ProgressBar mProgressBar;
    private TextView mTvUpdateInstall;
    private View mTvCancel;

    /**
     * @param versionName   版本名称
     * @param message       更新信息
     * @param isDownloaded
     * @param isForceUpdate 是否强制更新
     */
    public static UpdateAppDialogFragment newInstance(String versionName, String message, boolean isDownloaded,
                                                      boolean isForceUpdate) {
        Bundle args = new Bundle();
        args.putString(EXTRA_VERSION_NAME, versionName);
        args.putString(EXTRA_MESSAGE, message);
        args.putInt(EXTRA_TYPE, isDownloaded ? TYPE_INSTALL : TYPE_UPDATE);
        args.putBoolean(EXTRA_FORCE_UPDATE, isForceUpdate);
        UpdateAppDialogFragment fragment = new UpdateAppDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(EXTRA_TYPE);
            isForceUpdate = arguments.getBoolean(EXTRA_FORCE_UPDATE);
            mMessage = arguments.getString(EXTRA_MESSAGE);
            mVersionName = arguments.getString(EXTRA_VERSION_NAME);
        }
        setCancelable(false);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.dp280),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return inflater.inflate(R.layout.fragment_update_hint_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvUpdateInstall = view.findViewById(R.id.tv_update_now);
        mTvCancel = view.findViewById(R.id.tv_cancel);
        View space = view.findViewById(R.id.space);
        mTvCancel.setVisibility(View.GONE);
        checkIsForce(false);
        TextView tvNewVersionName = view.findViewById(R.id.tv_new_version_name);
        tvNewVersionName.setText(mVersionName);
        mProgressBar = view.findViewById(R.id.progress_bar);
        TextView tvMsg = view.findViewById(R.id.tv_update_info);
        tvMsg.setText(mMessage);
        switch (mType) {
            case TYPE_INSTALL:
                mTvUpdateInstall.setText("立即安装");
                mTvCancel.setVisibility(View.GONE);
                if (isForceUpdate) {
                    mTvCancel.setVisibility(View.GONE);
                    space.setVisibility(View.GONE);
                } else {
                    mTvCancel.setVisibility(View.VISIBLE);
                    space.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_UPDATE:
                mTvUpdateInstall.setText("立即更新");
                if (isForceUpdate) {
                    mTvCancel.setVisibility(View.GONE);
                    space.setVisibility(View.GONE);
                } else {
                    mTvCancel.setVisibility(View.VISIBLE);
                    space.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        mTvUpdateInstall.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (PermissionUtil.checkStorage()){
            doClick(v);
        }else {
            PermissionUtil.requestStorage(getActivity(),state -> {
                if (state == MagicInt.ONE){
                    doClick(v);
                }
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void doClick(View v){
        switch (v.getId()) {
            case R.id.tv_update_now:
                clickUpdate();
                break;
            case R.id.tv_cancel:
                clickCancel(v);
                break;
            default:
                break;
        }
    }

    private void clickUpdate() {
        if (mType == TYPE_INSTALL) {
            // 点击立即安装 不需要隐藏弹框
            // dismissAllowingStateLoss();
            UpdateService.start(getActivity(), UpdateService.CMD_INSTALL);
        } else if (mType == TYPE_UPDATE) {
            mTvUpdateInstall.setClickable(false);
            mTvUpdateInstall.setText("下载中...");
            mTvCancel.setVisibility(View.VISIBLE);
            checkIsForce(true);
            download();
        }
    }

    private void clickCancel(View v) {
        cancelDownload();
        dismissAllowingStateLoss();
//                checkFinishActivity();
        if (mUpdateAppDialogListener != null) {
            mUpdateAppDialogListener.onCancelDownloadClick(v);
        }
    }

    /**
     * 强制更新或强制安装都隐藏“取消下载”和“下次再说”按钮
     */
    private void checkIsForce(boolean clickUpdate) {
        if (isForceUpdate) {
            mTvCancel.setVisibility(View.GONE);
        }
    }

    private boolean checkFinishActivity() {
        // 如果是强制安装并且不是手动点击安装的状态，则销毁当前界面
        if (mType != TYPE_INSTALL) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
            return true;
        }
        return false;
    }

    private void download() {
        UpdateService.addOnDownloadListener(this);
        UpdateService.start(getActivity(), UpdateService.CMD_START_DOWNLOAD);
    }

    private void cancelDownload() {
        UpdateService.removeOnDownloadListener(this);
        UpdateService.start(getActivity(), UpdateService.CMD_STOP_DOWNLOAD);
    }

    @Override
    public void onDownloadError() {
        UpdateService.removeOnDownloadListener(this);
        mTvUpdateInstall.post(() -> {
            dismissAllowingStateLoss();
            ToastHelper.showCustomToast("更新包下载失败");
        });
        if (mUpdateAppDialogListener != null) {
            //强制升级或强制安装下载出错时，需要关闭当前页面
            mUpdateAppDialogListener.onDownloadError(isForceUpdate);
        }
    }

    @Override
    public void onDownloadProgress(final int progress) {
        mTvUpdateInstall.post(() -> {
            if (mProgressBar.getVisibility() != View.VISIBLE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mProgressBar.setProgress(progress);
        });
    }

    @Override
    public void onDownloadSuccess() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTvUpdateInstall.post(this::dismissAllowingStateLoss);
    }

    private UpdateAppDialogListener mUpdateAppDialogListener;

    public void setUpdateHintDialogListener(UpdateAppDialogListener updateAppDialogListener) {
        mUpdateAppDialogListener = updateAppDialogListener;
    }

    public interface UpdateAppDialogListener {
        /**
         * 取消下载点击回调
         *
         * @param view
         */
        void onCancelDownloadClick(View view);

        /**
         * 下载错误回调
         *
         * @param isNeedFinish 是否需要关闭当前页面
         */
        void onDownloadError(boolean isNeedFinish);
    }
}
