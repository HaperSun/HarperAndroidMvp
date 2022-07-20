package com.sun.img.provider;

import android.content.Context;

import androidx.core.content.FileProvider;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 自定义Provider，避免上层发生provider冲突
 */
public class ImagePickerProvider extends FileProvider {

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".provider";
    }

}
