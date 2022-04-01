package com.sun.base.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note: 关于解码编码的工具类
 */
public class EncodeUtil {

    private static final String TAG = "EncodeUtil";

    private EncodeUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return the urlencoded string.
     *
     * @param input The input.
     * @return
     */
    public static String urlEncode(final String input) {
        return urlEncode(input, "utf-8");
    }

    /**
     * Return the urlencoded string.
     *
     * @param input       The input.
     * @param charsetName The name of charset.
     * @return the urlencoded string
     */
    public static String urlEncode(final String input, final String charsetName) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(input, charsetName);
        } catch (UnsupportedEncodingException e) {
            LogHelper.e(TAG, "UnsupportedEncodingException", e);
        }
        return input;
    }
}
