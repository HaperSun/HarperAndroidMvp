package com.sun.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.sun.base.R;
import com.sun.common.bean.MagicInt;

/**
 * @author: Harper
 * @date: 2021/11/12
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
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onHeaderClick(MagicInt.ZERO, view);
                }
            });
        }
        if (mRightTitle != null) {
            mRightTitle.setOnClickListener(view -> {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onHeaderClick(MagicInt.ONE, view);
                }
            });
        }
    }

    /**
     * 设置标题
     * <p>
     * 连接Activity.setTitle() 方法，默认空实现，必要时有其子类实现
     *
     * @param title 标题
     */
    public void setTitle(CharSequence title) {
    }

    protected abstract int concreteLayout();

    protected abstract void init(Context context, AttributeSet attrs);

    private OnHeaderClickListener mOnHeaderClickListener;

    public void setHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        mOnHeaderClickListener = onHeaderClickListener;
    }

    public OnHeaderClickListener getHeaderClickListener() {
        return mOnHeaderClickListener;
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(int type, View view);
    }

}
