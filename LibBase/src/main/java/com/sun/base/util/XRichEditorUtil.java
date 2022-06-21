package com.sun.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sun.base.R;
import com.sun.base.widget.TransformationScale;
import com.sun.common.util.ScreenUtil;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note:
 */
public class XRichEditorUtil {
    //imageHeight == 0 始终为0 富文本编辑设定的
    public static void loadImage(final Context applicationContext, final String imagePath, final ImageView imageView, final int imageHeight, final View root, final boolean isLocalUpload) {
        //如果是网络图片
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            Glide.with(applicationContext).asBitmap().load(imagePath).dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(
                            new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (imageHeight > 0) {//固定高度
                                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                                FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
                                        lp.bottomMargin = 10;//图片的底边距
                                        imageView.setLayoutParams(lp);
                                        Glide.with(applicationContext).asBitmap().load(imagePath).centerCrop()
                                                .into(imageView);
                                    } else {
                                        //自适应高度
                                        Glide.with(applicationContext).asBitmap().load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .error(isLocalUpload ? R.mipmap.bg_default_upload_fail : R.mipmap.bg_default_load_fail)
//                                        .apply(new RequestOptions().override(200,200))
                                                .into(new TransformationScale(applicationContext, imageView, root, isLocalUpload));
                                    }
                                }

                                @Override
                                public void onLoadFailed(Drawable errorDrawable) {
                                    showFailPic(imageView, root, isLocalUpload);

                                }
                            }
                    );
        } else { //如果是本地图片
            if (imageHeight > 0) {//固定高度
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
                lp.bottomMargin = 10;//图片的底边距
                imageView.setLayoutParams(lp);

                Glide.with(applicationContext).asBitmap().load(imagePath).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.bg_upload_pic_fail).error(R.mipmap.bg_upload_pic_fail).into(imageView);
            } else {//自适应高度
                Glide.with(applicationContext).asBitmap().load(imagePath)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(isLocalUpload ? R.mipmap.bg_default_upload_fail : R.mipmap.bg_default_load_fail)
                        .into(new TransformationScale(applicationContext, imageView, root, isLocalUpload));

            }
        }
    }

    public static void showFailPic(ImageView imageView, View root, boolean isLocalUpload) {
        if (root instanceof RelativeLayout) {
            LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(194));
            if (!isLocalUpload) {
                llparams.bottomMargin = ScreenUtil.dp2px(17);
            }
            root.setLayoutParams(llparams);
            root.setBackgroundColor(Color.parseColor("#F2F2F2"));
        }
        if (imageView != null) {
            imageView.setImageResource(isLocalUpload ? R.mipmap.bg_upload_pic_fail : R.mipmap.bg_load_pic_fail);
        }
    }
}
