package com.sun.media.img;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author: Harper
 * @date: 2021/12/10
 * @note: 图片加载实体
 */
public class ImgLoader {

    private IImageLoaderStrategy mStrategy;
    private static volatile ImgLoader mInstance = null;

    public ImgLoader() {
    }

    /**
     * 单例模式的最佳实现：内存占用低，效率高，线程安全
     *
     * @return ImageLoader
     */
    public static ImgLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImgLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImgLoader();
                }
            }
        }
        return mInstance;
    }

    public void setStrategy() {
        mStrategy = ImgLoaderManager.getImgLoaderStrategy(ImgLoaderManager.STRATEGY_DEFAULT);
    }

    /**
     * 加载图片
     *
     * @param url       图片地址
     * @param imageView 目标容器
     */
    public void loadImage(String url, ImageView imageView) {
        mStrategy.loadImage(url, imageView);
    }

    /**
     * 加载视频，并截取0.01秒的图片做封面
     *
     * @param url       图片地址
     * @param imageView 目标容器
     */
    public void loadVideo(String url, ImageView imageView) {
        mStrategy.loadVideo(url, imageView);
    }

    /**
     * 加载图片
     *
     * @param url          图片地址
     * @param imageView    目标容器
     * @param loadListener 图片加载回调监听
     */
    public void loadImage(String url, ImageView imageView, ImageLoadListener loadListener) {
        mStrategy.loadImage(url, imageView, loadListener);
    }

    /**
     * 加载图片
     *
     * @param url          图片地址
     * @param imageView    目标容器
     * @param loadListener 图片加载回调监听
     */
    public void loadImage(String url, int width, int height, ImageView imageView, ImageLoadListener loadListener) {
        mStrategy.loadImage(url, width, height, imageView, loadListener);
    }

    /**
     * 加载图片
     *
     * @param url         图片地址
     * @param placeholder 占位图
     * @param imageView   目标容器
     */
    public void loadImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadImage(url, placeholder, imageView);
    }

    /**
     * 加载图片
     *
     * @param url           图片地址
     * @param placeholder   占位图
     * @param errorDrawable 失败展示图片
     * @param imageView     目标容器
     */
    public void loadImage(String url, int placeholder, int errorDrawable, ImageView imageView) {
        mStrategy.loadImage(url, placeholder, errorDrawable, imageView);
    }

    /**
     * 加载图片
     *
     * @param resourceId 本地图片id
     * @param imageView  目标容器
     */
    public void loadImage(int resourceId, ImageView imageView) {
        mStrategy.loadImage(resourceId, imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param resourceId 本地图片id
     * @param imageView  目标容器
     */
    public void loadCircleImage(int resourceId, ImageView imageView) {
        mStrategy.loadCircleImage(resourceId, imageView);
    }

    /**
     * 加载图片
     *
     * @param url           图片地址
     * @param placeholder   占位图
     * @param errorResource 失败展示图片
     * @param imageView     目标容器
     * @param autoFit       容器是否要根据内容自适应
     */
    public void loadImage(String url, int placeholder, int errorResource, ImageView imageView, boolean autoFit) {
        if (autoFit) {
            mStrategy.loadImageAutoFit(url, placeholder, errorResource, imageView);
        } else {
            mStrategy.loadImage(url, placeholder, errorResource, imageView);
        }
    }

    /**
     * 加载圆形图片
     *
     * @param url       图片地址
     * @param imageView 目标容器
     */
    public void loadCircleImage(String url, ImageView imageView) {
        mStrategy.loadCircleImage(url, imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param url         图片地址
     * @param placeholder 占位图
     * @param imageView   目标容器
     */
    public void loadCircleImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadCircleImage(url, placeholder, imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param url           图片地址
     * @param placeholder   占位图
     * @param errorResource 失败展示图片
     * @param imageView     目标容器
     */
    public void loadCircleImage(String url, int placeholder, int errorResource, ImageView imageView) {
        mStrategy.loadCircleImage(url, placeholder, errorResource, imageView);
    }

    /**
     * 加载圆形带边框的图片 - 只提供完整参数版本，没有占位或错误图的自行传0
     *
     * @param url           图片地址
     * @param placeholder   占位图
     * @param errorResource 失败展示图片
     * @param imageView     目标容器
     * @param borderWidth   边框宽度
     * @param borderColor   边框颜色
     */
    public void loadCircleBorderImage(String url, int placeholder, int errorResource, ImageView imageView, float borderWidth, int borderColor) {
        mStrategy.loadCircleBorderImage(url, placeholder, errorResource, imageView, borderWidth, borderColor);
    }

    /**
     * 带偏移的加载圆形带边框的图片 - 只提供完整参数版本，没有占位或错误图的自行传0
     *
     * @param url           图片地址
     * @param placeholder   占位图
     * @param errorResource 失败展示图片
     * @param imageView     目标容器
     * @param borderWidth   边框宽度
     * @param borderColor   边框颜色
     * @param heightPX      偏移y
     * @param widthPX       偏移x
     */
    public void loadCircleBorderImage(String url, int placeholder, int errorResource, ImageView imageView, float borderWidth, int borderColor, int heightPX, int widthPX) {
        mStrategy.loadCircleBorderImage(url, placeholder, errorResource, imageView, borderWidth, borderColor, heightPX, widthPX);
    }

    /**
     * 加载圆形图片
     *
     * @param url       图片地址
     * @param imageView 目标容器
     * @param corner    圆角
     */
    public void loadCornerImage(String url, ImageView imageView, int corner) {
        mStrategy.loadRectangleImage(url, imageView, corner);
    }

    /**
     * 加载Gif图片
     *
     * @param url       地址
     * @param imageView 目标容器
     */
    public void loadGifImage(String url, ImageView imageView) {
        mStrategy.loadGifImage(url, imageView);
    }

    /**
     * 加载Gif图片
     *
     * @param url         地址
     * @param placeholder 占位图
     * @param imageView   目标容器
     */
    public void loadGifImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadGifImage(url, placeholder, imageView);
    }

    /**
     * 加载Gif图片
     *
     * @param url           地址
     * @param placeholder   占位图
     * @param errorResource 错误图
     * @param imageView     目标容器
     */
    public void loadGifImage(String url, int placeholder, int errorResource, ImageView imageView) {
        mStrategy.loadGifImage(url, placeholder, errorResource, imageView);
    }

    /**
     * 保存图片
     *
     * @param context      上下文
     * @param url          地址
     * @param savePath     保存路径
     * @param saveFileName 保存名称
     * @param listener     ImageSaveListener监听器
     */
    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        mStrategy.saveImage(context, url, savePath, saveFileName, listener);
    }

    /**
     * 根据参数选择策略
     *
     * @param strategyType 具体策略类型
     */
    public void setLoadImgStrategy(@ImgLoaderManager.StrategyType int strategyType) {
        mStrategy = ImgLoaderManager.getImgLoaderStrategy(strategyType);
    }

    /**
     * 清除图片磁盘缓存
     *
     * @param context 上下文
     */
    public void clearImageDiskCache(final Context context) {
        mStrategy.clearImageDiskCache(context);
    }

    /**
     * 清除图片内存缓存
     *
     * @param context 上下文
     */
    public void clearImageMemoryCache(Context context) {
        mStrategy.clearImageMemoryCache(context);
    }

    /**
     * 根据不同的内存状态，来响应不同的内存释放策略
     *
     * @param context 上下文
     * @param level   内存登记
     */
    public void trimMemory(Context context, int level) {
        mStrategy.trimMemory(context, level);
    }

    /**
     * 清除图片所有缓存
     *
     * @param context 上下文
     */
    public void clearImageAllCache(Context context) {
        clearImageDiskCache(context.getApplicationContext());
        clearImageMemoryCache(context.getApplicationContext());
    }

    /**
     * 获取缓存大小
     *
     * @return CacheSize 缓存大小字符串，会自动转换单位
     */
    public String getCacheSize(Context context) {
        return mStrategy.getCacheSize(context);
    }

    /**
     * 获取缓存大小，单位字节
     *
     * @param context
     * @return
     */
    public long getCacheSizeByte(Context context) {
        return mStrategy.getCacheSizeByte(context);
    }
}
