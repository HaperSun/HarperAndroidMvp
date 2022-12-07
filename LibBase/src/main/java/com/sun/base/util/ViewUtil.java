package com.sun.base.util;

import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: View 的工具类
 */
public class ViewUtil {

    private ViewUtil() {
        throw new RuntimeException("you cannot new ViewUtil!");
    }

    /**
     * 获取 View 的高度
     */
    public static int getHeight(View v) {
        int height = v.getHeight();
        if (height <= 0) {
            v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            height = v.getMeasuredHeight();
        }
        return height;
    }

    /**
     * 获取 View 的宽度
     */
    public static int getWidth(View v) {
        int width = v.getWidth();
        if (width <= 0) {
            v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            width = v.getMeasuredWidth();
        }
        return width;
    }

    /**
     * 获取指定TextView的文本宽度
     *
     * @param textView
     * @return
     */
    public static float getTextWidth(final TextView textView) {
        TextPaint textPaint = textView.getPaint();
        return textPaint.measureText(textView.getText().toString());
    }

    /**
     * 获取 View 在屏幕上的位置
     *
     * @return 一个二维数组，包含 View 左上角的坐标信息，int[0] x 坐标，int[1] y 坐标
     */
    public static int[] getLocationOnScreen(View v) {
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        return loc;
    }

    /**
     * 设置View的paddingLeft
     *
     * @param view        view
     * @param paddingLeft paddingLeft
     */
    public static void setPaddingLeft(View view, int paddingLeft) {
        view.setPadding(paddingLeft, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * 设置View的paddingTop
     *
     * @param view       view
     * @param paddingTop paddingTop
     */
    public static void setPaddingTop(View view, int paddingTop) {
        view.setPadding(view.getPaddingLeft(), paddingTop, view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * 设置View的paddingRight
     *
     * @param view         view
     * @param paddingRight paddingRight
     */
    public static void setPaddingRight(View view, int paddingRight) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), paddingRight, view.getPaddingBottom());
    }

    /**
     * 设置View的paddingBottom
     *
     * @param view          view
     * @param paddingBottom paddingBottom
     */
    public static void setPaddingBottom(View view, int paddingBottom) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), paddingBottom);
    }
}
