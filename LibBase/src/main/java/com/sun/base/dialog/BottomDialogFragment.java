package com.sun.base.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sun.base.R;
import com.sun.base.util.CommonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/14
 * @note: 底部弹窗组件
 */
public class BottomDialogFragment extends DialogFragment {

    private static final String EXTRA_BUILDER = "builder";
    private Context mContext;
    private Window mWindow;
    //自定义的布局ID
    private int mLayoutResId;
    //自定义的View
    private View mCustomView;
    //配合下面选项一起使用
    private CharSequence mDialogTitle;
    //选项
    private List<DialogItem> mDialogItems;
    //是否可以多点点击
    private boolean mSetMotionEventSplittingEnabled;
    //是否需要背景变暗
    private boolean mBackgroundDimEnabled = true;

    public static BottomDialogFragment newInstance(Builder builder) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_BUILDER, builder);
        BottomDialogFragment fragment = new BottomDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            Builder builder = (Builder) args.getSerializable(EXTRA_BUILDER);
            this.mLayoutResId = builder.mLayoutResId;
            this.mCustomView = builder.mCustomView;
            this.mDialogTitle = builder.mDialogTitle;
            this.mDialogItems = builder.mDialogItems;
            this.mSetMotionEventSplittingEnabled = builder.mSetMotionEventSplittingEnabled;
            this.mBackgroundDimEnabled = builder.mBackgroundDimEnabled;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.transparentFrameWindowStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        mContext = getContext();
        boolean isCustom = false;
        if (mLayoutResId > 0) {
            isCustom = true;
            dialog.setContentView(mLayoutResId);
        } else if (mCustomView != null) {
            isCustom = true;
            dialog.setContentView(mCustomView);
        } else {
            dialog.setContentView(R.layout.fragment_bottom_dialog);
        }

        Window window = dialog.getWindow();
        if (!mBackgroundDimEnabled) {
            window.setDimAmount(0);
        }
        // 设置显示动画
        window.setWindowAnimations(R.style.bottom_dialog_style);
        window.setGravity(Gravity.BOTTOM);
        //设置横向全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow = window;
        if (!isCustom) {
            initUI();
        }
        if (!mSetMotionEventSplittingEnabled) {
            //设置不可以多点点击
            CommonUtil.setMotionEventSplittingEnabled($(android.R.id.content), false);
        }
        return dialog;
    }

    private <T extends View> T $(int id) {
        return mWindow.findViewById(id);
    }

    private void initUI() {
        LinearLayout llContainer = $(R.id.ll_container);
        llContainer.removeAllViews();
        if (!TextUtils.isEmpty(mDialogTitle)) {
            View titleView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_dialog_fragment_title,
                    llContainer, false);
            TextView tvTitle = titleView.findViewById(R.id.tv_title);
            tvTitle.setText(mDialogTitle);
            llContainer.addView(titleView);
        }
        for (final DialogItem dialogItem : mDialogItems) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_dialog_item,
                    llContainer, false);
            TextView tvItem = itemView.findViewById(R.id.tv_item);
            tvItem.setText(dialogItem.getTitle());
            tvItem.setTextColor(dialogItem.getTitleColor());
            tvItem.setOnClickListener(view -> {
                dismiss();
                View.OnClickListener clickListener = dialogItem.getClickListener();
                if (clickListener != null) {
                    clickListener.onClick(view);
                }
            });
            llContainer.addView(itemView);
        }
        $(R.id.btn_cancel).setOnClickListener(view -> dismiss());
    }

    private DialogInterface.OnDismissListener mOnDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    /**
     * 弹窗每项数据结构
     */
    public static class DialogItem {

        //选项名称
        private CharSequence mTitle;
        //选项颜色值
        @ColorInt
        private int mTitleColor;
        //选项点击事件回调
        private View.OnClickListener mClickListener;

        public DialogItem(CharSequence title, View.OnClickListener clickListener) {
            this(title, Color.parseColor("#1b1b1b"), clickListener);
        }

        public DialogItem(CharSequence title, @ColorInt int titleColor, View.OnClickListener clickListener) {
            mTitle = title;
            mTitleColor = titleColor;
            mClickListener = clickListener;
        }

        public CharSequence getTitle() {
            return mTitle;
        }

        public void setTitle(CharSequence title) {
            mTitle = title;
        }

        public int getTitleColor() {
            return mTitleColor;
        }

        public void setTitleColor(@ColorInt int titleColor) {
            mTitleColor = titleColor;
        }

        public View.OnClickListener getClickListener() {
            return mClickListener;
        }

        public void setClickListener(View.OnClickListener clickListener) {
            mClickListener = clickListener;
        }
    }

    public static class Builder implements Serializable {

        //自定义的布局ID
        private int mLayoutResId;
        //自定义的View
        private View mCustomView;
        //配合下面选项一起使用
        private CharSequence mDialogTitle;
        //选项
        private List<DialogItem> mDialogItems;
        //是否可以多点点击
        private boolean mSetMotionEventSplittingEnabled;
        //是否需要背景变暗
        private boolean mBackgroundDimEnabled = true;

        public Builder setLayoutResId(@LayoutRes int layoutResId) {
            this.mLayoutResId = layoutResId;
            return this;
        }

        public Builder setView(View view) {
            this.mCustomView = view;
            return this;
        }

        public Builder setDialogTitle(CharSequence dialogTitle) {
            this.mDialogTitle = dialogTitle;
            return this;
        }

        public Builder addDialogItem(DialogItem dialogItem) {
            if (this.mDialogItems == null) {
                this.mDialogItems = new ArrayList<>();
            }
            this.mDialogItems.add(dialogItem);
            return this;
        }

        public Builder setDialogItems(List<DialogItem> dialogItems) {
            this.mDialogItems = dialogItems;
            return this;
        }

        public Builder setSetMotionEventSplittingEnabled(boolean setMotionEventSplittingEnabled) {
            this.mSetMotionEventSplittingEnabled = setMotionEventSplittingEnabled;
            return this;
        }

        public Builder setBackgroundDimEnabled(boolean backgroundDimEnabled) {
            this.mBackgroundDimEnabled = backgroundDimEnabled;
            return this;
        }

        public BottomDialogFragment build() {
            return BottomDialogFragment.newInstance(this);
        }
    }

}
