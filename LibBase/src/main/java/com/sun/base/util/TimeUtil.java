package com.sun.base.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.sun.base.bean.TimeConstant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author: Harper
 * @date: 2022/4/2
 * @note: <p>Description: 时间工具类，部分代码来自开源库 AndroidUtilCode</p>
 * 开源项目
 * https://github.com/Blankj/AndroidUtilCode
 * 工具类
 * https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/src/main/java/com/blankj/utilcode/util/TimeUtils.java
 */
public final class TimeUtil {

    /**
     * 一天的ms数
     */
    public static final long TIME_DAY = 24 * 60 * 60 * 1000L;

    private TimeUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static DateFormat getDateFormatYmdHms() {
        return new SimpleDateFormat(TimeConstant.YMD_HMS, Locale.getDefault());
    }

    public static DateFormat getDateFormatYmd() {
        return new SimpleDateFormat(TimeConstant.YMD, Locale.getDefault());
    }

    /**
     * Millisecond to the formatted time string.
     *
     * @param millisecond 毫秒
     * @return <p>The pattern is {@code mm:ss}.</p>
     */
    public static String long2StringMs(long millisecond) {
        return long2String(millisecond, TimeConstant.MS);
    }

    /**
     * Millisecond to the formatted time string.
     *
     * @param millisecond 毫秒
     * @return <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     */
    public static String long2String(final long millisecond) {
        if (millisecond <= 0) {
            return "未知";
        }
        return long2String(millisecond, getDateFormatYmdHms());
    }

    /**
     * Millisecond to the formatted time string.
     *
     * @param millisecond 毫秒
     * @return <p>The pattern is {@code yyyy-MM-dd}.</p>
     */
    public static String long2StringYmd(final long millisecond) {
        return long2String(millisecond, getDateFormatYmd());
    }

    /**
     * Millisecond to the formatted time string.
     *
     * @param millisecond The millisecond.
     * @param format      The format.
     * @return the formatted time string
     */
    public static String long2String(final long millisecond, @NonNull final DateFormat format) {
        return format.format(new Date(millisecond));
    }

    /**
     * String to the formatted time millisecond.
     *
     * @param pattern <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *                <p>The pattern is {@code yyyy-MM-dd HH:mm}.</p>
     *                <p>The pattern is {@code yyyy-MM-dd}.</p>
     * @param s       time
     * @return millisecond
     */
    public static long string2Long(String s, String pattern) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }

    /**
     * Millisecond to the formatted time string.
     *
     * @param millisecond 毫秒值
     * @param pattern     <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *                    <p>The pattern is {@code yyyy-MM-dd HH:mm}.</p>
     *                    <p>The pattern is {@code yyyy-MM-dd}.</p>
     * @return the formatted time string
     */
    public static String long2String(long millisecond, @NonNull String pattern) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(millisecond));
    }

    @SuppressLint("SimpleDateFormat")
    public static Date string2Date(String s, String pattern){
        try {
            return new SimpleDateFormat(pattern).parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 根据毫秒时间戳，获取每月份中的天
     *
     * @param millisecond 毫秒值
     * @return int month
     */
    public static int getMonthByLong(long millisecond) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millisecond);
        //月份是从0开始计算的，所以需要加1
        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据毫秒时间戳，获取月份中的天
     *
     * @param millisecond 毫秒值
     * @return int day
     */
    public static int getDayByLong(long millisecond) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millisecond);
        return cd.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据毫秒值获取日期名称
     *
     * @param millisecond 毫秒值
     * @return 星期名称
     */
    public static String getWeekNameByLong(long millisecond) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(millisecond));
        int week = cd.get(Calendar.DAY_OF_WEEK);
        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = "周日";
                break;
            case Calendar.MONDAY:
                weekString = "周一";
                break;
            case Calendar.TUESDAY:
                weekString = "周二";
                break;
            case Calendar.WEDNESDAY:
                weekString = "周三";
                break;
            case Calendar.THURSDAY:
                weekString = "周四";
                break;
            case Calendar.FRIDAY:
                weekString = "周五";
                break;
            default:
                weekString = "周六";
                break;
        }
        return weekString;
    }

    /**
     * Date to the formatted time string.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param date The date.
     * @return the formatted time string
     */
    public static String date2String(final Date date) {
        if (date == null) {
            return "未知";
        }
        return date2String(date, getDateFormatYmdHms());
    }

    /**
     * Date to the formatted time string.
     *
     * @param date   The date.
     * @param format The format.
     * @return the formatted time string
     */
    public static String date2String(final Date date, @NonNull final DateFormat format) {
        return format.format(date);
    }

    /**
     * Return whether it is today.
     *
     * @param millisecond The millisecond.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final long millisecond) {
        long wee = getFirstTimeOfToday();
        return millisecond >= wee && millisecond < wee + TimeConstant.DAY;
    }

    /**
     * Return whether it is yesterday.
     *
     * @param millisecond The millisecond.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isYesterday(final long millisecond) {
        long wee = getFirstTimeOfToday() - TimeConstant.DAY;
        return millisecond >= wee && millisecond < wee + TimeConstant.DAY;
    }

    /**
     * Return whether it is tomorrow.
     *
     * @param millisecond The millisecond.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isTomorrow(final long millisecond) {
        long wee = getFirstTimeOfToday() + TimeConstant.DAY;
        return millisecond >= wee && millisecond < wee + TimeConstant.DAY;
    }

    /**
     * Return whether it is this year.
     *
     * @param millisecond The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isThisYear(final long millisecond) {
        Calendar now = Calendar.getInstance();
        return isYear(millisecond, now.get(Calendar.YEAR));
    }

    /**
     * Return whether it is the year.
     *
     * @param millisecond The millisecond.
     * @param year        check year
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isYear(final long millisecond, final int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        return calendar.get(Calendar.YEAR) == year;
    }

    /**
     * 获取当天的第一个时间戳
     */
    public static long getFirstTimeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间戳当天最小的时间戳，eg:传进来的时间戳代表的是2019-02-12 09:30:33 那么返回的是2019-02-12 00:00:00
     *
     * @param millisecond 指定时间戳
     * @return
     */
    public static long getFirstTimeByLong(long millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //将时/分/秒/毫秒均置为0
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取昨天的最小时间戳
     *
     * @return
     */
    public static long getYesterdayMinTime() {
        return (getFirstTimeOfToday() - TIME_DAY);
    }

    /**
     * 获取明天的最大时间戳
     *
     * @return
     */
    public static long getTomorrowMaxTime() {
        return (getFirstTimeOfToday() + 2 * TIME_DAY);
    }


}
