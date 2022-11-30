package com.sun.base.util;

import com.google.gson.Gson;

/**
 * @author Harper
 * @date 2022/11/30
 * note: 统一获取Gson
 */
public class GsonUtil {

    private static volatile Gson mInstance = null;

    public static Gson getGson() {
        if (null == mInstance) {
            synchronized (GsonUtil.class) {
                if (null == mInstance) {
                    mInstance = new Gson();
                }
            }
        }
        return mInstance;
    }
}
