package com.sun.base.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 邮箱匹配
 */
public class EmailCheckUtil {

    /**
     * 邮箱正则^
     * ^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$");

    /**
     * 匹配邮箱
     */
    public static boolean isEmailLegal(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(str).matches();
    }

}
