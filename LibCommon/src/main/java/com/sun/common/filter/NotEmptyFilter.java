package com.sun.common.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
/**
 * @author: Harper
 * @date:   2021/12/30
 * @note:
 */
public class NotEmptyFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        return TextUtils.isEmpty(source.toString().trim()) ? "" : source;
    }
}
