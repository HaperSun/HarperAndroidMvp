package com.sun.base.filter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @author Harper
 * @date 2022/1/20
 * note:EditText的长度过滤
 */
public class LengthFilter implements InputFilter {
    private final int mMax;
    private final LengthWatch mLengthWatch;

    public LengthFilter(int max, LengthWatch lengthWatch) {
        mMax = max;
        mLengthWatch = lengthWatch;
    }

    /**
     * @param source 输入的文字
     * @param start  输入=0，删除=0
     * @param end    输入=文字的长度，删除=0
     * @param dest   原先显示的内容
     * @param dstart 输入=原光标位置，删除=光标删除结束位置
     * @param dend   输入=原光标位置，删除=光标删除开始位置
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int keep = mMax - (dest.length() - (dend - dstart));

        if (keep <= 0) {
            if (mLengthWatch != null) {
                mLengthWatch.onReachMaxLength();
            }
            return "";
        } else if (keep >= end - start) {
            if (mLengthWatch != null) {
                //原字符串+输入字符串-（光标删除的开始位置-光标删除的结束位置）
            }
            return null; // keep original
        } else {
            keep += start;
            if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                --keep;
                if (keep == start) {
                    if (mLengthWatch != null) {
                        mLengthWatch.onReachMaxLength();
                    }
                    return "";
                }
            }
            if (mLengthWatch != null) {
                mLengthWatch.onReachMaxLength();
            }
            return source.subSequence(start, keep);
        }
    }

    public int getMax() {
        return mMax;
    }

    public interface LengthWatch {

        /**
         * edit长度达到MAX时调用此方法
         */
        void onReachMaxLength();
    }
}

