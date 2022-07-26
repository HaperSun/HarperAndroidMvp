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
        return (int) (dp * AppUtil.getCtx().getResources().getDisplayMetrics().density);
    }

    /**
     * dp转px
     *
     * @param dp dp
     * @return int
     */
    public static int dp2px(int dp) {
        return (int) (dp * AppUtil.getCtx().getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * dp转px
     *
     * @param dip double
     * @return int
     */
    public static int dip2px(double dip) {
        return (int) (dip * AppUtil.getCtx().getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * px转dp
     *
     * @param px int
     * @return int
     */
    public static int px2dp(int px) {
        return (int) (px / AppUtil.getCtx().getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 获得屏幕宽度
     *
     * @return int
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) AppUtil.getCtx().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 判断是否平板设备
     *
     * @return true:平板,false:手机
     */
    public static boolean isTabletDevice() {
        return (AppUtil.getCtx().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}