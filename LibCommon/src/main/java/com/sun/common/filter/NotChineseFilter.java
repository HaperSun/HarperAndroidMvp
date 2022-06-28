package com.sun.common.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 非中文汉字符输入
 */
public class NotChineseFilter implements InputFilter {


    public NotChineseFilter() {
        super();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence destTest = source;
        // check black-list set
        // 过滤一些复制粘贴的输入
        int length = source.length();
            StringBuilder sourceFinalSb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                char charVal = source.charAt(i);
                if (!isChinese(charVal)) {
                    sourceFinalSb.append(charVal);
                }
            }
            if (sourceFinalSb.length() > 0) {
                destTest = sourceFinalSb.toString();
            } else {
                destTest = "";
            }
        return TextUtils.equals(source, destTest) ? source : destTest;
    }

    //中文汉字
    private static final String REGEX_CN_CHAR = "[\u4e00-\u9fa5]";

    public static boolean isChinese(char codePoint) {
        Pattern chinesePat = Pattern.compile(REGEX_CN_CHAR);
        //判断是否是中文
        Matcher chineseMat = chinesePat.matcher(String.valueOf(codePoint));
        return chineseMat.matches();
    }

}
