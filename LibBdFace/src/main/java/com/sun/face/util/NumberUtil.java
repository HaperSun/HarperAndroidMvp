package com.sun.face.util;

/**
 * @author: Harper
 * @date: 2022/5/13
 * @note: 数字工具类
 */
public class NumberUtil {

    /**
     * float转double
     *
     * @param num float数据
     * @return double数据
     */
    public static double floatToDouble(float num) {
        return Double.valueOf(String.valueOf(num));
    }
}
