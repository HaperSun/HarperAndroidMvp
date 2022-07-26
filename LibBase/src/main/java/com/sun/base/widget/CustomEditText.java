package com.sun.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

import com.sun.base.filter.CharsFilter;
import com.sun.base.filter.EmojiFilter;
import com.sun.base.filter.MeetingTextLengthFilter;
import com.sun.base.filter.NotEmptyFilter;
import com.sun.base.filter.SpecialFilter;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 过滤emoji的edittext定制类
 */
@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEmojiFilters() {
        setFilters(new InputFilter[]{new EmojiFilter()});
    }

    /**
     * 过滤掉"/" and ":" 两个特殊字符,emoji
     */
    public void setSpecialCharacterFilters() {
        setFilters(new InputFilter[]{new SpecialFilter(), new EmojiFilter(), new InputFilter.LengthFilter(16)});

    }

    public void setSpecialCharacterFilters20() {
        setFilters(new InputFilter[]{new CharsFilter(), new EmojiFilter(), new InputFilter.LengthFilter(20)});
    }

    public void setMeetingSpecialCharacterFilters20() {
        setFilters(new InputFilter[]{new MeetingTextLengthFilter(), new InputFilter.LengthFilter(20)});
    }

    public void setSpecialCharacterFilters(InputFilter[] filters) {
        setFilters(filters);
    }

    public void setMaxFilters(int maxLength) {
        setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter.LengthFilter(maxLength)});
    }

    public void setEmptyFilters(int maxLength) {
        setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter
                .LengthFilter(maxLength), new NotEmptyFilter()});
    }

    public void setNoEmptyFilters() {
        setFilters(new InputFilter[]{new EmojiFilter(), new NotEmptyFilter()});
    }

}
