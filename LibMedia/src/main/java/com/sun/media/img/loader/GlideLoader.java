package com.sun.media.img.loader;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.sun.media.R;
import com.sun.media.img.i.IImgLoader;


/**
 * @author: Harper
 * @date: 2022/7/21
 * @note: 实现自定义图片加载
 */
public class GlideLoader implements IImgLoader {

    private RequestOptions mOptions = new RequestOptions()
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(R.mipmap.icon_image_default);
//            .error(R.mipmap.icon_image_error);

    private RequestOptions mPreOptions = new RequestOptions()
            .skipMemoryCache(true);
//            .error(R.mipmap.icon_image_error);

    @Override
    public void loadImage(ImageView imageView, String imagePath) {
        //小图加载
        Glide.with(imageView.getContext()).load(imagePath).apply(mOptions).into(imageView);
    }

    @Override
    public void loadPreImage(ImageView imageView, String imagePath) {
        //大图加载
        Glide.with(imageView.getContext()).load(imagePath).apply(mPreOptions).into(imageView);

    }

    @Override
    public void clearMemoryCache() {
        //清理缓存
    }
}
