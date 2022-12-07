package com.sun.demo2.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.animation.TranslateAnimation;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityInvertedImageBinding;

/**
 * @author: Harper
 * @date: 2022/3/14
 * @note: 图片倒影处理
 */
public class InvertedImageActivity extends BaseMvpActivity<ActivityInvertedImageBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, InvertedImageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_inverted_image;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        vdb.ivInvert.setImageBitmap(getReverseBitmapById(this, R.mipmap.bg_invert_image, 0.5f));
        TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 100);
        vdb.ivInvert.setAnimation(animation);
    }

    /**
     * @param context 上下文
     * @param resId   图片id
     * @param percent 倒影的深度 0~1f
     * @return Bitmap
     */
    public Bitmap getReverseBitmapById(Context context, int resId, float percent) {
        // 获取原始位图
        Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), resId);

        // 运用Matrix类反转像素
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);

        //创建倒影位图
        Bitmap rvsBitmap = Bitmap.createBitmap(srcBitmap, 0, (int) (srcBitmap.getHeight() * (1 - percent)),
                srcBitmap.getWidth(), (int) (srcBitmap.getHeight() * percent), matrix, false);

        // 根据上面原始位图和倒影位图高度+相隔20的高度创建新位图
        Bitmap comBitmap = Bitmap.createBitmap(srcBitmap.getWidth(),
                srcBitmap.getHeight() + rvsBitmap.getHeight() + 20, srcBitmap.getConfig());

        //绘制出原始位图和倒影位图
        Canvas gCanvas = new Canvas(comBitmap);
        gCanvas.drawBitmap(srcBitmap, 0, 0, null);
        gCanvas.drawBitmap(rvsBitmap, 0, srcBitmap.getHeight() + 20, null);


        Paint paint = new Paint();

        //LinearGradient,我们可以将之译为线型渐变、线型渲染等
        //Shader.TileMode.CLAMP,这种模式表示重复最后一种颜色直到该View结束的地方
        LinearGradient shader = new LinearGradient(0, srcBitmap.getHeight() + 20, 0, comBitmap.getHeight(),
                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        //setXfermode图像混合
        //DST_IN为显示上方覆盖内容
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        gCanvas.drawRect(0, srcBitmap.getHeight() + 20, srcBitmap.getWidth(), comBitmap.getHeight(), paint);
        return comBitmap;
    }
}