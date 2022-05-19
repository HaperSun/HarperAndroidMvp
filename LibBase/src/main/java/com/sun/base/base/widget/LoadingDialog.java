package com.sun.base.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.sun.base.R;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 加载框
 */
public class LoadingDialog extends Dialog {

    private ImageView mIvLoading;
    private TextView mTvTitle;

    private boolean isCanceledOnTouchOutside = false;
    private boolean isCancelable = false;
    private CharSequence title;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.LoadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading_dialog);
        mIvLoading = findViewById(R.id.iv_loading);
        mTvTitle = findViewById(R.id.tv_title);
        setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        setCancelable(isCancelable);
//        showLoadingGif();
        startAnimation();
        mTvTitle.setText(title);
        //背景不要变暗
        getWindow().setDimAmount(0);
    }

    /**
     * 显示加载动画gif
     */
    private void showLoadingGif() {
//        ImgLoader.INSTANCE.loadGifImage("file:///android_asset/global_loading_gif.gif", mIvLoading);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        this.isCanceledOnTouchOutside = cancel;
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        this.isCancelable = flag;
    }

    @Override
    public void setTitle(@StringRes int title) {
        setTitle(getContext().getString(title));
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        if (isShowing()) {
            if (TextUtils.isEmpty(title)){
                mTvTitle.setVisibility(View.GONE);
            }else {
                mTvTitle.setVisibility(View.VISIBLE);
                mTvTitle.setText(title);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        if (TextUtils.isEmpty(title)){
            mTvTitle.setVisibility(View.GONE);
        }else {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
        startAnimation();
    }

    /**
     * 开启动画
     */
    private void startAnimation() {
        mIvLoading.measure(0, 0);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        mIvLoading.startAnimation(rotateAnimation);
    }

    public static class Builder {

        private final LoadingDialog mLoadingDialog;

        public Builder(Context context) {
            mLoadingDialog = new LoadingDialog(context);
        }

        public Builder setCanceledOnTouchOutside(boolean cancel) {
            mLoadingDialog.setCanceledOnTouchOutside(cancel);
            return this;
        }

        public Builder setCancelable(boolean flag) {
            mLoadingDialog.setCancelable(flag);
            return this;
        }

        public Builder setTitle(@StringRes int title) {
            mLoadingDialog.setTitle(title);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            mLoadingDialog.setTitle(title);
            return this;
        }

        public LoadingDialog build() {
            return mLoadingDialog;
        }
    }
}
