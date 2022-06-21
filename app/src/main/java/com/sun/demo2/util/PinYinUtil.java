package com.sun.demo2.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author: Harper
 * @date: 2022/6/16
 * @note: 汉字工具类
 */
public class PinYinUtil {

    private static PinYinUtil util;
    private static HanyuPinyinOutputFormat format;

    public static PinYinUtil getInstance() {
        if (util == null) {
            synchronized (PinYinUtil.class.getName()) {
                if (util == null) {
                    util = new PinYinUtil();
                    format = new HanyuPinyinOutputFormat();
                    format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
                    format.setVCharType(HanyuPinyinVCharType.WITH_V);
                    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                }
            }
        }
        return util;
    }

    public String covertHanziToPinyinUpCase(String hanzi) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = hanzi.toCharArray();
        for (char c : charArray) {
            // 如果是空格字符
            if (Character.isSpaceChar(c)) {
                continue;
            }
            // 如果assic
            if (c <= 127) {
                sb.append(c);
                continue;
            }
            try {
                String[] hanziPinyinUpArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (null != hanziPinyinUpArray && hanziPinyinUpArray.length > 0 &&
                        null != hanziPinyinUpArray[0]) {
                    sb.append(hanziPinyinUpArray[0]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public StringBuffer sb = new StringBuffer();

    /**
     * 获取汉字字符串的首字母，英文字符不变
     * 例如：阿飞→af
     */
    public String getPinYinHeadChar(String chines) {
        sb.setLength(0);
        char[] chars = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char aChar : chars) {
            if (aChar > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(aChar, defaultFormat)[0].charAt(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(aChar);
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的第一个字母
     */
    public String getPinYinFirstLetter(String str) {
        sb.setLength(0);
        char c = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     */
    public String getPinYin(String chines) {
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : nameChar) {
            if (c > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
