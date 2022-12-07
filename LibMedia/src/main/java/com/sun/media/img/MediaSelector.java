package com.sun.media.img;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.fragment.app.Fragment;

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

    @SuppressLint("StaticFieldLeak")
    private static volatile MediaSelector mInstance = null;
    private Activity activity;
    private Fragment fragment;
    public MediaConfig config;
    private ArrayList<MediaFile> mSelectedFiles;

    public static MediaSelector getInstance() {
        if (mInstance == null) {
            synchronized (MediaSelector.class) {
                if (mInstance == null) {
                    mInstance = new MediaSelector();
                    builder().build();
                }
            }
        }
        return mInstance;
    }

    public static Builder builder(Activity activity) {
        return new Builder(activity);
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
        if (config.fromCamera){
            //启动相机
            if (activity != null) {
                CameraActivity.startActivityResult(activity, Parameter.REQUEST_CODE_MEDIA, config.mediaFileType);
            } else if (fragment != null) {
                CameraActivity.startActivityResult(fragment, Parameter.REQUEST_CODE_MEDIA, config.mediaFileType);
            }
        }else {
            //启动相册
            if (activity != null) {
                ImagePickerActivity.startActivityResult(activity, Parameter.REQUEST_CODE_MEDIA);
            } else if (fragment != null) {
                ImagePickerActivity.startActivityResult(fragment, Parameter.REQUEST_CODE_MEDIA);
            }
        }
    }

    public void setSelectedFiles(ArrayList<MediaFile> selectedFilePaths) {
        mSelectedFiles = selectedFilePaths;
    }

    /**
     * 获取当前选择的媒体文件集合
     *
     * @return ArrayList
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
     * @param currentPath currentPath
     * @param filePath    filePath
     * @return boolean
     */
    public static boolean isCanAddSelectionPaths(String currentPath, String filePath) {
        return (!MediaFileUtil.isVideoFileType(currentPath) || MediaFileUtil.isVideoFileType(filePath))
                && (MediaFileUtil.isVideoFileType(currentPath) || !MediaFileUtil.isVideoFileType(filePath));
    }

    public static class Builder {

        private Activity activity;
        private Fragment fragment;
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
        //操作类型：只包含拍照和相册两种
        private boolean fromCamera;
        //选择文件类型，默认图片和视频都可选择
        private int mediaFileType;
        //最大选择视频数，默认是1个
        private int maxVideoSelectCount;
        //是否需要上传到网络，默认不上传
        private boolean needUploadFile;
        //是否展示删除按钮，默认展示
        private boolean showDelete;
        //从相册中选择文件时，是否可以拍照，默认可以
        private boolean albumCanTakePhoto;
        //是否过滤GIF图片，默认过滤
        private boolean filterGif;
        //（拍摄时默认使用的是前置摄像头）是否需要旋转摄像头，默认不需要
        private boolean switchCamera;

        public Builder() {
            setDefault();
        }

        private Builder(Activity activity) {
            this.activity = activity;
            //重新设置下默认值，避免多处使用时，设置的相互影响
            setDefault();
        }

        private Builder(Fragment fragment) {
            this.fragment = fragment;
            //重新设置下默认值，避免多处使用时，设置的相互影响
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
            //默认从相册中选择
            fromCamera = false;
            //默认既可以选择图片又可以选择视频
            mediaFileType = MediaConfig.PHOTO;
            maxVideoSelectCount = 1;
            needUploadFile = false;
            showDelete = true;
            albumCanTakePhoto = true;
            //默认过滤GIF，暂不支持GIF
            filterGif = true;
            switchCamera = false;
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

        public Builder fromCamera(boolean fromCamera) {
            this.fromCamera = fromCamera;
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

        public Builder needUploadFile(boolean needUploadFile) {
            this.needUploadFile = needUploadFile;
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

        public Builder switchCamera(boolean switchCamera) {
            this.switchCamera = switchCamera;
            return this;
        }

        public MediaSelector build() {
            MediaSelector selector = MediaSelector.getInstance();
            MediaConfig config = new MediaConfig();
            selector.activity = activity;
            selector.fragment = fragment;
            config.isMirror = isMirror;
            config.maxOriginalSize = maxOriginalSize;
            config.maxCount = maxCount;
            config.selectedCount = selectedCount;
            config.maxWidth = maxWidth;
            config.maxHeight = maxHeight;
            config.maxImageSize = maxImageSize;
            config.maxVideoLength = maxVideoLength;
            config.maxVideoSize = maxVideoSize;
            config.fromCamera = fromCamera;
            config.mediaFileType = mediaFileType;
            config.maxVideoSelectCount = maxVideoSelectCount;
            config.needUploadFile = needUploadFile;
            config.showDelete = showDelete;
            config.albumCanTakePhoto = albumCanTakePhoto;
            config.filterGif = filterGif;
            config.switchCamera = switchCamera;
            selector.setConfig(config);
            return selector;
        }
    }
}
