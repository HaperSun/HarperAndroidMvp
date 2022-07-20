package com.sun.base.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Harper
 * @date 2022/7/19
 * note: 与业务相关性强的时间帮助类
 */
public class TimeHelp {

    /**
     * 获取视频时长（格式化）
     *
     * @param timestamp
     * @return
     */
    public static String getVideoDuration(long timestamp) {
        if (timestamp < 1000) {
            return "00:01";
        }
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取图片格式化时间
     *
     * @param timestamp
     * @return
     */
    public static String getImageTime(long timestamp) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());
        Calendar imageCalendar = Calendar.getInstance();
        imageCalendar.setTimeInMillis(timestamp);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) == imageCalendar.get(Calendar.DAY_OF_YEAR) && currentCalendar.get(Calendar.YEAR) == imageCalendar.get(Calendar.YEAR)) {
            return "今天";
        } else if (currentCalendar.get(Calendar.WEEK_OF_YEAR) == imageCalendar.get(Calendar.WEEK_OF_YEAR) && currentCalendar.get(Calendar.YEAR) == imageCalendar.get(Calendar.YEAR)) {
            return "本周";
        } else if (currentCalendar.get(Calendar.MONTH) == imageCalendar.get(Calendar.MONTH) && currentCalendar.get(Calendar.YEAR) == imageCalendar.get(Calendar.YEAR)) {
            return "本月";
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
            return sdf.format(date);
        }
    }
}
