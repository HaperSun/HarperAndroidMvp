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
        return TimeHelp.millis2String(deadLine, "mm:ss");
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
    public static String millis2String(final long millis) {
        return millis2String(millis, getDateFormatYmdHms());
    }

    public static String millisToString(final long millis) {
        return millis2String(millis, getDateFormatYmd());
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
    public static String millis2String(final long millis, @NonNull final DateFormat format) {
        return format.format(new Date(millis));
    }

    /**
     * 毫秒转化成字符串
     *
     * @param millis
     * @param formatStr
     * @return
     */
    public static String millis2String(final long millis, @NonNull final String formatStr) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(new Date(millis));
    }

    /**
     * 根据时间戳获取月份
     *
     * @param millis
     * @return
     */
    public static int millis2Month(final long millis) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millis);
        return cd.get(Calendar.MONTH) + 1;//月份是从0开始计算的，所以需要加1
    }

    /**
     * 根据时间戳获取月份中的天数
     *
     * @param millis
     * @return
     */
    public static int millis2DayOfMonth(final long millis) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millis);
        return cd.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 时间戳转换星期字符串
     *
     * @param time
     * @return
     */
    public static String millis2Week(long time) {

        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(time));

        int week = cd.get(Calendar.DAY_OF_WEEK); //获取星期

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
        return millis >= wee && millis < wee + TimeConstants.DAY;
    }

    /**
     * Return whether it is yesterday.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isYesterday(final long millis) {
        long wee = getWeeOfToday() - TimeConstants.DAY;
        return millis >= wee && millis < wee + TimeConstants.DAY;
    }

    /**
     * Return whether it is tomorrow.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isTomorrow(final long millis) {
        long wee = getWeeOfToday() + TimeConstants.DAY;
        return millis >= wee && millis < wee + TimeConstants.DAY;
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
