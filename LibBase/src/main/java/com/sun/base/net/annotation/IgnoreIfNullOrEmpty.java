package com.sun.base.net.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: BaseRequest中key值对应的value为null或空值的时候忽略，不上传
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreIfNullOrEmpty {
}
