package com.sun.base.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class MeetingTextLengthFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 输入内容是否超过设定值，最多输入五个汉字10个字符
        if (getTextLength(dest.toString()) + getTextLength(source.toString()) > 50) {
            // 输入框内已经有10个字符则返回空字符
            if (getTextLength(dest.toString()) >= 50) {
                return "";
                // 如果输入框内没有字符，且输入的超过了10个字符，则截取前五个汉字
            } /*else if (getTextLength(dest.toString()) == 0) {
                return source.toString().substring(0, 10);
            } else {
                // 输入框已有的字符数为双数还是单数
                if (getTextLength(dest.toString()) % 2 == 0) {
                    return source.toString().substring(0, 10 - (getTextLength(dest.toString()) / 2));
                } else {
                    return source.toString().substring(0, 10 - (getTextLength(dest.toString()) / 2 + 1));
                }
            }*/
        }
        return null;
    }

    /**
     * 获取字符数量 汉字占2个，英文占一个
     *
     * @param text
     * @return
     */
    public int getTextLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 255) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }
}
