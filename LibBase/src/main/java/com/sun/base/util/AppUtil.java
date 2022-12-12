package com.sun.base.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sun.base.BuildConfig;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public abstract class AppUtil {

    @SuppressLint("StaticFieldLeak")
    public static Context ctx;
    /**
     * 当前应用的包名
     */
    public static String mPackageName;
    /**
     * 当前应用的版本号
     */
    public static int mVersionCode;
    /**
     * 当前应用的版本名称
     */
    public static String mVersionName;
    /**
     * 获取服务端地址
     */
    public static String mBaseUrl;
    /**
     * 获取H5地址
     */
    public static String mH5Url;

    public static void init(Context context, String packageName, int versionCode, String versionName,
                            String baseUrl, String h5Url) {
        ctx = context;
        mPackageName = packageName;
        mVersionCode = versionCode;
        mVersionName = versionName;
        mBaseUrl = baseUrl;
        mH5Url = h5Url;
    }

    /**
     * 判断安装的版本是不是debug版本
     *
     * @return boolean
     */
    public static boolean isDebugApk() {
        return BuildConfig.DEBUG;
    }
}
