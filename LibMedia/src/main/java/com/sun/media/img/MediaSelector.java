package com.sun.media.img;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.sun.media.camera.CameraActivity;
import com.sun.media.img.manager.MediaConfig;

/**
 * @author Harper
 * @date 2022/7/27
 * note:
 */
public class MediaSelector {

    private static volatile MediaSelector mMediaSelector;
    private FragmentActivity fragmentActivity;
    private Fragment fragment;
    private FragmentManager manager;
    public MediaConfig config;


    public static MediaSelector getInstance() {
        if (mMediaSelector == null) {
            synchronized (MediaSelector.class) {
                if (mMediaSelector == null) {
                    mMediaSelector = new MediaSelector();
                }
            }
        }
        return mMediaSelector;
    }

    public static Builder builder(FragmentActivity fragmentActivity) {
        return new Builder(fragmentActivity);
    }

    public static Builder builder(Fragment fragment) {
        return new Builder(fragment);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void setConfig(MediaConfig config) {
        this.config = config;
    }

    public void show() {
        Context context = null;
        if (fragmentActivity != null) {
            context = fragmentActivity;
        } else if (fragment != null) {
            context = fragment.getContext();
        }
        switch (config.selectType) {
            case MediaConfig.TAKE_PHOTO:
            case MediaConfig.TAKE_VIDEO:
            case MediaConfig.TAKE_BOTH:
                if (context != null) {
                    CameraActivity.start(context, config.selectType);
                }
                break;
            case MediaConfig.PHOTO_ALBUM:
                break;
            case MediaConfig.BOTTOM_DIALOG:
                break;
            default:
                break;
        }
    }

    public static class Builder {

        private FragmentManager manager;
        private Fragment fragment;
        private FragmentActivity fragmentActivity;
        //前置摄像头拍摄是否启用镜像 默认开启
        private boolean isMirror;
        //最大原图大小 单位MB
        private int maxOriginalSize;
        //最大选择数目
        private int maxCount;
        //剩余选择数目
        private int remainderCount;
        //最大图片宽度
        private int maxWidth;
        //最大图片高度
        private int maxHeight;
        //单位：MB , 默认15MB
        private int maxImageSize;
        //视频时长，单位：毫秒 ,默认30秒
        private int maxVideoLength;
        //视频大小，单位：MB
        private int maxVideoSize;
        //操作类型
        private int selectType;
        //最大选择视频数
        private int maxVideoCount;
        //在本地显示，不用上传到网络
        public boolean showLocal;
        //可以删除
        public boolean showDelete;

        public Builder() {
            setDefault();
        }

        private Builder(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
            this.manager = fragmentActivity.getSupportFragmentManager();
            setDefault();
        }

        private Builder(Fragment fragment) {
            this.fragment = fragment;
            this.manager = fragment.getChildFragmentManager();
            setDefault();
        }

        /**
         * 设置默认值
         */
        private void setDefault() {
            isMirror = true;
            maxOriginalSize = 15;
            maxCount = 9;
            maxWidth = 1920;
            maxHeight = 1920;
            maxImageSize = 15;
            maxVideoLength = 30000;
            maxVideoSize = 20;
            selectType = MediaConfig.TAKE_PHOTO;
            maxVideoCount = 1;
            showLocal = false;
            showDelete = true;
        }

        public Builder isMirror(boolean isMirror) {
            this.isMirror = isMirror;
            return this;
        }

        public Builder maxOriginalSize(int maxOriginalSize) {
            this.maxOriginalSize = maxOriginalSize;
            return this;
        }

        public Builder maxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        public Builder remainderCount(int remainderCount) {
            this.remainderCount = remainderCount;
            return this;
        }

        public Builder maxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public Builder maxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder maxImageSize(int maxImageSize) {
            this.maxImageSize = maxImageSize;
            return this;
        }

        public Builder maxVideoLength(int maxVideoLength) {
            this.maxVideoLength = maxVideoLength;
            return this;
        }

        public Builder maxVideoSize(int maxVideoSize) {
            this.maxVideoSize = maxVideoSize;
            return this;
        }

        public Builder selectType(int selectType) {
            this.selectType = selectType;
            return this;
        }

        public Builder maxVideoCount(int maxVideoCount) {
            this.maxVideoCount = maxVideoCount;
            return this;
        }

        public Builder showLocal(boolean showLocal) {
            this.showLocal = showLocal;
            return this;
        }

        public Builder showDelete(boolean showDelete) {
            this.showDelete = showDelete;
            return this;
        }

        public MediaSelector build() {
            MediaSelector selector = MediaSelector.getInstance();
            MediaConfig config = new MediaConfig();
            selector.fragmentActivity = fragmentActivity;
            selector.fragment = fragment;
            selector.manager = manager;
            config.isMirror = isMirror;
            config.maxOriginalSize = maxOriginalSize;
            config.maxCount = maxCount;
            config.remainderCount = remainderCount;
            config.maxWidth = maxWidth;
            config.maxHeight = maxHeight;
            config.maxImageSize = maxImageSize;
            config.maxVideoLength = maxVideoLength;
            config.maxVideoSize = maxVideoSize;
            config.selectType = selectType;
            config.maxVideoCount = maxVideoCount;
            config.showLocal = showLocal;
            config.showDelete = showDelete;
            selector.setConfig(config);
            return selector;
        }
    }
}
