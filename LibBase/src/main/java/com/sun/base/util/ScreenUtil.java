package com.sun.base.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author Sigal
 * on 2020/8/6
 * note:
 */
public class ScreenUtil {

    /**
     * dp转px
     *
     * @param dp dp
     * @return int
     */
    public static int dp2px(float dp) {
        return (int) (dp * AppUtil.ctx.getResources().getDisplayMetrics().density);
    }

    /**
     * dp转px
     *
     * @param dp dp
     * @return int
     */
    public static int dp2px(int dp) {
        return (int) (dp * AppUtil.ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * dp转px
     *
     * @param dip double
     * @return int
     */
    public static int dip2px(double dip) {
        return (int) (dip * AppUtil.ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * px转dp
     *
     * @param px int
     * @return int
     */
    public static int px2dp(int px) {
        return (int) (px / AppUtil.ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float px2dp(float f) {
        return f / (AppUtil.ctx.getResources().getDisplayMetrics().densityDpi / 160F);
    }

    /**
     * 判断是否平板设备
     *
     * @return true:平板,false:手机
     */
    public static boolean isTabletDevice() {
        return (AppUtil.ctx.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) AppUtil.ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics;
    }

    /**
     * 获得屏幕宽度
     *
     * @return int
     */
    public static int getScreenWidth() {
        //方法1
        DisplayMetrics displayMetrics = AppUtil.ctx.getResources().getDisplayMetrics();
        return displayMetrics == null ? 0 : displayMetrics.widthPixels;
//        //方法2
//        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @return int
     */
    public static int getScreenHeight() {
        //方法1
        DisplayMetrics displayMetrics = AppUtil.ctx.getResources().getDisplayMetrics();
        return displayMetrics == null ? 0 : displayMetrics.heightPixels;
//        //方法2
//        return getDisplayMetrics().heightPixels;
    }
}