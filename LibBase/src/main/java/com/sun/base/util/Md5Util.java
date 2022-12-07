package com.sun.base.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: Harper
 * @date: 2021/12/14
 * @note: md5 加密算法工具类 <br/>
 */
public final class Md5Util {

    /**
     * 全局数组
     */
    private final static String[] STR_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private Md5Util() {
        throw new UnsupportedOperationException("you cannot new Md5Util!");
    }

    /**
     * 返回形式为数字跟字符串
     * @param bByte byte
     * @return String
     */
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return STR_DIGITS[iD1] + STR_DIGITS[iD2];
    }

    /**
     * 返回形式只为数字
     * @param bByte  byte
     * @return String
     */
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    /**
     * 转换字节数组为16进制字串
     * @param bByte byte[]
     * @return String
     */
    private static String byteToString(byte[] bByte) {
        StringBuilder sBuffer = new StringBuilder();
        for (byte b : bByte) {
            sBuffer.append(byteToArrayString(b));
        }
        return sBuffer.toString();
    }

    public static String getMd5Code(String strObj) {
        String resultString = null;
        try {
            resultString = strObj;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes(Charset.forName("UTF-8"))));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
}