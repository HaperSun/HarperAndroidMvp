package com.sun.base.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Harper
 * @date: 2022/7/7
 * @note: 用来存储临时数据用，解决Intent传递不了大数据的问题
 */
public class WeakDataHolder {

    private static volatile WeakDataHolder sInstance = null;

    private WeakDataHolder() {
        throw new RuntimeException("you cannot new WeakDataHolder!");
    }

    public static WeakDataHolder getInstance() {
        if (sInstance == null) {
            synchronized (WeakDataHolder.class) {
                if (sInstance == null) {
                    sInstance = new WeakDataHolder();
                }
            }
        }
        return sInstance;
    }

    private final Map<String, WeakReference<Object>> map = new HashMap<>();

    /**
     * 数据存储
     *
     * @param id id
     * @param object object
     */
    public void saveData(String id, Object object) {
        map.put(id, new WeakReference<>(object));
    }

    /**
     * 获取数据
     *
     * @param id id
     * @return Object
     */
    public Object getData(String id) {
        WeakReference<Object> weakReference = map.get(id);
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }
}
