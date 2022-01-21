package com.sun.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.sun.common.BuildConfig;
import com.sun.common.bean.AppConfig;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public abstract class AppUtil {

    @SuppressLint("StaticFieldLeak")
    private static AppConfig mAppConfig;

    public static void init(AppConfig appConfig) {
        mAppConfig = appConfig;
    }

    public static Context getApplicationContext(){
        return mAppConfig.ctx;
    }

    /**
     * 获取服务端地址
     *
     * @return
     */
    public static String getServerUrl() {
        return mAppConfig.baseUrl;
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
     * @return 当前应用的版本号
     */
    public static int getVersionCode() {
        return mAppConfig.versionCode;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        return mAppConfig.versionName;
    }

    /**
     * @return 当前应用的包名
     */
    public static String getPackageName() {
        return mAppConfig.packageName;
    }

    /**
     * 判断安装的版本是不是debug版本 <br/>
     */
    public static boolean isApkDebugEnable() {
        try {
            ApplicationInfo info = mAppConfig.ctx.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
