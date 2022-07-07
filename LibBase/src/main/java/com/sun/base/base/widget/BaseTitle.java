package com.sun.base.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sun.base.R;

/**
 * @author Harper
 * @date 2022/7/7
 * note:
 */
public class BaseTitle extends LinearLayout {

    private final Context mContext;
    private OnTitleClickListener mOnTitleClickListener;
    private MarqueeTextView mTvTitle;
    private LinearLayout mLlLeft;

    public BaseTitle(Context context) {
        this(context, null);
    }

    public BaseTitle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_base_title, this);
        mContext = context;
    }

    public void initView(int theme) {
        boolean isWhite = theme == R.color.white;
        setBackgroundColor(ContextCompat.getColor(mContext, theme));
        setOrientation(HORIZONTAL);
        mTvTitle = findViewById(R.id.tv_title);
        ImageView ivBack = findViewById(R.id.iv_back);
        mLlLeft = findViewById(R.id.ll_title);
        mTvTitle.setTextColor(ContextCompat.getColor(mContext, isWhite ? R.color.black : R.color.white));
        ivBack.setImageResource(isWhite ? R.mipmap.ic_back_black : R.mipmap.ic_back_white);
        ivBack.setOnClickListener(v -> {
            if (mOnTitleClickListener != null) {
                mOnTitleClickListener.onLeftClick(v);
            }
        });
    }

    public void setTitle(int resId) {
        setTitle(mContext.getString(resId));
    }

    public void setTitle(String s) {
        if (mTvTitle != null) {
            mTvTitle.setText(s);
        }
    }

    public LinearLayout getTitleLeftContainer() {
        return mLlLeft;
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        mOnTitleClickListener = onTitleClickListener;
    }

    public interface OnTitleClickListener {
        /**
         * 点击标题的返回按钮
         *
         * @param view view
         */
        void onLeftClick(View view);
    }
}
