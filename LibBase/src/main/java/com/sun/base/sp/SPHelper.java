package com.sun.base.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note:
 */
public class SPHelper {

    //=====================begin 在这里定义各种sp的文件名
    /**
     * 作业计时相关的sp文件名
     */
    private static final String SP_FILE_NAME = "Harper_SP";
    //=====================end 在这里定义各种sp的文件名

    /**
     * 获取SharedPreferences实例对象
     *
     * @param context
     * @param fileName sp的文件名
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context, String fileName) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 获取作业相关的SharedPreferences实例对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getHomeworkSharedPreferences(Context context) {
        return getSharedPreferences(context, SP_FILE_NAME);
    }

    /**
     * 获取默认的SharedPreferences实例对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
