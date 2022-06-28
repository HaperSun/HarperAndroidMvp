package com.sun.base.util;

import android.text.TextUtils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 密码验证
 */
public class PasswordCheckUtil {

    /**
     * 密码正则
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$");

    public static boolean inLength(String s) {
        return !TextUtils.isEmpty(s) && s.length() >= 6 && s.length() <= 32;
    }

    /**
     * 密码验证
     */
    public static boolean isLegal(String s) throws PatternSyntaxException {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(s).matches();
    }

}
