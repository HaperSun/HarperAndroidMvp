package com.sun.base.img;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.sun.base.util.FileUtil;
import com.sun.base.util.ImgLoaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * @author: Harper
 * @date:   2021/12/10
 * @note: 使用Glide的图片加载策略
 */
public class GlideImageLoaderStrategy implements IImageLoaderStrategy {

    @Override
    public void loadImage(String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
              .dontAnimate();
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadImage(String url, ImageView imageView, final ImageLoadListener loadListener) {
        loadListener.onLoadingStarted();
        RequestOptions requestOptions = new RequestOptions()
                .dontAnimate();
        Glide.with(imageView.getContext()).asBitmap()
                .load(url)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(requestOptions)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if(resource==null){
                            loadListener.onLoadingFailed(new NullPointerException());
                        }else {
                            loadListener.onLoadingComplete(resource);
                        }
                    }
                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        loadListener.onLoadingFailed(new NullPointerException());
                    }
                });
    }

    @Override
    public void loadImage(String url, int width, int height, ImageView imageView,final ImageLoadListener loadListener) {
        loadListener.onLoadingStarted();
        RequestOptions requestOptions = new RequestOptions()
                .dontAnimate();
        Glide.with(imageView.getContext()).asBitmap()
                .load(url)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(requestOptions)
                .into(new SimpleTarget<Bitmap>(width,height) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if(resource==null){
                            loadListener.onLoadingFailed(new NullPointerException());
                        }else {
                            loadListener.onLoadingComplete(resource);
                        }
                    }
                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        loadListener.onLoadingFailed(new NullPointerException());
                    }
                });
    }

    @Override
    public void loadImage(String url, int placeholder, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder).dontAnimate();
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadImage(int resourceId, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(resourceId)
                .into(imageView);
    }

    @Override
    public void loadImage(String url, int placeholder, int errorResource, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder)
                .error(errorResource)
                .dontAnimate();
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    @Override
    public void loadImageAutoFit(String url, int placeholder, int errorResource, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder)
                .error(errorResource) .dontAnimate();
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)) //这里用result会导致显示的是缩略图大小而非原始图片
                .into(imageView);
    }

    @Override
    public void loadCircleImage(String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate().transform(new GlideCircleTransform(imageView.getContext()));
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadCircleImage(int resourceId, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate().transform(new GlideCircleTransform(imageView.getContext()));
        Glide.with(imageView.getContext())
                .load(resourceId)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadCircleImage(String url, int placeholder, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder).transform(new GlideCircleTransform(imageView.getContext()));
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadCircleImage(String url, int placeholder, int errorResource, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().error(errorResource).dontAnimate().transform(new GlideCircleTransform(imageView.getContext()))
                .placeholder(placeholder);
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadCornerImage(String url, ImageView imageView, int corner) {
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(corner));
        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadCircleBorderImage(String url, int placeholder, int errorResource, ImageView imageView, float borderWidth, int borderColor) {
        RequestOptions requestOptions = new RequestOptions().error(errorResource).dontAnimate()
                .transform(new GlideCircleTransform(imageView.getContext(), borderWidth, borderColor))
                .placeholder(placeholder);
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadCircleBorderImage(String url, int placeholder, int errorResource, ImageView imageView, float borderWidth, int borderColor, int heightPx, int widthPx) {
        RequestOptions requestOptions = new RequestOptions().error(errorResource).dontAnimate()
                .transform(new GlideCircleTransform(imageView.getContext(), borderWidth, borderColor, heightPx, widthPx))
                .placeholder(placeholder);
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadGifImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void loadGifImage(String url, int placeholder, ImageView imageView) {

        RequestOptions requestOptions = new RequestOptions()
                .dontAnimate().placeholder(placeholder);
        Glide.with(imageView.getContext()).asGif()
                .load(url)
                .apply(requestOptions)
                /*.skipMemoryCache(true)*/
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    @Override
    public void loadGifImage(String url, int placeholder, int errorDrawable, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder)
                .error(errorDrawable).dontAnimate();
        Glide.with(imageView.getContext()).asGif().apply(requestOptions)
                .load(url)
                /*.skipMemoryCache(true)*/
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        if (!ImgLoaderUtils.isSDCardExsit() || TextUtils.isEmpty(url)) {
            listener.onSaveFail();
            return;
        }
        InputStream fromStream = null;
        OutputStream toStream = null;
        try {
            File cacheFile = Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            if (cacheFile == null || !cacheFile.exists()) {
                listener.onSaveFail();
                return;
            }
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, saveFileName + ImgLoaderUtils.getPicType(cacheFile.getAbsolutePath()));

            fromStream = new FileInputStream(cacheFile);
            toStream = new FileOutputStream(file);
            byte length[] = new byte[1024];
            int count;
            while ((count = fromStream.read(length)) > 0) {
                toStream.write(length, 0, count);
            }
            //用广播通知相册进行更新相册
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            listener.onSaveSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onSaveFail();
        } finally {
            FileUtil.close(fromStream);
            FileUtil.close(toStream);
        }

    }


    @Override
    public void clearImageDiskCache(final Context context) {
        try {
            //只能在主线程执行
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }

    @Override
    public String getCacheSize(Context context) {
        try {
            return ImgLoaderUtils.getFormatSize(ImgLoaderUtils.getFolderSize(Glide.getPhotoCacheDir(context.getApplicationContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public long getCacheSizeByte(Context context) {
        try {
            return ImgLoaderUtils.getFolderSize(Glide.getPhotoCacheDir(context.getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
