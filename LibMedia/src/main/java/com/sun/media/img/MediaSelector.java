package com.sun.media.img;

import androidx.fragment.app.FragmentActivity;

import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.MediaFileUtil;
import com.sun.media.camera.CameraActivity;
import com.sun.media.img.manager.MediaConfig;
import com.sun.media.img.ui.activity.ImagePickerActivity;

import java.util.ArrayList;

/**
 * @author Harper
 * @date 2022/7/27
 * note:
 */
public class MediaSelector {

    private static volatile MediaSelector mMediaSelector;
    private FragmentActivity fragmentActivity;
    public MediaConfig config;
    private ArrayList<MediaFile> mSelectedFiles;

    public static MediaSelector getInstance() {
        if (mMediaSelector == null) {
            synchronized (MediaSelector.class) {
                if (mMediaSelector == null) {
                    mMediaSelector = new MediaSelector();
                    builder().build();
                }
            }
        }
        return mMediaSelector;
    }

    public static Builder builder(FragmentActivity fragmentActivity) {
        return new Builder(fragmentActivity);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void setConfig(MediaConfig config) {
        this.config = config;
    }

    public void show() {
        if (fragmentActivity != null) {
            switch (config.operationType) {
                case MediaConfig.TAKE_PHOTO:
                case MediaConfig.TAKE_VIDEO:
                case MediaConfig.TAKE_BOTH:
                    //启动相机
                    CameraActivity.startForResult(fragmentActivity, Parameter.REQUEST_CODE_MEDIA, config.mediaFileType);
                    break;
                case MediaConfig.FROM_ALBUM:
                    //启动相册
                    ImagePickerActivity.startForResult(fragmentActivity, Parameter.REQUEST_CODE_MEDIA);
                    break;
                default:
                    break;
            }
        }
    }

    public void setSelectedFiles(ArrayList<MediaFile> selectedFilePaths) {
        mSelectedFiles = selectedFilePaths;
    }

    /**
     * 获取当前选择的媒体文件集合
     *
     * @return
     */
    public ArrayList<MediaFile> getSelectedFiles() {
        if (CollectionUtil.isEmpty(mSelectedFiles)) {
            return mSelectedFiles = new ArrayList<>();
        }
        return mSelectedFiles;
    }

    /**
     * 是否可以添加到选择集合（在singleType模式下，图片视频不能一起选）
     *
     * @param currentPath
     * @param filePath
     * @return
     */
    public static boolean isCanAddSelectionPaths(String currentPath, String filePath) {
        return (!MediaFileUtil.isVideoFileType(currentPath) || MediaFileUtil.isVideoFileType(filePath))
                && (MediaFileUtil.isVideoFileType(currentPath) || !MediaFileUtil.isVideoFileType(filePath));
    }

    public static class Builder {

        private FragmentActivity fragmentActivity;
        //前置摄像头拍摄是否启用镜像 默认开启
        private boolean isMirror;
        //最大原图大小 单位MB
        private int maxOriginalSize;
        //最大选择数目
        private int maxCount;
        //已选择数目
        private int selectedCount;
        //最大图片宽度
        private int maxWidth;
        //最大图片高度
        private int maxHeight;
        //单位：MB , 默认15MB
        private int maxImageSize;
        //视频时长，单位：毫秒 ,默认30秒
        private long maxVideoLength;
        //视频大小，单位：MB
        private int maxVideoSize;
        //操作类型
        private String operationType;
        private int mediaFileType;
        //最大选择视频数
        private int maxVideoSelectCount;
        //在本地显示，不用上传到网络
        public boolean showLocal;
        //可以删除
        public boolean showDelete;
        //相册中是否可以拍照
        public boolean albumCanTakePhoto;
        //是否过滤GIF图片，默认过滤
        public boolean filterGif;

        public Builder() {
            setDefault();
        }

        private Builder(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
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
            maxVideoLength = 301 * 1000L;
            maxVideoSize = 20;
            operationType = MediaConfig.TAKE_PHOTO;
            mediaFileType = MediaConfig.PHOTO;
            maxVideoSelectCount = 1;
            showLocal = true;
            showDelete = true;
            albumCanTakePhoto = true;
            filterGif = true;
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
            if (maxCount <= 9) {
                this.maxCount = maxCount;
            }
            return this;
        }

        public Builder selectedCount(int selectedCount) {
            this.selectedCount = selectedCount;
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

        public Builder maxVideoLength(long maxVideoLength) {
            this.maxVideoLength = maxVideoLength;
            return this;
        }

        public Builder maxVideoSize(int maxVideoSize) {
            this.maxVideoSize = maxVideoSize;
            return this;
        }

        public Builder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder mediaFileType(int mediaFileType) {
            this.mediaFileType = mediaFileType;
            return this;
        }

        public Builder maxVideoCount(int maxVideoSelectCount) {
            this.maxVideoSelectCount = maxVideoSelectCount;
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

        public Builder albumCanTakePhoto(boolean albumCanTakePhoto) {
            this.albumCanTakePhoto = albumCanTakePhoto;
            return this;
        }

        public Builder filterGif(boolean filterGif) {
            this.filterGif = filterGif;
            return this;
        }

        public MediaSelector build() {
            MediaSelector selector = MediaSelector.getInstance();
            MediaConfig config = new MediaConfig();
            selector.fragmentActivity = fragmentActivity;
            config.isMirror = isMirror;
            config.maxOriginalSize = maxOriginalSize;
            config.maxCount = maxCount;
            config.selectedCount = selectedCount;
            config.maxWidth = maxWidth;
            config.maxHeight = maxHeight;
            config.maxImageSize = maxImageSize;
            config.maxVideoLength = maxVideoLength;
            config.maxVideoSize = maxVideoSize;
            config.operationType = operationType;
            config.mediaFileType = mediaFileType;
            config.maxVideoSelectCount = maxVideoSelectCount;
            config.showLocal = showLocal;
            config.showDelete = showDelete;
            config.albumCanTakePhoto = albumCanTakePhoto;
            config.filterGif = filterGif;
            selector.setConfig(config);
            return selector;
        }
    }
}
