package com.sun.demo2.model;

import com.sun.base.bean.MediaFile;

import java.util.List;

/**
 * @author Harper
 * @date 2022/8/26
 * note:
 */
public class ImageDisplayBean {

    public List<MediaFile> list;

    public ImageDisplayBean(List<MediaFile> list) {
        this.list = list;
    }
}
