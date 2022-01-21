package com.sun.base.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sun.base.BuildConfig;
import com.sun.base.bean.BaseConfig;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public abstract class BaseUtil {

    @SuppressLint("StaticFieldLeak")
    private static BaseConfig mBaseConfig;

    public static void init(BaseConfig baseConfig) {
        mBaseConfig = baseConfig;
    }

    public static Context getApplicationContext(){
        return mBaseConfig.ctx;
    }

    /**
     * 获取服务端地址
     *
     * @return
     */
    public static String getServerUrl() {
        return mBaseConfig.baseUrl;
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
        return mBaseConfig.versionCode;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        return mBaseConfig.versionName;
    }

    /**
     * @return 当前应用的包名
     */
    public static String getPackageName() {
        return mBaseConfig.packageName;
    }
}
