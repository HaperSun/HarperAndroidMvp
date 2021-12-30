package com.sun.base.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.sun.base.util.LogUtil;
import com.sun.base.util.XRichEditorUtil;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: Glide加载图片时，根据图片宽度等比缩放
 */
public class TransformationScale extends ImageViewTarget<Bitmap> {

    private ImageView target;
    private View root;
    private boolean isLocalUpload;
    private Context context;

    public TransformationScale(ImageView target) {
        super(target);
        this.target = target;
    }

    public TransformationScale(ImageView target, View root) {
        super(target);
        this.target = target;
        this.root = root;
    }

    public TransformationScale(Context context, ImageView target, View root, boolean isLocalUpload) {
        super(target);
        this.context = context;
        this.target = target;
        this.root = root;
        this.isLocalUpload = isLocalUpload;
    }

    @Override
    protected void setResource(Bitmap resource) {
        if (resource != null) {
            //获取原图的宽高
            int width = resource.getWidth();
            int height = resource.getHeight();
            //获取imageView的宽
            int imageViewWidth = target.getWidth();

            //计算缩放比例
            float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

            //计算图片等比例放大后的高
            int imageHeight = (int) (height * sy);
            //固定图片高度，记得设置裁剪剧中
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight);

            if (root instanceof RelativeLayout) {
                LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, imageHeight);
                root.setLayoutParams(llparams);
            }
            if (!isLocalUpload) {
                params.bottomMargin = 34;
            }
            target.setScaleType(ImageView.ScaleType.CENTER_CROP);
            target.setLayoutParams(params);
            view.setImageBitmap(changeBitmapSize(resource, imageViewWidth, imageHeight));
        } else {
            XRichEditorUtil.showFailPic(context, target, root, isLocalUpload);
        }
    }


    private Bitmap changeBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        LogUtil.e("width", "width:" + width);
        LogUtil.e("height", "height:" + height);
        //设置想要的大小
//        int newWidth=30;
//        int newHeight=30;

        //计算压缩的比率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        //获取新的bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.getWidth();
        bitmap.getHeight();
        LogUtil.e("newWidth", "newWidth: " + bitmap.getWidth());
        LogUtil.e("newHeight", "newHeight: " + bitmap.getHeight());
        return bitmap;
    }
}
