package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.LogUtil;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityPickingPictureBinding;
import com.sun.demo2.view.dialog.BottomShareDialog;
/**
 * @author: Harper
 * @date: 2022/3/14
 * @note: 图片取色并融入背景色效果
 */
public class PickingPictureActivity extends BaseMvpActivity implements View.OnClickListener {

    private boolean isMethod1 = false;
    private long time;
    private ActivityPickingPictureBinding mBind;

    public static void start(Context context) {
        Intent intent = new Intent(context, PickingPictureActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_picking_picture;
    }

    @Override
    public void initView() {
        mBind = (ActivityPickingPictureBinding) mViewDataBinding;
        mBind.btnMethod1.setOnClickListener(this);
        mBind.btnMethod2.setOnClickListener(this);
        mBind.ivShare.setOnClickListener(v -> {
            new BottomShareDialog.Builder(mThis())
                    .setConfig("","","","")
                    .build().show();
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_method1:
                isMethod1 = true;
                pickColor();
                break;
            case R.id.btn_method2:
                isMethod1 = false;
                pickColor();
                break;
            default:
                break;
        }
    }

    private void pickColor() {
        Glide.with(this).asBitmap()
                .load(R.mipmap.bg_invert_image)
                .into(new SimpleTarget<Bitmap>(300, 300) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(palette -> {
                            if (palette == null) {
                                return;
                            }
                            if (palette.getDarkVibrantColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
                                createLinearGradientBitmap(palette.getDarkVibrantColor(Color.TRANSPARENT), palette.getVibrantColor(Color.TRANSPARENT));
                            } else if (palette.getDarkMutedColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
                                createLinearGradientBitmap(palette.getDarkMutedColor(Color.TRANSPARENT), palette.getMutedColor(Color.TRANSPARENT));
                            } else {
                                createLinearGradientBitmap(palette.getLightMutedColor(Color.TRANSPARENT), palette.getLightVibrantColor(Color.TRANSPARENT));
                            }
                        });

                        LogUtil.d("with=" + resource.getWidth() + "--height=" + resource.getHeight());
                        time = System.currentTimeMillis();
                        if (isMethod1) {
                            mBind.ivMain.setImageBitmap(getImageToChange(resource));
                        } else {
                            mBind.ivMain.setImageBitmap(handleBimap(resource));
                        }
                        time = System.currentTimeMillis() - time;
                        mBind.tvTime.setText("耗时: " + (time / 1000f) + "s");


                    }
                });
    }


    //创建线性渐变背景色
    private void createLinearGradientBitmap(int darkColor, int color) {
        int bgColors[] = new int[2];
        bgColors[0] = darkColor;
        bgColors[1] = color;

        Bitmap bgBitmap = Bitmap.createBitmap(mBind.ivBg.getWidth(), mBind.ivBg.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        canvas.setBitmap(bgBitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        LinearGradient gradient = new LinearGradient(0, 0, 0, bgBitmap.getHeight(), bgColors[0], bgColors[1], Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());
        canvas.drawRoundRect(rectF, 20, 20, paint);
        canvas.drawRect(rectF, paint);
        mBind.ivBg.setImageBitmap(bgBitmap);
    }


    //修改透明度
    private Bitmap getImageToChange(Bitmap mBitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        int mWidth = mBitmap.getWidth();
        int mHeight = mBitmap.getHeight();
        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                int color = mBitmap.getPixel(j, i);
                int g = Color.green(color);
                int r = Color.red(color);
                int b = Color.blue(color);
                int a = Color.alpha(color);
                //从中间部分开始透明渐变
                float index = i * 1.0f / mHeight;
                if (index > 0.5f) {
                    a = 255 - (int) (i / (mHeight / 2f) * 255);
                }
                color = Color.argb(a, r, g, b);
                createBitmap.setPixel(j, i, color);
            }
        }

        return createBitmap;
    }

    /**
     * 通过位移运算来做透渐变，相比之前的方法提高90倍左右
     *
     * @param bitmap
     * @return
     */
    private Bitmap handleBimap(Bitmap bitmap) {
        //透明渐变
        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        //循环开始的下标，设置从什么时候开始改变
        int start = argb.length / 2;
        int end = argb.length;

//        int mid = argb.length;
//        int row = ((mid - start) / bitmap.getHeight()) + 2;


        int width = bitmap.getWidth();
        for (int i = 0; i < bitmap.getHeight() / 2 + 1; i++) {
            for (int j = 0; j < width; j++) {
                int index = start - width + i * width + j;
                if (argb[index] != 0) {
                    argb[index] = ((int) ((1 - i / (bitmap.getHeight() / 2f)) * 255) << 24) | (argb[index] & 0x00FFFFFF);
                }
            }
        }
//        for (int i = mid; i < argb.length; i++) {
//            argb[i] = (argb[i] & 0x00FFFFFF);
//        }

        return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

    }


}