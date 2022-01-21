package com.sun.common.util;

import android.content.Context;
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
     * @param context context
     * @param dp      dp
     * @return int
     */
    public static int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dp      dp
     * @return int
     */
    public static int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 获得屏幕宽度
     *
     * @param context context
     * @return int
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}