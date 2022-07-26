package com.sun.base.util;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: ①adapter的ViewHolder中不要申明needClick=true,手动实现点击事件
 * ②使用needClick=true时,当前类要实现onClickListener接口
 */
public class ViewInjectUtil {
    /**
     * activity中的注解,只需要传this即可
     *
     * @param activity activity.this
     */
    public static void initInActivity(Activity activity) {
        try {
            Field[] fields = activity.getClass().getDeclaredFields();
            if (null != fields && fields.length > 0) {
                for (Field field : fields) {
                    ViewInject viewInject = field.getAnnotation(ViewInject.class);//返回ViewInject中声明的所有注解
                    if (null != viewInject) {
                        int viewId = viewInject.id();
                        field.setAccessible(true);//暴力反射private
                        field.set(activity, activity.findViewById(viewId));
                        boolean clickMethod = viewInject.needClick();
                        if (clickMethod) {
                            activity.findViewById(viewId).setOnClickListener((View.OnClickListener) activity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("注解", "失败");
            throw new InjectException("反射注解失败");
        }
    }

    /**
     * @param o           类的实例对象如ViewHolder.this
     * @param convertView 复用的ConvertView对象,或者fragment
     */
    public static void initNotInActivity(Object o, View convertView) {
        try {
            Field[] fields = o.getClass().getDeclaredFields();
            if (null != fields && fields.length > 0) {
                for (Field field : fields) {
                    ViewInject viewInject = field.getAnnotation(ViewInject.class);//返回ViewInject中声明的所有注解
                    if (null != viewInject) {
                        int viewId = viewInject.id();
                        field.setAccessible(true);//暴力反射private
                        field.set(o, convertView.findViewById(viewId));
                        boolean clickMethod = viewInject.needClick();
                        if (clickMethod) {
                            convertView.findViewById(viewId).setOnClickListener((View.OnClickListener) o);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("注解", "失败");
            throw new InjectException("反射注解失败");
        }
    }
}
