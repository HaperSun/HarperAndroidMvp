package com.sun.db.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * @author Harper
 * @date 2021/11/9
 * note:
 */
public class CommonUtil {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void initCommonUtil(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 判断安装的版本是不是debug版本 <br/>
     */
    public static boolean isApkDebugEnable() {
        try {
            ApplicationInfo info = mContext.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
