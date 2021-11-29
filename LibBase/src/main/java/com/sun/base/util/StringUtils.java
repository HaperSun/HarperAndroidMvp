package com.sun.base.util;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note: 字符串操作工具包
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern passworder = Pattern.compile("^[\\w\\d]{6,}");

    private final static Pattern IMG = Pattern
            .compile(".*?(gif|jpeg|png|jpg|bmp|JPEG|PNG|JPG|BMP|GIF)");

    private final static Pattern URL = Pattern
            .compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    private final static Pattern number = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");

    private final static Pattern tel = Pattern.compile("^((13[0-9])|(14[0-9])|(15([0-9]|[5-9]))|(18[0-9]))\\d{8}$");

    private final static Pattern END = Pattern.compile("face_[0-9]{1,}");


    /**
     * 正则表达式，用来判断消息内是否有表情 <br/>
     */
    public final static String face_regrex = "\\[face_(([1-9]|[1-6]\\d)|(70|71))\\]";
    public final static Pattern face = Pattern.compile(face_regrex);

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> timeFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    /**
     * 判断字符串是否是网络连接地址
     *
     * @param url
     * @return
     */
    public static boolean isWebUrlString(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static String nullToNone(String s) {
        String result = "";
        if (s != null) {
            result = s;
        }
        return result;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        return toDate(sdate, dateFormater.get());
    }

    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getStrDate(long times) {
        return dateFormater2.get().format(new Date(times));
    }

    public static String getDateString(Date date) {
        return dateFormater.get().format(date);
    }

    public static String getTimeString(Date date) {
        if (date == null) {
            return "未知";
        }
        return getTimeString(date.getTime());
    }

    public static String getTimeString(long timestamp) {
        if (timestamp < 0) {
            return "未知";
        }
        return timeFormater.get().format(new Date(timestamp));
    }

    /**
     * 格式化时间
     *
     * @param timestamp
     * @param pattern   例如"yyyy-MM-dd HH:mm"
     * @return
     */
    public static String getFormatTime(long timestamp, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(timestamp);
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time;

        if (TimeZoneUtil.isInEasternEightZones()) {
            time = toDate(sdate);
        } else {
            time = TimeZoneUtil.transformTime(toDate(sdate),
                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
        }

        if (time == null) {
            return "Unknown";
        }
        String ftime;
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater.get().format(cal.getTime());
        String paramDate = dateFormater.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天 ";
        } else if (days > 2 && days < 31) {
            ftime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            ftime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            ftime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            ftime = "3个月前";
        } else {
            ftime = dateFormater.get().format(time);
        }
        return ftime;
    }

    public static boolean isEqual(String compa, String compb) {
        try {
            return compa.equals(compb);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 判断一个文件名是否是office文档
     */
    public static boolean isDoc(String fileName) {
        boolean result = false;

        if (!isEmpty(fileName)) {
            fileName = fileName.toLowerCase();
            result = fileName.endsWith(".doc")
                    || fileName.endsWith(".docx")
                    || fileName.endsWith("ppt")
                    || fileName.endsWith("pptx")
                    || fileName.endsWith("xlsx")
                    || fileName.endsWith("xls");
        }
        return result;
    }

    /**
     * 若数字< 10 ，则在前面加个0 <br/>
     */
    public static String friendlyStr(int source) {
        String result;
        if (source < 10) {
            result = "0" + source;
        } else {
            result = String.valueOf(source);
        }
        return result;
    }

    public static String friendly_time2(String sdate) {
        String res = "";
        if (isEmpty(sdate)) {
            return "";
        }

        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String currentData = StringUtils.getDataTime("MM-dd");
        int currentDay = toInt(currentData.substring(3));
        int currentMoth = toInt(currentData.substring(0, 2));

        int sMoth = toInt(sdate.substring(5, 7));
        int sDay = toInt(sdate.substring(8, 10));
        int sYear = toInt(sdate.substring(0, 4));
        Date dt = new Date(sYear, sMoth - 1, sDay - 1);

        if (sDay == currentDay && sMoth == currentMoth) {
            res = "今天 / " + weekDays[getWeekOfDate(new Date())];
        } else if (sDay == currentDay + 1 && sMoth == currentMoth) {
            res = "昨天 / " + weekDays[(getWeekOfDate(new Date()) + 6) % 7];
        } else {
            if (sMoth < 10) {
                res = "0";
            }
            res += sMoth + "/";
            if (sDay < 10) {
                res += "0";
            }
            res += sDay + " / " + weekDays[getWeekOfDate(dt)];
        }

        return res;
    }

    /**
     * 将时间戳转化为 n 小时 m 分钟
     * 传入的值以s为单位
     *
     * @param timestamp
     * @return
     */
    public static String friendly_time(long timestamp) {
        StringBuilder result = new StringBuilder();
        if (timestamp < 60) {
            return result.append("不到1分钟").toString();
        }
        int hours = (int) (timestamp / (60 * 60));
        int days = 0;
        if (hours >= 24) {
            days = hours / 24;
            result.append(days).append("天");
        }
        hours = hours - days * 24;
        if (result.indexOf("天") > 0) {
            // 表示剩余大于1天
            result.append(hours).append("小时");
        } else {
            // 剩余时间 小于1天 小时个数大于0 才显示小时
            if (hours > 0) {
                result.append(hours).append("小时");
            }
        }

        if (timestamp - (days * 24 * 60 * 60 + hours * 60 * 60) <= 0) {
            return result.append("0分").toString();
        }
        int minutes = (int) ((timestamp - (days * 24 * 60 * 60 + hours * 60 * 60)) / 60);
        result.append(minutes).append("分");
        return result.toString();
    }

    /**
     * HH:mm:ss <br/>
     *
     * @param time 毫秒位单位
     */
    public static String friendly_time_second(long time) {
        StringBuilder result = new StringBuilder();
        if (time <= 0) {
            return result.append("00").append(":").append("00").append(":").append("00").toString();
        }
        time /= 1000;
        int hours = (int) (time / (60 * 60));
        result.append(getDualNumber(hours)).append(":");
        if (time - (hours * 60 * 60) <= 0) {
            return result.append("00").append(":").toString();
        }
        int minutes = (int) ((time - (hours * 60 * 60)) / 60);
        result.append(getDualNumber(minutes)).append(":");
        int seconds = (int) (time - hours * 60 * 60 - minutes * 60);
        result.append(getDualNumber(seconds));
        return result.toString();
    }

    public static String new_friendly_time_second(long time) {
        StringBuilder result = new StringBuilder();
        if (time <= 0) {
            return result.append("0秒").toString();
        }
        int hours = (int) (time / (60 * 60));
        if (hours > 0) {//大于一小时
            result.append(hours).append("小时");
        }

        if (hours > 0) {//大于一小时  则计算分  0 分 0秒都可显示
            int minutes = (int) ((time - (hours * 60 * 60)) / 60);
            result.append(minutes).append("分");
            int seconds = (int) (time - hours * 60 * 60 - minutes * 60);
            result.append(seconds).append("秒");
        } else { // 不大于一小时  则不显示0分
            int minutes = (int) ((time - (hours * 60 * 60)) / 60);
            if (minutes > 0) {//满1分钟  显示分
                result.append(minutes).append("分");
            }
            int seconds = (int) (time - hours * 60 * 60 - minutes * 60);//秒数必显示
            result.append(seconds).append("秒");
        }
        return result.toString();
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater.get().format(today);
            String timeDate = dateFormater.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    public static String getCurTimeStr() {
        Calendar cal = Calendar.getInstance();
        return dateFormater.get().format(cal.getTime());
    }

    public static String timestamp2DateStr(String timestamp, SimpleDateFormat sdf) {
        if (StringUtils.isEmpty(timestamp) || "null".equals(timestamp)) {
            timestamp = "0";
        }
        return sdf.format(new Date(Long.parseLong(timestamp) * 1000));
    }


    /***
     * 计算两个时间差，返回的是的秒s
     *
     * @param dete1
     * @param date2
     * @return
     * @author 火蚁 2015-2-9 下午4:50:06
     */
    public static long calDateDifferent(String dete1, String date2) {

        long diff = 0;

        Date d1;
        Date d2;

        try {
            d1 = dateFormater.get().parse(dete1);
            d2 = dateFormater.get().parse(date2);

            // 毫秒ms
            diff = d2.getTime() - d1.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diff / 1000;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || input.toLowerCase().equals("null")) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            return false;
        }
        return emailer.matcher(email).matches();
    }

    /**
     * 判断一个url或者文件名是否为图片url
     *
     * @param urlName
     * @return
     */
    public static boolean isImg(String urlName) {
        if (urlName == null || urlName.trim().length() == 0) {
            return false;
        }
        return IMG.matcher(urlName).matches();
    }

    /**
     * 判断是否为一个合法的url地址
     *
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return URL.matcher(str).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    public static String[] int2StrArray(int[] ints) {
        if (ints == null) {
            return null;
        }
        String[] strs = new String[ints.length];
        for (int i = 0; i < ints.length; i++) {
            strs[i] = String.valueOf(ints[i]);
        }
        return strs;
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = null;
        BufferedReader read = null;
        try {
            isr = new InputStreamReader(is,"UTF-8");
            read = new BufferedReader(isr);
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line).append("<br>");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(isr);
            FileUtil.close(read);
            FileUtil.close(is);
        }
        return res.toString();
    }

    /***
     * 截取字符串
     *
     * @param start 从那里开始，0算起
     * @param num   截取多少个
     * @param str   截取的字符串
     * @return
     */
    public static String getSubString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        int leng = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > leng) {
            start = leng;
        }
        if (num < 0) {
            num = 1;
        }
        int end = start + num;
        if (end > leng) {
            end = leng;
        }
        return str.substring(start, end);
    }

    /**
     * 获取当前时间为每年第几周
     *
     * @return
     */
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    /**
     * 获取当前时间为每年第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        week = week == 0 ? 52 : week;
        return week > 0 ? week : 1;
    }

    public static int[] getCurrentDate() {
        int[] dateBundle = new int[3];
        String[] temp = getDataTime("yyyy-MM-dd").split("-");

        for (int i = 0; i < 3; i++) {
            try {
                dateBundle[i] = Integer.parseInt(temp[i]);
            } catch (Exception e) {
                dateBundle[i] = 0;
            }
        }
        return dateBundle;
    }

    /**
     * 返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 去除城市名中的市
     *
     * @param city
     * @return
     */
    public static String eliminationCity(String city) {
        String result = null;
        if (city.endsWith("市")) {
            int index = city.lastIndexOf("市");
            result = city.substring(0, index);
        }
        return result;
    }


    /*
     * 将十进制转换成ip地址
     */
    public static String num2ip(int ip) {
        int[] b = new int[4];
        String x;
        b[0] = (ip >> 24) & 0xff;
        b[1] = (ip >> 16) & 0xff;
        b[2] = (ip >> 8) & 0xff;
        b[3] = ip & 0xff;
        x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "."
                + Integer.toString(b[3]);
        return x;
    }

    /**
     * 获取手机当前时间 <br/>
     * created by dengjie at 2016/01/06 11:53
     */
    @SuppressLint("SimpleDateFormat")
    public static String getPhoneCurrentTime() {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        return date.format(Calendar.getInstance().getTime());
    }


    /**
     * 将传递的浮点型字符串小数转换为int类型的数字
     *
     * @param src
     * @return
     */
    public static String getFloatToIntString(String src) {
        String result = "";
        if (src == null) {
            return "0";
        }
        try {
            int ff = (int) Float.parseFloat(src);
            result = String.valueOf(ff);
        } catch (NumberFormatException e) {
            result = "0";
        } finally {
            return result;
        }
    }

    /**
     * 是否是6位以上  数字字母组合 <br/>
     */
    public static boolean isPasswordValid(String password) {
        return passworder.matcher(password).find();
    }

    public static void main(String[] args) {
        System.out.println(getFloatToIntString("3.33"));
    }

    public static int parseInt(String del) {
        if (del != null && !isEmpty(del) && isNumeric(del)) {
            if (del.contains(".")) {
                return (int) parseFloat(del);
            }
            return Integer.parseInt(del);
        } else {
            return 0;
        }
    }

    public static int parseInt(String value, int default_value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return default_value;
    }

    public static long parseLong(String del) {
        if (del != null && !isEmpty(del) && isNumeric(del)) {
            return Long.parseLong(del);
        } else {
            return 0L;
        }
    }

    public static long parseLong(String value, long default_value) {
        try {
            return Long.parseLong(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return default_value;
    }

    public static double parseDouble(String del) {
        if (del != null && !isEmpty(del) && isNumeric(del)) {
            return Double.parseDouble(del);
        } else {
            return 0.0D;
        }
    }

    public static double parseDouble(String value, double default_value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return default_value;
    }

    public static float parseFloat(String del) {
        if (del != null && !isEmpty(del) && isNumeric(del)) {
            return Float.parseFloat(del);
        } else {
            return 0.0F;
        }
    }

    public static boolean isNumeric(String str) {
        Matcher matcher = number.matcher(str);
        return matcher.matches();
    }

    /**
     * 解析题库中 <img></img>标签 <br/> <br/>
     */
    public static String dealImg(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        String regStr = "<img>(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?</img>";
        Pattern p = Pattern.compile(regStr);
        Matcher matcher = p.matcher(src);
        int start = 0;
        while (matcher.find()) {
            String s = src.substring(start, matcher.start());
            stringBuilder.append(s);
            stringBuilder.append("[图片]");
            start = matcher.end();
        }
        stringBuilder.append(src.substring(start, src.length()));

        return stringBuilder.toString();
    }

    /**
     * 获取下载url中的文件名
     *
     * @param url
     * @return
     */
    public static String getHttpFileName(String url) {
        String result = "";
        String[] strs = url.split("/");
        if (strs.length > 0) {
            result = strs[strs.length - 1];
        }
        return result;
    }

    public static boolean isTelNumber(String telNumber) {
        boolean result = false;

        if (isEmpty(telNumber)) {
            result = false;
        } else {
            Matcher matcher = tel.matcher(telNumber);
            if (matcher.matches()) {
                result = true;
            }
        }
        return result;
    }

    public static String getIntFromFloat(float avgScore) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(avgScore);
        if (p.indexOf(".") > 0) {
            p = p.replaceAll("0+?$", "");//去掉后面无用的零
            p = p.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return p;
    }

    public static String getDualNumber(int index) {
        if (index < 10) {
            return "0" + String.valueOf(index);
        } else {
            return String.valueOf(index);
        }
    }

    /**
     * 判断两个字符串大小
     */
    public static int compareTo(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int lim = Math.min(len1, len2);
        char[] charS1 = s1.toCharArray();
        char[] charS2 = s2.toCharArray();
        int k = 0;
        while (k < lim) {
            char c1 = charS1[k];
            char c2 = charS2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    /**
     * 去除字符串中的空白字符，包括空格、制表符、换页符
     *
     * @param str 源字符串
     *
     * @return 返回去除所有空白字符的字符串
     */
    public static String trimAllWhitespace(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return str;
        }
        char[] dest = new char[len];
        int destPos = 0;
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                dest[destPos++] = c;
            }
        }
        return new String(dest, 0, destPos);
    }

    /**
     * 移除文本尾部的空格
     *
     * @param content 原文本内容
     * @return 返回移除文本尾部空格后的内容
     */
    public static String trimTailWhiteSpace(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll("\\s*$", "");
        return content;
    }

    /**
     * 比较两个字符串是否相等（如果有一方为null，另一方为空字符串，也认为是相等）
     *
     * @param left
     * @param right
     * @return
     */
    public static boolean emptyNullEquals(String left, String right) {
        if (left == null) {
            return TextUtils.isEmpty(right);
        }
        if (right == null) {
            return TextUtils.isEmpty(left);
        }
        return TextUtils.equals(left, right);
    }


    /**
     * 获取名字
     *
     * @return 名字
     */
    public static String getName(String name, String desStr) {
        return name.contains(desStr) ? name : name + desStr;
    }

    /**
     * @param targetStr 要处理的字符串
     * @description 切割字符串，将文本和img标签碎片化，如"ab<img>cd"转换为"ab"、"<img>"、"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        List<String> splitTextList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<img.*?src=\\\"(.*?)\\\".*?>");
        Matcher matcher = pattern.matcher(targetStr);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                splitTextList.add(targetStr.substring(lastIndex, matcher.start()));
            }
            splitTextList.add(targetStr.substring(matcher.start(), matcher.end()));
            lastIndex = matcher.end();
        }
        if (lastIndex != targetStr.length()) {
            splitTextList.add(targetStr.substring(lastIndex, targetStr.length()));
        }
        return splitTextList;
    }

    /**
     * 获取img标签中的src值
     * @param content
     * @return
     */
    public static String getImgSrc(String content){
        String str_src = null;
        //目前img标签标示有3种表达式
        //<img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
        //开始匹配content中的<img />标签
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //获取到匹配的<img />标签中的内容
                String str_img = m_img.group(2);

                //开始匹配<img />标签中的src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    str_src = m_src.group(3);
                }
                //结束匹配<img />标签中的src

                //匹配content中是否存在下一个<img />标签，有则继续以上步骤匹配<img />标签中的src
                result_img = m_img.find();
            }
        }
        return str_src;
    }

    /**
     * 关键字高亮显示
     * @param target  需要高亮的关键字
     * @param text	     需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     * SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * vHolder.tv_itemName_search.setText(textString);
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 从html文本中提取图片地址，或者文本内容
     * @param html 传入html文本
     * @param isGetImage true获取图片，false获取文本
     * @return
     */
    public static ArrayList<String> getTextFromHtml(String html, boolean isGetImage){
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        //根据img标签分割出图片和字符串
        List<String> list = cutStringByImgTag(html);
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            if (text.contains("<img") && text.contains("src=")) {
                //从img标签中获取图片地址
                String imagePath = getImgSrc(text);
                imageList.add(imagePath);
            } else {
                textList.add(text);
            }
        }
        //判断是获取图片还是文本
        if (isGetImage) {
            return imageList;
        } else {
            return textList;
        }
    }
}