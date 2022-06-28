package com.sun.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 注解声明
 * 常用的布局id和click(默认false不设置点击事件)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {

    int id();

    boolean needClick() default false;
}
