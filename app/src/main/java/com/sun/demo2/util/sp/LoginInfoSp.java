package com.sun.demo2.util.sp;

import android.content.SharedPreferences;

import com.sun.base.sp.AbstractSharePreferenceOperate;
import com.sun.base.sp.SpHelper;
import com.sun.base.util.AppUtil;

/**
 * @author Harper
 * @date 2021/12/31
 * note: 使用sp保存登录信息
 */
public class LoginInfoSp extends AbstractSharePreferenceOperate<String> {

    private static volatile LoginInfoSp mInstance = null;

    public static LoginInfoSp getSp() {
        if (mInstance == null) {
            synchronized (LoginInfoSp.class) {
                if (mInstance == null) {
                    mInstance = new LoginInfoSp();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return SpHelper.getAppSharedPreferences(AppUtil.getCtx());
    }

    @Override
    protected String getKey() {
        return LoginInfoSp.class.getSimpleName();
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
