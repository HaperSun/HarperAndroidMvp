package com.sun.base.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 手机正则匹配
 */
public class PhoneNumberCheckUtil {
    /**
     * ^ 匹配输入字符串开始的位置
     * \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
     * $ 匹配输入字符串结尾的位置
     */
    private static final Pattern HK_PATTERN = Pattern.compile("^(5|6|8|9)\\d{7}$");
    private static final Pattern CHINA_PATTERN = Pattern.compile("^((12[0-9])|(13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");
    private static final Pattern NUM_PATTERN = Pattern.compile("[0-9]+");

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isLegal(String s) throws PatternSyntaxException {
        return isChinaPhoneLegal(s) || isHkPhoneLegal(s);
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
    private static boolean isChinaPhoneLegal(String s) throws PatternSyntaxException {
        return CHINA_PATTERN.matcher(s).matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    private static boolean isHkPhoneLegal(String s) throws PatternSyntaxException {
        return HK_PATTERN.matcher(s).matches();
    }

    /**
     * 判断是否是正整数的方法
     */
    public static boolean isNumeric(String s) {
        return NUM_PATTERN.matcher(s).matches();
    }
}
