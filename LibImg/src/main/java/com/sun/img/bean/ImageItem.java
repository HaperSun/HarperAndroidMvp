package com.sun.img.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 描述预览图片的基本数据结构
 */
public class ImageItem implements Parcelable {


    /**
     * 缩略图路径，可以是本地地址，也可以是网络地址
     */
    private String mImageThumb;
    /**
     * 原图路径，可以是本地地址，也可以是网络地址
     */
    private String mImageOri;

    public ImageItem(String imageThumb, String imageOri) {
        mImageThumb = imageThumb;
        mImageOri = imageOri;
    }

    public ImageItem(String imageOri) {
        mImageOri = imageOri;
    }

    public String getImageThumb() {
        return mImageThumb;
    }

    public void setImageThumb(String imageThumb) {
        mImageThumb = imageThumb;
    }

    public String getImageOri() {
        return mImageOri;
    }

    public void setImageOri(String imageOri) {
        mImageOri = imageOri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mImageThumb);
        dest.writeString(this.mImageOri);
    }

    protected ImageItem(Parcel in) {
        this.mImageThumb = in.readString();
        this.mImageOri = in.readString();
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
