package com.sun.base.sp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.sun.base.R;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note: sp帮助类，存储文件名：Harper_SP
 */
public class SpHelper {

    /**
     * 获取以App名称命名的相关SharedPreferences实例对象
     *
     * @param context context
     * @return SharedPreferences
     */
    public static SharedPreferences getAppSharedPreferences(Context context) {
        return getSharedPreferences(context, context.getString(R.string.Harper_SP));
    }

    /**
     * 获取SharedPreferences实例对象
     *
     * @param context  context
     * @param fileName sp的文件名
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreferences(Context context, String fileName) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 获取默认的SharedPreferences实例对象
     * android.preference.PreferenceManager过时了，替换成androidx.preference.PreferenceManager
     *
     * @param context context
     * @return SharedPreferences
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
