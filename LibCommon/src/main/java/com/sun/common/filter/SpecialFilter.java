package com.sun.common.filter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 过滤掉"/" and ":" 两个特殊字符
 */
public class SpecialFilter implements InputFilter {

    public SpecialFilter() {
        super();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // check black-list set
        for (int i = 0; i < source.length(); i++) {
            if (isSpecialCharacter(source.charAt(i))) {
                return "";
            }
        }
        return source;
    }

    private boolean isSpecialCharacter(char c) {
        return c == '/' || c == ':' || c == '\\'
                || c == '*' || c == '?' || c == '"'
                || c == '<' || c == '>' || c == '|';
    }
}
