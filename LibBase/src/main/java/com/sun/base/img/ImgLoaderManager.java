package com.sun.base.img;

import android.util.Log;

import androidx.annotation.IntDef;

/**
 * @author: Harper
 * @date:   2021/12/10
 * @note: ImgLoader配置文件
 */
public class ImgLoaderManager {

    //加载策略-Glide
    public final static int STRATEGY_GLIDE = 1;
    //加载策略-ImageLoader
    public final static int STRATEGY_IMAGE_LOADER = 2;
    //默认策略
    public final static int STRATEGY_DEFAULT = STRATEGY_GLIDE;

    @IntDef({STRATEGY_GLIDE, STRATEGY_IMAGE_LOADER})
    public @interface StrategyType {}

    /**
     * 根据参数选择策略模式
     * @param strategyType 具体策略类型
     */
    public static IImageLoaderStrategy getImgLoaderStrategy(@StrategyType int strategyType) {
        switch (strategyType) {
            case STRATEGY_GLIDE:
                return new GlideImageLoaderStrategy();
            default:
                Log.e("ImgLoader", "暂不支持更多类型选择，返回默认Strategy");
                return new GlideImageLoaderStrategy();
        }
    }
}
