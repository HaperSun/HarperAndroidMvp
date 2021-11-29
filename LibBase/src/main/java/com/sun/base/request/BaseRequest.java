package com.sun.base.request;

import android.text.TextUtils;

import com.sun.base.net.annotation.IgnoreIfNullOrEmpty;
import com.sun.base.net.annotation.Key;
import com.sun.base.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */

public class BaseRequest {

    protected boolean mIsNeedVerification = true; //需不需要加额外的校验参数，默认开启，如果子类不需要校验参数，可以在子类构造函数中，将此值设为false

    protected HashMap<String, String> params = new HashMap<>();

    public HashMap<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        } else {
            params.clear();
        }
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getModifiers() != Modifier.PRIVATE) {
                continue;
            }
            try {
                String key = field.getName();
                if (field.isAnnotationPresent(Key.class)) {
                    Key annotationKey = field.getAnnotation(Key.class);
                    String annotationValue = annotationKey.value();
                    if (!TextUtils.isEmpty(annotationValue)) {
                        key = annotationValue;
                    }
                }
                field.setAccessible(true);
                String value = null;
                Object valueObj = field.get(this);
                if (valueObj != null) {
                    value = String.valueOf(valueObj);
                }
                if (!TextUtils.isEmpty(value)) {
                    params.put(key, value);
                } else {//这里的逻辑是愚蠢的服务端说即使字段为空，也需要传参数 what the hell!
                    //有这个标识的话，就不传给服务端了
                    if (field.isAnnotationPresent(IgnoreIfNullOrEmpty.class)) {
                        continue;
                    }
                    params.put(key, "");
                }
            } catch (IllegalAccessException e) {
                LogUtil.e(getClass().getName(), "IllegalAccessException", e);
            }
        }
        return params;
    }
}
