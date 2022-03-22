package com.sun.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sun.base.R;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 左边是返回icon，中间标题，右侧无内容
 */
public class LeftIconHeader extends BaseHeader {

    private TextView mTvTitle;

    public LeftIconHeader(Context context) {
        super(context);
    }

    public LeftIconHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftIconHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int concreteLayout() {
        return R.layout.layout_left_icon_header;
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        setBackgroundColor(Color.WHITE);
        mTvTitle = findViewById(R.id.tv_title);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseHeader);
            //读取xml中标记的标题文本
            final CharSequence title = ta.getText(R.styleable.BaseHeader_centerTitle);
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
            }
            ta.recycle();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }
}
