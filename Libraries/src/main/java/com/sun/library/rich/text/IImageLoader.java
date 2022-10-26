package com.sun.library.rich.text;

import android.view.View;
import android.widget.ImageView;

public interface IImageLoader {
    void loadImage(String imagePath, ImageView imageView, int imageHeight, View root, boolean isLocalUpload);
}
