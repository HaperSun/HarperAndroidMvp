package com.sun.base.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: AES 加解密
 */
public class AesUtil {

    private static final String TAG = AesUtil.class.getSimpleName();
    private static final String ENCODING = "utf-8";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * 加密
     *
     * @param sKey 秘钥
     * @param iv   向量
     * @param sSrc 加密的字符串
     */
    public static String encrypt(String sKey, String iv, String sSrc) {
        try {
            SecretKeySpec spec = new SecretKeySpec(sKey.getBytes(ENCODING), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, spec, ips);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(ENCODING));

            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(TAG, "encrypt", e);
        }

        return null;
    }

    /**
     * 解密
     *
     * @param sKey 秘钥
     * @param iv   向量
     * @param sSrc 解密的字符串
     */
    public static String decrypt(String sKey, String iv, String sSrc) {
        try {
            byte[] raw = sKey.getBytes(ENCODING);
            SecretKeySpec spec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, spec, ips);
            byte[] s = Base64.decode(sSrc, Base64.NO_WRAP);
            byte[] encrypted = cipher.doFinal(s);

            return new String(encrypted, ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(TAG, "decrypt", e);
        }

        return null;
    }

    private AesUtil() {}
}
