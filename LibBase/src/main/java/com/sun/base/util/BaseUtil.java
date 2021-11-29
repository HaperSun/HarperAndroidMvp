package com.sun.base.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.sun.base.BuildConfig;
import com.sun.base.R;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public abstract class BaseUtil {

    /**
     * 获取服务端地址
     *
     * @return
     */
    public static String getServerUrl() {
        return BuildConfig.DEBUG ? BuildConfig.TEST_URL : BuildConfig.RELEASE_URL;
    }


    /**
     * 是否是测试环境
     *
     * @return
     */
    public static boolean isTestApi() {
        return BuildConfig.DEBUG;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        return context.getResources().getString(R.string.version_name);
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
