package com.sun.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.sun.base.R;
import com.sun.base.util.CommonUtils;


/**
 * @author: Harper
 * @date:   2021/12/14
 * @note:
 */
public class CommonAlertDialog extends Dialog {

    private CharSequence mTitle;
    private CharSequence mMessage;
    private CharSequence mNegativeText;
    private CharSequence mPositiveText;

    private View.OnClickListener mNegativeClickListener;
    private View.OnClickListener mPositiveClickListener;
    //NegativeBtn是否可见，默认可见
    private boolean mIsShowNegativeBtn = true;
    private ImageView mIvWave;
    private boolean mIsShowWave = false;
    //是否可以多点点击
    private boolean mSetMotionEventSplittingEnabled;
    //弹窗时背景是否要变暗
    private boolean mIsDim = true;

    public CommonAlertDialog(@NonNull Context context) {
        super(context, R.style.AlertDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common_alert);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvMessage = findViewById(R.id.tv_message);
        TextView btnNegative = findViewById(R.id.btn_negative);
        TextView btnPositive = findViewById(R.id.btn_positive);
        //添加底部波浪
        mIvWave = findViewById(R.id.iv_wave);
        mIvWave.setVisibility(mIsShowWave ? View.VISIBLE : View.GONE);
        tvTitle.setVisibility(TextUtils.isEmpty(mTitle) ? View.GONE : View.VISIBLE);
        tvTitle.setText(mTitle);
        tvMessage.setVisibility(TextUtils.isEmpty(mMessage) ? View.GONE : View.VISIBLE);
        tvMessage.setText(mMessage);
        btnNegative.setText(mNegativeText);
        btnPositive.setText(mPositiveText);
        btnNegative.setVisibility(mIsShowNegativeBtn ? View.VISIBLE : View.GONE);
        btnNegative.setOnClickListener(view -> {
            if (mNegativeClickListener != null) {
                mNegativeClickListener.onClick(view);
            }
            dismiss();
        });
        btnPositive.setOnClickListener(view -> {
            if (mPositiveClickListener != null) {
                mPositiveClickListener.onClick(view);
            }
            dismiss();
        });
        if (!mSetMotionEventSplittingEnabled) {
            //设置不可以多点点击
            CommonUtils.setMotionEventSplittingEnabled(findViewById(android.R.id.content), false);
        }
        if (!mIsDim) {
            //背景不要变暗
            getWindow().setDimAmount(0);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
    }

    public void setNegativeText(CharSequence negativeText) {
        mNegativeText = negativeText;
    }

    public void setPositiveText(CharSequence positiveText) {
        mPositiveText = positiveText;
    }

    public void setNegativeClickListener(View.OnClickListener negativeClickListener) {
        mNegativeClickListener = negativeClickListener;
    }

    public void setPositiveClickListener(View.OnClickListener positiveClickListener) {
        mPositiveClickListener = positiveClickListener;
    }

    public void setShowNegativeBtn(boolean showNegativeBtn) {
        mIsShowNegativeBtn = showNegativeBtn;
    }

    public void showWave(boolean isShowWave) {
        mIsShowWave = isShowWave;
    }

    public void setSetMotionEventSplittingEnabled(boolean setMotionEventSplittingEnabled) {
        mSetMotionEventSplittingEnabled = setMotionEventSplittingEnabled;
    }

    public void setDim(boolean dim) {
        mIsDim = dim;
    }

    public static class Builder {

        private final CommonAlertDialog mCommonAlertDialog;

        public Builder(Context context) {
            mCommonAlertDialog = new CommonAlertDialog(context);
        }

        public Builder setTitle(CharSequence title) {
            mCommonAlertDialog.setTitle(title);
            return this;
        }

        public Builder setTitle(@StringRes int resId) {
            return setTitle(mCommonAlertDialog.getContext().getString(resId));
        }

        public Builder setMessage(CharSequence message) {
            mCommonAlertDialog.setMessage(message);
            return this;
        }

        public Builder setMessage(@StringRes int resId) {
            return setMessage(mCommonAlertDialog.getContext().getString(resId));
        }

        public Builder setNegativeText(CharSequence negativeText) {
            mCommonAlertDialog.setNegativeText(negativeText);
            return this;
        }

        public Builder setNegativeText(@StringRes int resId) {
            return setNegativeText(mCommonAlertDialog.getContext().getString(resId));
        }

        public Builder setNegativeText(CharSequence negativeText,
                                       View.OnClickListener negativeClickListener) {
            mCommonAlertDialog.setNegativeText(negativeText);
            mCommonAlertDialog.setNegativeClickListener(negativeClickListener);
            return this;
        }

        public Builder setNegativeText(@StringRes int resId,
                                       View.OnClickListener negativeClickListener) {
            return setNegativeText(mCommonAlertDialog.getContext().getString(resId), negativeClickListener);
        }

        public Builder setPositiveText(CharSequence positiveText) {
            mCommonAlertDialog.setPositiveText(positiveText);
            return this;
        }

        public Builder setPositiveText(@StringRes int resId) {
            return setPositiveText(mCommonAlertDialog.getContext().getString(resId));
        }

        public Builder setPositiveText(CharSequence positiveText,
                                       View.OnClickListener positiveClickListener) {
            mCommonAlertDialog.setPositiveText(positiveText);
            mCommonAlertDialog.setPositiveClickListener(positiveClickListener);
            return this;
        }

        public Builder setPositiveText(@StringRes int resId,
                                       View.OnClickListener positiveClickListener) {
            return setPositiveText(mCommonAlertDialog.getContext().getString(resId), positiveClickListener);
        }

        public Builder setCancelable(boolean cancelable) {
            mCommonAlertDialog.setCancelable(cancelable);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancelableOnTouchOutside) {
            mCommonAlertDialog.setCanceledOnTouchOutside(cancelableOnTouchOutside);
            return this;
        }

        public Builder setShowNegativeBtn(boolean showNegativeBtn) {
            mCommonAlertDialog.setShowNegativeBtn(showNegativeBtn);
            return this;
        }

        public Builder showWave(boolean isShowWave) {
            mCommonAlertDialog.showWave(isShowWave);
            return this;
        }

        public Builder setSetMotionEventSplittingEnabled(boolean setMotionEventSplittingEnabled) {
            mCommonAlertDialog.setSetMotionEventSplittingEnabled(setMotionEventSplittingEnabled);
            return this;
        }

        public Builder setDim(boolean dim) {
            mCommonAlertDialog.setDim(dim);
            return this;
        }

        public CommonAlertDialog build() {
            return mCommonAlertDialog;
        }
    }
}
