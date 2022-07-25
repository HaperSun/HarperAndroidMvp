package com.sun.media.img.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author: Harper
 * @date: 2022/7/7
 * @note: 解决GridView和ScollView冲突问题
 */
public class AllSizeGridView extends GridView {

    private boolean mHasScrollbar = false;

    public AllSizeGridView(Context context) {
        super(context);
    }

    public AllSizeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AllSizeGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
     * @param hasScrollbar hasScrollbar
     */
    public void setHasScrollbar(boolean hasScrollbar) {
        this.mHasScrollbar = hasScrollbar;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHasScrollbar == false) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
