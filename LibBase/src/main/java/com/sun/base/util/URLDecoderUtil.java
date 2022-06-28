package com.sun.base.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 关于解码编码的工具类
 */
public class URLDecoderUtil {

    private static final String TAG = URLDecoderUtil.class.getSimpleName();

    private URLDecoderUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getDecodeStr(String decodeStr) {
        String decodedString = "";
        try {
            decodedString = URLDecoder.decode(decodeStr, "utf-8");
            System.out.println(decodedString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decodedString;
    }
}
