package com.sun.base.util;

/**
 * @author: Harper
 * @date: 2022/4/2
 * @note: 时间常量
 */
public interface TimeConstant {

    String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    String YMD_HM = "yyyy-MM-dd HH:mm";
    String YMD = "yyyy-MM-dd";
    String HMS = "HH:mm:ss";
    String MD = "MM-dd";
    String HM = "HH:mm";
    String MS = "mm:ss";

    int MSEC = 1;
    int SEC = 1000;
    int MIN = 60000;
    int HOUR = 3600000;
    int DAY = 86400000;
}
