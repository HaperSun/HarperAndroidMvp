package com.sun.base.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Harper
 * @date:   2021/12/30
 * @note: //"^[a-zA-Z0-9_\u4e00-\u9fa5]+$"
 */
public class CharsFilter implements InputFilter {
    String regex="^[a-zA-Z0-9,_\u4e00-\u9fa5]";
    public CharsFilter() {
        super();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // check black-list set
        for (int i = 0; i < source.length(); i++) {
            if (!isSpecialCharacter(source.charAt(i))) {
                return "";
            }
        }
        return source;
    }

    private boolean isSpecialCharacter(char c) {

        Pattern p   =   Pattern.compile(regex);
        StringBuffer sb=new StringBuffer();
        sb.append(c);
        Matcher m   =   p.matcher(sb);
        return  m.matches();
    }
}
