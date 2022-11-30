package com.sun.media.img.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sun.base.bean.MediaFile;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.ui.view.PinchImageView;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 大图浏览适配器（并不是比较好的方案，后期会用RecyclerView来实现）
 */
public class ImagePreViewAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<MediaFile> mMediaFileList;

    LinkedList<PinchImageView> viewCache = new LinkedList<>();

    public ImagePreViewAdapter(Context context, List<MediaFile> mediaFileList) {
        this.mContext = context;
        this.mMediaFileList = mediaFileList;
    }

    @Override
    public int getCount() {
        return mMediaFileList == null ? 0 : mMediaFileList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PinchImageView imageView;
        if (viewCache.size() > 0) {
            imageView = viewCache.remove();
            imageView.reset();
        } else {
            imageView = new PinchImageView(mContext);
        }
        try {
            ImageLoader.load().loadImage(mMediaFileList.get(position).getPath(), imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        PinchImageView imageView = (PinchImageView) object;
        container.removeView(imageView);
        viewCache.add(imageView);
    }
}
