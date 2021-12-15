package com.sun.base.img;
/**
 * @author: Harper
 * @date:   2021/12/10
 * @note: 图片保存监听
 */
public interface ImageSaveListener {

    void onSaveSuccess();

    void onSaveFail();
}