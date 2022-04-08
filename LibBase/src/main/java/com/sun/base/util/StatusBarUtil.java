package com.sun.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Properties;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 状态栏工具类
 */
public class StatusBarUtil {

    private StatusBarUtil() {
        throw new RuntimeException("you cannot new StatusBarUtil!");
    }

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * 是否是MIUI设备
     *
     * @return
     */
    public static boolean isMIUI() {
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(KEY_MIUI_VERSION_CODE) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE) != null;
        } catch (final IOException e) {
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore all exception
                }
            }
        }
    }

    /**
     * 是否是Flyme设备
     *
     * @return
     */
    public static boolean isFlyme() {
        return Build.DISPLAY.startsWith("Flyme");
    }

    /**
     * 设备是否支持白底黑字状态栏
     * <p>
     * 状态栏和Toobar白底黑字只能适配到android6.0+以及小米和魅族手机
     *
     * @return
     */
    public static boolean isLightStatusBarSupported() {
        if (Build.MODEL.contains("TB3-850F")) {
            //联想学生机适配下
            return false;
        }
        return isMIUI() || isFlyme() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 系统状态栏是否不在机器顶部
     * 某些设备状态栏在屏幕下方，例如E人E本，这会影响状态栏展示，需要区分
     *
     * @return
     */
    public static boolean isStatusBarPositionNotOnTop() {
        if (Build.MODEL.contains("EBEN")) {
            //E人E本状态栏在屏幕下方
            return true;
        }
        return false;
    }

    /**
     * 设置状态栏渐变颜色
     *
     * @param activity 目标activity
     * @param view     目标View
     */
    public static void setGradientColor(@NonNull Activity activity, View view) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        ViewGroup decorView = (ViewGroup) activityWeakReference.get().getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(android.R.id.custom);
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView);
        }
        setRootView(activityWeakReference.get(), false);
        setTransparentForWindow(activityWeakReference.get());
        setPaddingTop(activityWeakReference.get(), view);
    }

    /**
     * 设置根布局参数
     *
     * @param activity         目标activity
     * @param fitSystemWindows 是否预留toolbar的高度
     */
    private static void setRootView(Activity activity, boolean fitSystemWindows) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        ViewGroup parent = activityWeakReference.get().findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fitSystemWindows);
                ((ViewGroup) childView).setClipToPadding(fitSystemWindows);
            }
        }
    }

    /**
     * 设置透明状态栏
     *
     * @param activity 目标界面
     */
    public static void setTransparentForWindow(@NonNull Activity activity) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        Window window = activityWeakReference.get().getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     *
     * @param context 目标Context
     * @param view    需要增高的View
     */
    public static void setPaddingTop(Context context, @NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0 && view.getPaddingTop() == 0) {
                lp.height += getStatusBarHeight(context);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                        view.getPaddingRight(), view.getPaddingBottom());
            }
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context 目标Context
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param window                window
     * @param statusBarColorIsWhite 状态栏的图标和字体颜色是否是白色
     */
    public static void setImmersiveStatusBar(Window window, boolean statusBarColorIsWhite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0及以上
            View decorView = window.getDecorView();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4到5.0
            WindowManager.LayoutParams localLayoutParams = window.getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        //修改字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //android6.0以后可以对状态栏文字颜色和图标进行修改
            if (statusBarColorIsWhite) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

}
