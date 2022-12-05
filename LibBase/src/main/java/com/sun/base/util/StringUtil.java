package com.sun.base.util;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.sun.base.bean.TimeConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 字符串操作工具包
 */
public class StringUtil {

    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern IMG = Pattern.compile(".*?(gif|jpeg|png|jpg|bmp|JPEG|PNG|JPG|BMP|GIF)");
    private final static Pattern URL = Pattern.compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");
    private final static Pattern number = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    /**
     * 正则表达式，用来判断消息内是否有表情 <br/>
     */
    public final static String face_regrex = "\\[face_(([1-9]|[1-6]\\d)|(70|71))\\]";
    public final static Pattern face = Pattern.compile(face_regrex);
    /**
     * ^ 匹配输入字符串开始的位置
     * \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
     * $ 匹配输入字符串结尾的位置
     */
    private static final Pattern HK_PATTERN = Pattern.compile("^(5|6|8|9)\\d{7}$");
    private static final Pattern CHINA_PATTERN =
            Pattern.compile("^((12[0-9])|(13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");
    /**
     * 密码正则
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$");

    /**
     * 字符串空处理
     *
     * @param s string
     * @return string
     */
    public static String trimEmpty(String s) {
        return null == s || "".equals(s) || "null".equals(s) ? "" : s;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendlyTime(String sdate) {
        Date time;
        if (TimeZoneUtil.isInEasternEightZones()) {
            time = TimeUtil.string2Date(sdate, TimeConstant.YMD);
        } else {
            time = TimeZoneUtil.transformTime(TimeUtil.string2Date(sdate, TimeConstant.YMD),
                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
        }
        if (time == null) {
            return "Unknown";
        }
        String ftime;
        Calendar cal = Calendar.getInstance();
        // 判断是否是同一天
        String curDate = TimeUtil.getDateFormatYmd().format(cal.getTime());
        String paramDate = TimeUtil.getDateFormatYmd().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
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
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
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
            ftime = TimeUtil.getDateFormatYmd().format(time);
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
            result = fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith("ppt")
                    || fileName.endsWith("pptx") || fileName.endsWith("xlsx") || fileName.endsWith("xls");
        }
        return result;
    }

    public static String friendlyTime2(String sdate) {
        String res = "";
        if (isEmpty(sdate)) {
            return "";
        }
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String currentData = StringUtil.getDataTime("MM-dd");
        int currentDay = string2Int(currentData.substring(3));
        int currentMoth = string2Int(currentData.substring(0, 2));
        int sMoth = string2Int(sdate.substring(5, 7));
        int sDay = string2Int(sdate.substring(8, 10));
        int sYear = string2Int(sdate.substring(0, 4));
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
    public static String friendlyTime3(long timestamp) {
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

    public static String newFriendlyTimeSecond(long time) {
        StringBuilder result = new StringBuilder();
        if (time <= 0) {
            return result.append("0秒").toString();
        }
        int hours = (int) (time / (60 * 60));
        if (hours > 0) {
            //大于一小时
            result.append(hours).append("小时");
        }
        int minutes = (int) ((time - (hours * 60 * 60)) / 60);
        if (hours > 0) {
            //大于一小时  则计算分  0 分 0秒都可显示
            result.append(minutes).append("分");
            int seconds = (int) (time - hours * 60 * 60 - minutes * 60);
            result.append(seconds).append("秒");
        } else {
            // 不大于一小时  则不显示0分
            if (minutes > 0) {
                //满1分钟  显示分
                result.append(minutes).append("分");
            }
            //秒数必显示
            int seconds = (int) (time - hours * 60 * 60 - minutes * 60);
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
        Date time = TimeUtil.string2Date(sdate, TimeConstant.YMD);
        Date today = new Date();
        if (time != null) {
            String nowDate = TimeUtil.getDateFormatYmd().format(today);
            String timeDate = TimeUtil.getDateFormatYmd().format(time);
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
        String curDate = TimeUtil.getDateFormatYmd().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    public static String getCurTimeStr() {
        Calendar cal = Calendar.getInstance();
        return TimeUtil.getDateFormatYmd().format(cal.getTime());
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
            d1 = TimeUtil.getDateFormatYmd().parse(dete1);
            d2 = TimeUtil.getDateFormatYmd().parse(date2);
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
    public static boolean isNetUrl(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return URL.matcher(str).matches();
    }

    /**
     * 字符串转整数
     *
     * @param s
     * @return
     */
    public static int string2Int(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 对象转整数
     *
     * @param s
     * @return 转换异常返回 0
     */
    public static long string2Long(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Long.parseLong(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param s
     * @return 转换异常返回 false
     */
    public static boolean string2Bool(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Boolean.parseBoolean(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String[] int2StrArray(int[] ints) {
        if (ints == null) {
            return null;
        }
        String[] s = new String[ints.length];
        for (int i = 0; i < ints.length; i++) {
            s[i] = String.valueOf(ints[i]);
        }
        return s;
    }


    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is 流
     * @return
     */
    public static String inputStream2String(InputStream is) {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader read = null;
        try {
            isr = new InputStreamReader(is, "UTF-8");
            read = new BufferedReader(isr);
            String line;
            line = read.readLine();
            while (line != null) {
                sb.append(line).append("<br>");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(isr);
            FileUtil.close(read);
            FileUtil.close(is);
        }
        return sb.toString();
    }

    /***
     * 截取字符串
     *
     * @param start 从那里开始，0算起
     * @param num   截取多少个
     * @param str   截取的字符串
     * @return
     */
    public static String subString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        try {
           return str.substring(start, start + num);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
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
        if (StringUtil.isEmpty(src)) {
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
    public static String getFileNameFromUrl(String url) {
        String result = "";
        String[] strs = url.split("/");
        if (strs.length > 0) {
            result = strs[strs.length - 1];
        }
        return result;
    }

    public static String float2String(float avgScore) {
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String p = decimalFormat.format(avgScore);
        if (p.indexOf(".") > 0) {
            //去掉后面无用的零
            p = p.replaceAll("0+?$", "");
            //如小数点后面全是零则去掉小数点
            p = p.replaceAll("[.]$", "");
        }
        return p;
    }

    /**
     * 将数字转成字符串，小于10的数字前面加0
     *
     * @param a 数字
     * @return
     */
    public static String int2String(int a) {
        return a < 10 ? "0" + a : String.valueOf(a);
    }

    /**
     * 去除字符串中的空白字符，包括空格、制表符、换页符
     *
     * @param s 源字符串
     * @return 返回去除所有空白字符的字符串
     */
    public static String trimAllWhitespace(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (len == 0) {
            return s;
        }
        char[] dest = new char[len];
        int destPos = 0;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                dest[destPos++] = c;
            }
        }
        return new String(dest, 0, destPos);
    }

    /**
     * 移除文本尾部的空格
     *
     * @param s 原文本内容
     * @return 返回移除文本尾部空格后的内容
     */
    public static String trimTailWhiteSpace(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return s.replaceAll("\\s*$", "");
    }

    /**
     * 比较两个字符串是否相等（如果有一方为null，另一方为空字符串，也认为是相等）
     *
     * @param a a
     * @param b b
     * @return
     */
    public static boolean emptyNullEquals(String a, String b) {
        if (a == null) {
            return TextUtils.isEmpty(b);
        }
        if (b == null) {
            return TextUtils.isEmpty(a);
        }
        return TextUtils.equals(a, b);
    }


    /**
     * @param targetStr 要处理的字符串
     * @description 切割字符串，将文本和img标签碎片化，如"ab<img>cd"转换为"ab"、"<img>"、"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        List<String> splitTextList = new ArrayList<>();
        if (!TextUtils.isEmpty(targetStr) && targetStr.contains("img")) {
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
                splitTextList.add(targetStr.substring(lastIndex));
            }
        }
        return splitTextList;
    }

    /**
     * 获取img标签中的src值
     *
     * @param content
     * @return
     */
    public static String getImgSrc(String content) {
        if (TextUtils.isEmpty(content) || !content.contains("img")) {
            return content;
        }
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
     *
     * @param target 需要高亮的关键字
     * @param text   需要显示的文字
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
            // 需要重复！
            span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 从html文本中提取图片地址，或者文本内容
     *
     * @param html       传入html文本
     * @param isGetImage true获取图片，false获取文本
     * @return
     */
    public static ArrayList<String> getTextFromHtml(String html, boolean isGetImage) {
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        //根据img标签分割出图片和字符串
        List<String> list = cutStringByImgTag(html);
        if (CollectionUtil.notEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                String text = list.get(i);
                if (!TextUtils.isEmpty(text)) {
                    if (text.contains("<img") && text.contains("src=")) {
                        //从img标签中获取图片地址
                        String imagePath = getImgSrc(text);
                        imageList.add(imagePath);
                    } else {
                        textList.add(text);
                    }
                }
            }
        }
        //是获取图片还是获取文本
        if (isGetImage) {
            return imageList;
        } else {
            return textList;
        }
    }

    /**
     * 将String中null转成""
     *
     * @param s 文本
     * @return 文本
     */
    public static String formatString(String s) {
        return TextUtils.isEmpty(s) ? "" : s;
    }

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isLegalPhone(String s) throws PatternSyntaxException {
        return isLegalChinaPhone(s) || isLegalHkPhone(s);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 145,147,149
     * 15+除4的任意数(不要写^4，这样的话字母也会被认为是正确的)
     * 166
     * 17+3,5,6,7,8
     * 18+任意数
     * 198,199
     */
    public static boolean isLegalChinaPhone(String s) throws PatternSyntaxException {
        Matcher m = CHINA_PATTERN.matcher(s);
        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isLegalHkPhone(String s) throws PatternSyntaxException {
        Matcher m = HK_PATTERN.matcher(s);
        return m.matches();
    }

    /**
     * 是否是6位以上  数字字母组合 <br/>
     */
    public static boolean isLegalPassword(String s) throws PatternSyntaxException {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(s).matches();
    }
}