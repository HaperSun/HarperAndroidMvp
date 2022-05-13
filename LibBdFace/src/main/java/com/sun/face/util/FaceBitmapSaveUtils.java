package com.sun.face.util;

public class FaceBitmapSaveUtils {

    private static FaceBitmapSaveUtils instance = null;

    private String mBitmap;

    /**
     * 单例模式
     * @return FaceSDKManager实体
     */
    public static FaceBitmapSaveUtils getInstance() {
        if (instance == null) {
            synchronized (FaceBitmapSaveUtils.class) {
                if (instance == null) {
                    instance = new FaceBitmapSaveUtils();
                }
            }
        }
        return instance;
    }

    public void setBitmap(String bitmap) {
        mBitmap = bitmap;
    }

    public String getBitmap() {
        return mBitmap;
    }

    public void release() {
        if (instance != null) {
            instance = null;
        }
    }
}
