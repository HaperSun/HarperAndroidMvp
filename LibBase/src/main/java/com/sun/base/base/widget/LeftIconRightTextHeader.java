package com.sun.base.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sun.base.R;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note: 左边icon，右边文字的头部
 */
public class LeftIconRightTextHeader extends BaseHeader {

    private TextView mTvTitle;
    private TextView mTvRight;

    public LeftIconRightTextHeader(Context context) {
        super(context);
    }

    public LeftIconRightTextHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftIconRightTextHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int concreteLayout() {
        return R.layout.layout_left_icon_right_text_header;
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        setBackgroundColor(Color.WHITE);
        mTvTitle = findViewById(R.id.tv_title);
        mTvRight = (TextView) mRightTitle;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LeftIconRightTextHeader);
            //读取xml中标记的标题文本
            final CharSequence title = ta.getText(R.styleable.LeftIconRightTextHeader_centerTitle);
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
            }
            //读取xml中标记的右边文本
            final CharSequence rightText = ta.getText(R.styleable.LeftIconRightTextHeader_rightText);
            if (!TextUtils.isEmpty(rightText)) {
                mTvRight.setText(rightText);
            }
            ta.recycle();
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTvTitle.setText(title);
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public TextView getTvRight() {
        return mTvRight;
    }

    public void setTvRightPadding(int left, int top, int right, int bottom) {
        mTvRight.setPadding(left, top, right, bottom);
    }
}
