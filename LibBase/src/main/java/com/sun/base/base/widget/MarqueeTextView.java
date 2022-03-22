package com.sun.base.base.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 走马灯TextView
 */
public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 跑马灯效果
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //            // ...结束
//            setEllipsize(TextUtils.TruncateAt.END);
        setMarqueeRepeatLimit(-1);
        setSingleLine(true);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

} 