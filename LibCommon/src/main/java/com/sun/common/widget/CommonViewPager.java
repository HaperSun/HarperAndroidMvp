package com.sun.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;


/**
 * @author: Harper
 * @date: 2022/4/16
 * @note: 处理过滑动的viewpager
 */
public class CommonViewPager extends ViewPager {

    /**
     * mViewTouchMode表示ViewPager是否全权控制滑动事件，默认为false，即不控制
     */
    private boolean mViewTouchMode = false;
    /**
     * 是否支持左右滑动
     */
    private boolean mLeftRightScrollEnable = true;

    public CommonViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
    }

    public void setViewTouchMode(boolean b) {
        if (b && !isFakeDragging()) {
            // 全权控制滑动事件
            beginFakeDrag();
        } else if (!b && isFakeDragging()) {
            // 终止控制滑动事件
            endFakeDrag();
        }
        mViewTouchMode = b;
    }

    /**
     * 在mViewTouchMode为true的时候，ViewPager不拦截点击事件，点击事件将由子View处理
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mLeftRightScrollEnable) {
            return false;
        }
        if (mViewTouchMode) {
            return false;
        }
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mLeftRightScrollEnable) {
            return false;
        }
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在mViewTouchMode为true或者滑动方向不是左右的时候，ViewPager将放弃控制点击事件，
     * 这样做有利于在ViewPager中加入ListView等可以滑动的控件，否则两者之间的滑动将会有冲突
     */
    @Override
    public boolean arrowScroll(int direction) {
        if (mViewTouchMode) {
            return false;
        }
        if (direction != FOCUS_LEFT && direction != FOCUS_RIGHT) {
            return false;
        }
        return super.arrowScroll(direction);
    }

    public boolean isLeftRightScrollEnable() {
        return mLeftRightScrollEnable;
    }

    public void setLeftRightScrollEnable(boolean leftRightScrollEnable) {
        mLeftRightScrollEnable = leftRightScrollEnable;
    }

}
