package com.sun.img.load;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author: Harper
 * @date: 2021/12/10
 * @note: 图片加载策略接口
 */
public interface IImageLoaderStrategy {

    //====常用====
    void loadImage(String url, ImageView imageView);

    void loadImage(int resourceId, ImageView imageView);

    void loadImage(String url, ImageView imageView, ImageLoadListener loadListener);

    void loadImage(String url, int width, int height, ImageView imageView, ImageLoadListener loadListener);

    void loadImage(String url, int placeholder, ImageView imageView);

    void loadImage(String url, int placeholder, int errorResource, ImageView imageView);

    //需要根据图片撑大imageView时使用
    void loadImageAutoFit(String url, int placeholder, int errorResource, ImageView imageView);

    //圆形图
    void loadCircleImage(String url, ImageView imageView);

    void loadCircleImage(int resourceId, ImageView imageView);

    void loadCircleImage(String url, int placeholder, ImageView imageView);

    void loadCircleImage(String url, int placeholder, int errorResource, ImageView imageView);

    //====进阶====
    void loadCircleBorderImage(String url, int placeholder, int errorResource, ImageView imageView, float borderWidth, int borderColor);

    void loadCircleBorderImage(String url, int placeholder, int errorResource, ImageView imageView, float borderWidth, int borderColor, int heightPx, int widthPx);

    void loadCornerImage(String url, ImageView imageView, int corner);

    //加载GIF
    void loadGifImage(String url, ImageView imageView);

    void loadGifImage(String url, int placeholder, ImageView imageView);

    void loadGifImage(String url, int placeholder, int errorDrawable, ImageView imageView);

    void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener);

    //====管理====
    //清除硬盘缓存
    void clearImageDiskCache(final Context context);

    //清除内存缓存
    void clearImageMemoryCache(Context context);

    //根据不同的内存状态，来响应不同的内存释放策略
    void trimMemory(Context context, int level);

    //获取缓存大小,已经转换为字符串了
    String getCacheSize(Context context);

    /**
     * 获取缓存大小，单位字节
     *
     * @param context
     * @return
     */
    long getCacheSizeByte(Context context);
}
