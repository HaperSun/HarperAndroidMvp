package com.rich.text;

import android.view.View;
import android.widget.ImageView;

public class XRichText {
    private static XRichText instance;
    private IImageLoader imageLoader;

    public static XRichText getInstance() {
        if (instance == null) {
            synchronized (XRichText.class) {
                if (instance == null) {
                    instance = new XRichText();
                }
            }
        }
        return instance;
    }

    public void setImageLoader(IImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    /**
     * @param imagePath  图片路径
     * @param imageView
     * @param imageHeight  图片高度
     * @param root
     * @param isLocalUpload 是否本地上传，还是网络获取的图片
     */
    public void loadImage(String imagePath, ImageView imageView, int imageHeight, View root, boolean isLocalUpload) {
        if (imageLoader != null) {
            imageLoader.loadImage(imagePath, imageView, imageHeight, root, isLocalUpload);
        }
    }
}
