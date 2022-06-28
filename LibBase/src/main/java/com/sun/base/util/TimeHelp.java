package com.sun.base.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

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
public final class TimeHelp {

    /**
     * 一天的ms数
     */
    public static final long TIME_DAY = 24 * 60 * 60 * 1000L;

    private static DateFormat getDateFormatYmdHms() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    private static DateFormat getDateFormatYmd() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    /**
     * 格式化截止时间显示
     *
     * @param deadLine 截止时间时间戳，精确到ms
     * @return String
     */
    public static String getFormatDeadLineTime(long deadLine) {
        //截止时间始终显示"MM月dd日 HH:mm"
        return TimeHelp.formatTime(deadLine, "mm:ss");
    }

    private TimeHelp() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Milliseconds to the formatted time string.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param millis The milliseconds.
     * @return the formatted time string
     */
    public static String formatTime(final long millis) {
        return formatTime(millis, getDateFormatYmdHms());
    }

    public static String millisToString(final long millis) {
        return formatTime(millis, getDateFormatYmd());
    }

    /**
     * 将字符串转为时间戳
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static long string2Millis(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * Milliseconds to the formatted time string.
     *
     * @param millis The milliseconds.
     * @param format The format.
     * @return the formatted time string
     */
    public static String formatTime(final long millis, @NonNull final DateFormat format) {
        return format.format(new Date(millis));
    }

    /**
     * 毫秒转化成字符串时间戳
     *
     * @param millisecond 毫秒值
     * @param formatStr   字符串时间戳格式
     * @return
     */
    public static String formatTime(long millisecond, @NonNull String formatStr) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(new Date(millisecond));
    }

    /**
     * 根据毫秒时间戳，获取每月份中的天
     *
     * @param millisecond
     * @return
     */
    public static int getMonth(long millisecond) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millisecond);
        //月份是从0开始计算的，所以需要加1
        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据毫秒时间戳，获取月份中的天
     *
     * @param millisecond 毫秒
     * @return
     */
    public static int getDay(long millisecond) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millisecond);
        return cd.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据毫秒值获取日期名称
     *
     * @param millisecond 毫秒时间戳
     * @return 星期名称
     */
    public static String getWeekName(long millisecond) {
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
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final long millis) {
        long wee = getWeeOfToday();
        return millis >= wee && millis < wee + TimeConstant.DAY;
    }

    /**
     * Return whether it is yesterday.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isYesterday(final long millis) {
        long wee = getWeeOfToday() - TimeConstant.DAY;
        return millis >= wee && millis < wee + TimeConstant.DAY;
    }

    /**
     * Return whether it is tomorrow.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isTomorrow(final long millis) {
        long wee = getWeeOfToday() + TimeConstant.DAY;
        return millis >= wee && millis < wee + TimeConstant.DAY;
    }

    /**
     * Return whether it is this year.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isThisYear(final long millis) {
        Calendar now = Calendar.getInstance();
        return isYear(millis, now.get(Calendar.YEAR));
    }

    /**
     * Return whether it is the year.
     *
     * @param millis The milliseconds.
     * @param year   Which year
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isYear(final long millis, final int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.YEAR) == year;
    }

    /**
     * Return today wee milliseconds.
     */
    public static long getWeeOfToday() {
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
     * @param timeStamp 指定时间戳
     * @return
     */
    public static long getMinTime(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //将时/分/秒/毫秒均置为0
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取今天的最小时间戳 即0时0分0秒
     *
     * @return
     */
    public static long getTodayMinTime() {
        long current = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取昨天的最小时间戳
     *
     * @return
     */
    public static long getYesterdayMinTime() {
        return (getTodayMinTime() - TIME_DAY);
    }

    /**
     * 获取明天的最大时间戳
     *
     * @return
     */
    public static long getTomorrowMaxTime() {
        return (getTodayMinTime() + 2 * TIME_DAY);
    }
}
