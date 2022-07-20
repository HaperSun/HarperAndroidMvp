package com.sun.base.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: Harper
 * @date: 2022/7/7
 * @note:
 */
public class BitmapUtil {

    // 图片标准宽度
    private static int IMG_DEFAULT_STANDARD_W_SIZE = 1024;
    // 图片标准高度
    private static int IMG_DEFAULT_STANDARD_H_SIZE = 768;

    private BitmapUtil() {
        throw new RuntimeException("you cannot new BitmapUtil!");
    }

    /**
     * 根据原图生成新的尺寸的位图
     *
     * @param path      原图路径
     * @param newWidth  新图宽度
     * @param newHeight 新图高度
     * @return
     */
    public static Bitmap getBitmapPath(String path, int newWidth, int newHeight) {
        Bitmap src = loadBitmap(path);
        if (src == null || newWidth == 0) {
            return src;
        }
        Bitmap dest = zoomImage(src, newWidth, newHeight);
        if (!dest.equals(src)) {
            src.recycle();
        }
        return dest;
    }

    public static Bitmap getBitmapNewSize(Bitmap bitmap, int newWidth, int newHeight, boolean needToRecycle) {
        if (bitmap == null || newWidth <= 0 || newHeight <= 0) {
            return bitmap;
        }
        Bitmap dest = zoomImage(bitmap, newWidth, newHeight);
        if (needToRecycle && !dest.equals(bitmap)) {
            bitmap.recycle();
        }
        return dest;
    }

    /**
     * 修改bitmap的宽高(忽略原图宽高比例)
     *
     * @param bm        原bitmap
     * @param newWidth  新的宽度
     * @param newHeight 新的高度
     * @return
     */
    public static Bitmap getNewSizeBitmap(Bitmap bm, int newWidth, int newHeight, boolean needToRecycle) {
        if (bm == null || newWidth <= 0 || newHeight <= 0) {
            return bm;
        }
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap dest = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        if (needToRecycle && !dest.equals(bm)) {
            bm.recycle();
        }
        return dest;
    }

    public static Bitmap loadBitmap(String path) {
        Point pt = getBitmapSize(path);
        int size = getSize(pt);
        if (size >= 1024) {
            return loadBitmap(path, Bitmap.Config.RGB_565);
        } else {
            return loadBitmap(path, Bitmap.Config.ARGB_8888);
        }
    }

    public static Bitmap loadBitmap(String path, Bitmap.Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Point getBitmapSize(String path) {
        Point pt = new Point();
        BufferedInputStream in = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            in = new BufferedInputStream(new FileInputStream(path));
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    //这两个方向宽高需要置换 ExifInterface
                    pt.set(options.outHeight, options.outWidth);
                } else {
                    pt.set(options.outWidth, options.outHeight);
                }
            } catch (Exception ex) {
                LogHelper.e("BitmapUtil", "getBitmapSize", ex);
                pt.set(options.outWidth, options.outHeight);
            }
        } catch (Exception ex) {
            LogHelper.e("BitmapUtil", "getBitmapSize", ex);
        } finally {
            FileUtil.close(in);
        }
        return pt;
    }

    private static int getSize(Point pt) {
        return pt.x > pt.y ? pt.x : pt.y;
    }

    public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 计算缩放率，新尺寸除原始尺寸s
        if (newWidth == 0) {
            newWidth = IMG_DEFAULT_STANDARD_W_SIZE;
        }
        if (newHeight == 0) {
            newHeight = IMG_DEFAULT_STANDARD_H_SIZE;
        }
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 缩放图片动作
        float scale = Math.min(scaleWidth, scaleHeight);

        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bgimage, 0, 0, width, height,
                matrix, true);
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static boolean saveBitmap(Bitmap bm, String path) {
        boolean result = false;
        FileOutputStream out = null;
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogHelper.d("load_cover1", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.d("load_cover2", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.d("load_cover3", e.getMessage());
        }finally {
            FileUtil.close(out);
        }
        return result;
    }
}
