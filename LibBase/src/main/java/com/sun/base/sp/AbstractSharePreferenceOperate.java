package com.sun.base.sp;

import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note: SharePreference操作接口
 */
public abstract class AbstractSharePreferenceOperate<T> {

    /**
     * 指定哪一个SharedPreferences 子类指定
     *
     * @return
     */
    protected abstract SharedPreferences getSharedPreferences();

    /**
     * key值 子类指定
     *
     * @return
     */
    protected abstract String getKey();

    /**
     * 取值
     *
     * @param key key值
     * @return
     */
    protected abstract T get(SharedPreferences sharedPreferences, String key);

    /**
     * 存值
     *
     * @param sharedPreferences
     * @param key               key值
     * @param t                 要存的数据
     * @return
     */
    protected abstract boolean save(SharedPreferences sharedPreferences, String key, T t);

    /**
     * 检查key值
     *
     * @return
     */
    private boolean checkKey() {
        String key = getKey();
        return !TextUtils.isEmpty(key);
    }

    /**
     * 检查SharedPreferences
     *
     * @return
     */
    private boolean checkSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences != null;
    }


    /**
     * 取值
     *
     * @return
     */
    public T get() {
        if (!checkSharedPreferences()) {
            return null;
        }
        if (!checkKey()) {
            return null;
        }
        return get(getSharedPreferences(), getKey());
    }


    /**
     * 存值
     *
     * @param t 要存的数据
     * @return
     */
    public boolean save(T t) {
        if (!checkSharedPreferences()) {
            return false;
        }
        if (!checkKey()) {
            return false;
        }
        return save(getSharedPreferences(), getKey(), t);
    }

}
