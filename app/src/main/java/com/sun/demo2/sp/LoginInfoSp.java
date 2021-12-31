package com.sun.demo2.sp;

import android.content.SharedPreferences;

import com.sun.base.sp.AbstractSharePreferenceOperate;
import com.sun.base.sp.SPHelper;
import com.sun.demo2.MainApplication;

/**
 * @author Harper
 * @date 2021/12/31
 * note: 使用sp保存登录信息
 */
public class LoginInfoSp extends AbstractSharePreferenceOperate<String> {

    @Override
    protected SharedPreferences getSharedPreferences() {
        return SPHelper.getHomeworkSharedPreferences(MainApplication.getContext());
    }

    @Override
    protected String getKey() {
        return "LOGIN_INFO";
    }

    @Override
    protected String get(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getString(key, "");
    }

    @Override
    protected boolean save(SharedPreferences sharedPreferences, String key, String s) {
        return sharedPreferences.edit().putString(key, s).commit();
    }
}
