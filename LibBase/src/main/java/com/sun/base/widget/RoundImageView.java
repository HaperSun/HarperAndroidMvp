package com.sun.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.sun.base.R;


/**
 * @author: Harper
 * @date: 2022/10/20
 * @note: 圆角 ImageView
 */
public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {

    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;
    public static final int BORDER_RADIUS_DEFAULT = 10;
    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";
    // 显示类型
    private int type;
    // 圆角大小
    private int mBorderRadius;
    private int mBorderWidth;
    private int mBorderColor;
    // 绘图的画笔
    private Paint mBitmapPaint;
    // 圆角半径
    private int mRadius;
    private Matrix mMatrix;
    // view的宽度
    private int mWidth;
    private RectF mRoundRectF;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
    }

    private void initAttr(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        // 默认为10dp
        mBorderRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BORDER_RADIUS_DEFAULT,
                        getResources().getDisplayMetrics()));
        // 默认为圆形
        type = typedArray.getInt(R.styleable.RoundImageView_shapeType, TYPE_CIRCLE);
        mBorderColor = typedArray.getColor(R.styleable.RoundImageView_borderColor, Color.WHITE);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_borderWidth, 0);
        typedArray.recycle();
    }

    private int wMode = MeasureSpec.UNSPECIFIED;
    private int hMode = MeasureSpec.UNSPECIFIED;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        wMode = MeasureSpec.getMode(widthMeasureSpec);
        hMode = MeasureSpec.getMode(heightMeasureSpec);
        // 类型为圆形时，我们强制让view的宽和高一致
        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setUpShader();
        if (type == TYPE_ROUND) {
            canvas.drawRoundRect(mRoundRectF, mBorderRadius, mBorderRadius, mBitmapPaint);
        } else if (type == TYPE_CIRCLE) {
            canvas.drawCircle(mRadius, mRadius, mRadius - mBorderWidth, mBitmapPaint);
            if (mBorderWidth > 0) {
                // 绘制边框
                Paint paint = new Paint();
                paint.setColor(mBorderColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAntiAlias(true);
                paint.setStrokeWidth(mBorderWidth);
                canvas.drawCircle(mRadius, mRadius, mWidth / 2 - mBorderWidth, paint);
            }
        }
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int sideLength = Math.min(availableWidth, availableHeight);
        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;
        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (type == TYPE_ROUND) {
            mRoundRectF = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        if (bitmap == null) {
            return;
        }
        // 将bitmap作为着色器，就是在指定区域内绘制bitmap
        // 渲染图像，使用图像为绘制图形着色
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE) {
            // 拿到bitmap宽高中的最小值
            int bSize = Math.min(bitmap.getHeight(), bitmap.getWidth());
            scale = mWidth * 1.0f / bSize;
        } else if (type == TYPE_ROUND) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；
            // 缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里缩放的最大值；
           /* // 如果宽高是WRAP_CONTENT ==> MeasureSpec.AT_MOST 则不用缩放
            if (!(wMode == MeasureSpec.AT_MOST && hMode == MeasureSpec.AT_MOST)) {

            }*/
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(),
                    getHeight() * 1.0f / bitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        bitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(bitmapShader);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state)
                    .getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setBorderRadius(int borderRadius) {
        int pxVal = dp2px(borderRadius);
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE) {
                this.type = TYPE_CIRCLE;
            }
            requestLayout();
        }
    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
