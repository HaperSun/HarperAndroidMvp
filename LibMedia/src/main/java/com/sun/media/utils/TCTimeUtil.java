package com.sun.media.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式化工具
 */
public class TCTimeUtil {

    /**
     * 将秒数转换为hh:mm:ss的格式
     *
     * @param second
     * @return
     */
    public static String formattedTime(long second) {
        String formatTime;
        long h, m, s;
        h = second / 3600;
        m = (second % 3600) / 60;
        s = (second % 3600) % 60;
        if (h == 0) {
            formatTime = asTwoDigit(m) + ":" + asTwoDigit(s);
        } else {
            formatTime = asTwoDigit(h) + ":" + asTwoDigit(m) + ":" + asTwoDigit(s);
        }
        return formatTime;
    }

    private static String asTwoDigit(long digit) {
        String value = "";
        if (digit < 10) {
            value = "0";
        }
        value += String.valueOf(digit);
        return value;
    }

    /**
     * 格式化时间，返回时分秒
     * @param time time
     * @return String
     */
    public static String formatHms(String time){
        if (TextUtils.isEmpty(time)){
            return "";
        }
        if (time.contains(":")){
            return time;
        }
        long aa = Long.parseLong(time);
        return formattedTime(aa);
    }

    /**
     * 格式化时间，返回月日时分
     *
     * @param time time
     * @return String
     */
    public static String formatMdHm(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            Date date = new Date();
            date.setTime(Long.valueOf(time));
            return sdf.format(date);
        } catch (Exception e) {
            return time;
        }
    }
}
