package com.sun.demo2.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.sun.base.util.ScreenUtil;
import com.sun.demo2.R;


/**
 * @author: Harper
 * @date: 2022/6/20
 * @note: 索引条控件, 默认是"#ABCDEFGHIJKLMNOPQRSTUVWXYZ",可以调用updateLettersData方法来跟新索引表内容
 * 注意的是要等视图显示之后才能调用这个方法
 */
public class FastIndexBar extends View {

    private String letters;
    private char[] CHARS_ARR;
    private Paint whitePaint;
    private Paint grayPaint;
    private Rect textRect;
    private float CELL_HEIGHT;
    private int CELL_WIDTH;
    private int TOP_MARGIN;
    private int TOTAL_HEIGHT;

    //对外改变数据借口
    public void setLettersData(String letters) {
        this.letters = letters;
        invalidate();
    }

    public interface OnCharSelectedListener {
        void onCharSelected(char c);
    }
    OnCharSelectedListener ocsl;


    public void setOnCharSelectedListener(OnCharSelectedListener ocsl) {
        this.ocsl = ocsl;
    }

    public FastIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FastIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastIndexBar(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        // 开启反锯齿
        //默認顏色
        whitePaint = getTextPaint(ContextCompat.getColor(context,R.color.blue));
        //選中顏色
        grayPaint = getTextPaint(Color.GRAY);
        textRect = new Rect();
    }

    private Paint getTextPaint(int color) {
        Paint result = new Paint(Paint.ANTI_ALIAS_FLAG);
        result.setColor(color);
        result.setTextSize(ScreenUtil.isTabletDevice() ? ScreenUtil.dp2px(22) : ScreenUtil.dp2px(16));
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == letters || letters.length() <= 0) {
            return;
        }
        initValue();
        float textX;
        float textY;
        Paint textPaint;
        for (int i = 0; i < CHARS_ARR.length; i++) {
            if (i == touchIndex) {
                //按住的是gray色
                textPaint = grayPaint;
            } else {
                //默認的是53
                textPaint = whitePaint;
            }
            textPaint.getTextBounds(CHARS_ARR, i, 1, textRect);
            //x的值是控件的宽度的一半减去字体边界的一半
            textX = CELL_WIDTH / 2 - textRect.width() / 2;
            //y值是CELL_HEIGHT的n倍加上CELL_HEIGHT一半和字体边界的一半,再加上距离顶部的间隔
            textY = CELL_HEIGHT / 2 + textRect.height() / 2 + i * CELL_HEIGHT + TOP_MARGIN;
            canvas.drawText(CHARS_ARR, i, 1, textX, textY, textPaint);
        }
    }

    //得到控件的宽度和每一个cell的高度
    private void initValue() {
        CHARS_ARR = letters.toCharArray();
        CELL_HEIGHT = ScreenUtil.isTabletDevice() ? ScreenUtil.dp2px(24) : ScreenUtil.dp2px(18);
        CELL_WIDTH = getWidth();
        TOTAL_HEIGHT = (int) (CHARS_ARR.length * CELL_HEIGHT);
        //顶部距离
        TOP_MARGIN = (getHeight() - TOTAL_HEIGHT) / 2;
    }
    // 初始值应该是<0
    int touchIndex = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                try {
                    int touchY = (int) event.getY();
                    //针对触摸区域进行处理
                    if (touchY >= TOP_MARGIN && touchY <= (TOP_MARGIN + TOTAL_HEIGHT)) {
                        updateTouchIndex((int) ((touchY - TOP_MARGIN) / CELL_HEIGHT));
                    } else if (touchY < TOP_MARGIN) {
                        updateTouchIndex(0);
                    } else {
                        if (CHARS_ARR != null && CHARS_ARR.length > 0) {
                            updateTouchIndex(CHARS_ARR.length - 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
                updateTouchIndex(-1);
                break;
        }
        return true;
    }

    private void updateTouchIndex(int index) {
        if (touchIndex == index) {
            return;
        }
        touchIndex = index;
        //重绘,改变颜色
        invalidate();
        if (ocsl != null) {
            if (touchIndex >= 0 && touchIndex < CHARS_ARR.length) {
                ocsl.onCharSelected(CHARS_ARR[touchIndex]);
            }
        }
    }
}