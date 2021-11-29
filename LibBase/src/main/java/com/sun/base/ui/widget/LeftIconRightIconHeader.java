package com.sun.base.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.base.R;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 左边icon，右边icon的头部
 */
public class LeftIconRightIconHeader extends BaseHeader {

    private TextView mTvTitle;
    private ImageView mIvRight;

    public LeftIconRightIconHeader(Context context) {
        super(context);
    }

    public LeftIconRightIconHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftIconRightIconHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int concreteLayout() {
        return R.layout.layout_left_icon_right_icon_header;
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        setBackgroundColor(Color.WHITE);
        mTvTitle = findViewById(R.id.tv_title);
        mIvRight = (ImageView) mRightTitle;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LeftIconRightIconHeader);
            //读取xml中标记的标题文本
            final CharSequence title = ta.getText(R.styleable.LeftIconRightIconHeader_centerTitle);
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
            }
            //读取xml中标记的右边图标
            int rightIcon = ta.getResourceId(R.styleable.LeftIconRightIconHeader_rightIcon, 0);
            if (rightIcon != 0) {
                mIvRight.setImageResource(rightIcon);
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

    public ImageView getIvRight() {
        return mIvRight;
    }
}
