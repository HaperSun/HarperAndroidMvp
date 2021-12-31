package com.sun.base.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note: sp帮助类，存储文件名：Harper_SP
 */
public class SPHelper {

    /**
     * 获取SharedPreferences实例对象
     *
     * @param context  context
     * @param fileName sp的文件名
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context, String fileName) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 获取作业相关的SharedPreferences实例对象
     *
     * @param context context
     * @return
     */
    public static SharedPreferences getHomeworkSharedPreferences(Context context) {
        return getSharedPreferences(context, "Harper_SP");
    }

    /**
     * 获取默认的SharedPreferences实例对象
     *
     * @param context context
     * @return
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
