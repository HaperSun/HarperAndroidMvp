package com.sun.img.load;

/**
 * @author: Harper
 * @date: 2021/12/10
 * @note: 图片保存监听
 */
public interface ImageSaveListener {

    /**
     * 保存成功
     */
    void onSaveSuccess();

    /**
     * 保存成功失败
     */
    void onSaveFail();
}