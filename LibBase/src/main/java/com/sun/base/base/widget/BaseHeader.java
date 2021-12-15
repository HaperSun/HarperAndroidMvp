package com.sun.base.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.sun.base.R;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note: 头部基类
 */
public abstract class BaseHeader extends RelativeLayout {

    protected View mLeftTitle;
    protected View mRightTitle;

    public BaseHeader(Context context) {
        this(context, null);
    }

    public BaseHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(concreteLayout(), this);
        setBackgroundResource(R.color.white);

        mLeftTitle = findViewById(R.id.v_left);
        mRightTitle = findViewById(R.id.v_right);

        init(context, attrs);

        if (mLeftTitle != null) {
            mLeftTitle.setOnClickListener(view -> {
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onLeftClick(view);
                }
            });
        }

        if (mRightTitle != null) {
            mRightTitle.setOnClickListener(view -> {
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onRightClick(view);
                }
            });
        }
    }

    /**
     * 设置标题
     *
     * 连接Activity.setTitle() 方法，默认空实现，必要时有其子类实现
     *
     * @param title 标题
     */
    public void setTitle(CharSequence title) {}

    protected abstract int concreteLayout();

    protected abstract void init(Context context, AttributeSet attrs);

    private OnTitleClickListener mOnTitleClickListener;

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        mOnTitleClickListener = onTitleClickListener;
    }

    public OnTitleClickListener getOnTitleClickListener() {
        return mOnTitleClickListener;
    }

    public interface OnTitleClickListener {
        void onLeftClick(View view);

        void onRightClick(View view);
    }

}
