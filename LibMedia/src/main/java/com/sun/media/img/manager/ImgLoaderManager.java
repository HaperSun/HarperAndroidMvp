package com.sun.media.img.manager;

import android.util.Log;

import androidx.annotation.IntDef;

import com.sun.media.img.GlideImageLoaderStrategy;
import com.sun.media.img.i.IImageLoaderStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: Harper
 * @date: 2021/12/10
 * @note: ImgLoader加载策略管理
 * 当前只封装了Glide的加载策略
 */
public class ImgLoaderManager {

    //加载策略-Glide
    public final static int STRATEGY_GLIDE = 1;
    //加载策略-ImageLoader
    public final static int STRATEGY_IMAGE_LOADER = 2;
    //默认策略
    public final static int STRATEGY_DEFAULT = STRATEGY_GLIDE;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STRATEGY_GLIDE, STRATEGY_IMAGE_LOADER})
    public @interface StrategyType {
    }

    /**
     * 根据参数选择策略模式
     *
     * @param strategyType 具体策略类型
     */
    public static IImageLoaderStrategy getImgLoaderStrategy(@StrategyType int strategyType) {
        if (strategyType == STRATEGY_GLIDE) {
            return new GlideImageLoaderStrategy();
        }
        Log.e("ImgLoader", "暂不支持更多类型选择，返回默认Strategy");
        return new GlideImageLoaderStrategy();
    }
}
