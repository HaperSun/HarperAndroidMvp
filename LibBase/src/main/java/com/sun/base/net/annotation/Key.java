package com.sun.base.net.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 为了解决BaseRequest中key值为java关键字时无法使用问题
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Key {

    /**
     * 返回你在请求中想要的key值
     *
     * @return
     */
    String value();
}
