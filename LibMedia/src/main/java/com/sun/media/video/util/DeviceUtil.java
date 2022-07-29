package com.sun.media.video.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.sun.base.util.AppUtil;

import java.util.List;


/**
 * @author Sigal
 * on 2020/12/12
 * note:
 */
public final class DeviceUtil {

    public static int getDeviceWidth() {
        return AppUtil.getCtx().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDeviceHeight() {
        return AppUtil.getCtx().getResources().getDisplayMetrics().heightPixels;
    }

    @SuppressLint("WrongConstant")
    public static boolean hasAppInstalled(String pkgName) {
        try {
            AppUtil.getCtx().getPackageManager().getPackageInfo(pkgName, PackageManager.PERMISSION_GRANTED);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isAppRunInBackground() {
        ActivityManager activityManager = (ActivityManager) AppUtil.getCtx().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(AppUtil.getPackageName())) {
                // return true -> Run in background
                // return false - > Run in foreground
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    private static String[] huaweiRongyao = {
            "hwH60",    //荣耀6
            "hwPE",     //荣耀6 plus
            "hwH30",    //3c
            "hwHol",    //3c畅玩版
            "hwG750",   //3x
            "hw7D",      //x1
            "hwChe2",      //x1
    };

    public static String getDeviceModel() {
        return Build.DEVICE;
    }

    public static boolean isHuaWeiRongyao() {
        int length = huaweiRongyao.length;
        for (int i = 0; i < length; i++) {
            if (huaweiRongyao[i].equals(getDeviceModel())) {
                return true;
            }
        }
        return false;
    }
}
