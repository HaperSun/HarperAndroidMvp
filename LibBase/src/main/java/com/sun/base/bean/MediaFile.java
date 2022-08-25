package com.sun.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 媒体实体类
 */
public class MediaFile implements Parcelable {

    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int GIF = 3;
    //是图片选择组件的添加按钮
    public static final int BUTTON_ADD = 4;
    //是从相册选择图片时的拍照按钮
    public static final int BUTTON_CAMERA = 5;

    public String path;
    private String mime;
    private Integer folderId;
    private String folderName;
    private long duration;
    private long dateToken;

    /******************************************自定义属性****************************************/

    //1是图片，2是视频，3是GIF，4是从相册选择图片时的拍照按钮
    public int itemType;
    //相册页面使用
    private boolean selected;
    private String selectedIndex;

    //图片选择组件使用
    /**
     * 真实宽度
     */
    public int width;
    /**
     * 真实高度
     */
    public int height;
    /**
     * 网络地址
     */
    public String url;
    /**
     * 是否来自网络
     */
    public boolean fromNet;

    public MediaFile() {
    }

    public MediaFile(int itemType) {
        this.itemType = itemType;
    }

    protected MediaFile(Parcel in) {
        path = in.readString();
        mime = in.readString();
        if (in.readByte() == 0) {
            folderId = null;
        } else {
            folderId = in.readInt();
        }
        folderName = in.readString();
        duration = in.readLong();
        dateToken = in.readLong();
        selected = in.readByte() != 0;
        itemType = in.readInt();
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
        fromNet = in.readByte() != 0;
        selectedIndex = in.readString();
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDateToken() {
        return dateToken;
    }

    public void setDateToken(long dateToken) {
        this.dateToken = dateToken;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFromNet() {
        return fromNet;
    }

    public void setFromNet(boolean fromNet) {
        this.fromNet = fromNet;
    }

    public String getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(String selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(mime);
        if (folderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(folderId);
        }
        dest.writeString(folderName);
        dest.writeLong(duration);
        dest.writeLong(dateToken);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(itemType);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
        dest.writeByte((byte) (fromNet ? 1 : 0));
        dest.writeString(selectedIndex);
    }
}

