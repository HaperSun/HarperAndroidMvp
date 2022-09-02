package com.sun.base.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @author: Harper
 * @date: 2022/8/26
 * @note: 高度自适应的ImageView
 */
public class ResizableWidthImageView extends androidx.appcompat.widget.AppCompatImageView {

    public ResizableWidthImageView(Context context) {
        super(context);
    }

    public ResizableWidthImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableWidthImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable d = getDrawable();
            if (d != null) {
                // ceil not round - avoid thin vertical gaps along the left/right edges
                int height = MeasureSpec.getSize(heightMeasureSpec);
                // 考虑设置了最大宽度的情况
                int maxHeight = getMaxHeight();
                height = Math.min(maxHeight, height);
                //高度根据使得图片的宽度充满屏幕计算而得
                int width = (int) Math.ceil((float) height * (float) d.getIntrinsicWidth() / (float) d.getIntrinsicHeight());
                setMeasuredDimension(width, height);
                if (mCallBack != null) {
                    mCallBack.onHeightMeasured(height);
                }
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCallBack != null) {
            mCallBack.onDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void onHeightMeasured(int measuredHeight);

        void onDetachedFromWindow();
    }
}
