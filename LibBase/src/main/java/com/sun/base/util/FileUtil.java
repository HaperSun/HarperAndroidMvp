package com.sun.base.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.sun.base.R;
import com.sun.common.toast.ToastHelper;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 关于文件操作的工具类
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    private FileUtil() {
        throw new RuntimeException("You cannot initialize me!");
    }

    /**
     * 返回sd卡程序包名下文件路径，卸载程序时会一起跟着清空
     *
     * @param context
     * @param dir     文件夹名
     * @return
     */
    public static File getExternalFileDir(Context context, String dir) {
        if (context == null) {
            return null;
        }
        File appFiles = context.getExternalFilesDir(dir);
        if (appFiles == null) {
            String packageName = context.getPackageName();
            File externalPath = Environment.getExternalStorageDirectory();
            appFiles = new File(externalPath.getAbsolutePath() + "/Android/data/" + packageName + "/files/" + dir);
            if (!appFiles.exists()) {
                appFiles.mkdirs();
            }
        }
        return appFiles;
    }

    /**
     * 将指定数据保存到指定文件中去
     *
     * @param data     要保存的数据
     * @param filePath 要保存的文件路径
     * @param append   是否加在文件末尾，false会覆盖文件内容
     * @return
     */
    public static boolean saveStrToFile(String data, String filePath, boolean append) {
        if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(filePath)) {
            try {
                File f = new File(filePath);
                if (!f.exists()) {
                    createFile(f);
                }
                FileOutputStream fos = new FileOutputStream(filePath, append);
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                output.write(data);
                output.flush();
                output.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.d("saveStrToFile", e.getMessage());
            }
        }
        return false;
    }

    /**
     * 读取指定文件中的数据
     *
     * @param filePath 文件路径
     * @return
     */
    public static String readStrFromFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File f = new File(filePath);
        if (f.exists()) {
            FileInputStream fis = null;
            ByteArrayOutputStream baos = null;
            try {
                fis = new FileInputStream(f);
                baos = new ByteArrayOutputStream(1024);
                byte[] temp = new byte[1024];
                int size = 0;
                while ((size = fis.read(temp)) != -1) {
                    baos.write(temp, 0, size);
                }
                byte[] fileContent = baos.toByteArray();
                baos.close();
                fis.close();
                return new String(fileContent, Charset.forName("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                FileUtil.close(baos);
                FileUtil.close(fis);
            }
        }
        return null;
    }

    public static boolean createFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除目录下所有的文件
     *
     * @param filePath 要删除的文件目录的路径
     */
    public static void delete(String filePath) {
        delete(filePath, true);
    }

    /**
     * 删除目录下所有的文件
     *
     * @param filePath       要删除的文件目录的路径
     * @param deleteRootFile 是否要删除根目录文件夹，如果指定的文件是文件夹的话
     */
    public static void delete(String filePath, boolean deleteRootFile) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        delete(new File(filePath), deleteRootFile);
    }

    /**
     * 删除目录下所有的文件
     *
     * @param file 要删除的文件目录
     */
    public static void delete(File file) {
        delete(file, true);
    }

    /**
     * 删除目录下所有的文件
     *
     * @param file           要删除的文件目录
     * @param deleteRootFile 是否要删除根目录文件夹，如果指定的文件是文件夹的话
     */
    public static void delete(File file, boolean deleteRootFile) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                if (deleteRootFile) {
                    file.delete();
                }
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            if (deleteRootFile) {
                file.delete();
            }
        }
    }

    /**
     * 检测文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    /**
     * 检测文件目录是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileDirExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    /**
     * 检测文件夹是否存在，如果不存在则创建文件夹
     *
     * @param path
     */
    public static boolean checkFileDirOrMk(String path) {
        if (!isFileDirExist(path)) {
            makeDir(path);
            return false;
        }
        return true;
    }

    /**
     * 创建文件路径
     *
     * @param path 文件路径或文件名，例如：/sdcard/msc/ 或 /sdcard/msc/msc.log
     */
    public static void makeDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File f = new File(path);
        if (!path.endsWith("/")) {
            f = f.getParentFile();
        }
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;
        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小格式化字符串
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirSize(file); // 递归调用继续统计
                }
            }
        }
        return dirSize;
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName) {
        String extension = "";
        if (fileName != null && fileName.length() > 0) {
            int index = fileName.lastIndexOf('.');
            if (index > -1 && index < fileName.length() - 1) {
                extension = fileName.substring(index + 1);
            }
        }
        return extension;
    }

    /**
     * 获取文件名
     *
     * @param path 文件路径
     * @return
     */
    public static String getFileNameByPath(String path) {
        String name = "";
        try {
            path = path.trim();
            if (path.lastIndexOf("?") > -1
                    && path.lastIndexOf("?") < path.length()) {
                path = path.substring(0, path.lastIndexOf("?"));
            }

            String[] names = path.split("/");
            name = names[names.length - 1];
        } catch (Exception ex) {
            name = "";
        }
        return name;
    }

    /**
     * 关闭stream or reader
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (IOException e) {
            LogHelper.e(TAG, "close closeable exception", e);
        }
    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 获取文件的MD5值
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getFileMD5(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return getFileMD5(new File(filePath));
    }

    /**
     * 获取文件的MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            FileUtil.close(in);
        }
        return toHexString(digest.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 获取文件的MIME
     *
     * @param filePathOrUrl 本地路径或者url
     * @return
     */
    public static String getFileMimeType(String filePathOrUrl) {
        String mimeType = null;
        String extension = FileUtil.getExtension(filePathOrUrl);
        if (TextUtils.isEmpty(extension)) {
            //使用系统API，获取URL路径中文件的后缀名（扩展名）
            extension = MimeTypeMap.getFileExtensionFromUrl(filePathOrUrl);
        }
        if (!TextUtils.isEmpty(extension)) {
            //使用系统API，获取MimeTypeMap的单例实例，然后调用其内部方法获取文件后缀名（扩展名）所对应的MIME类型
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        LogHelper.i(TAG, "filePathOrUrl-->" + filePathOrUrl + "的系统定义的MIME类型为：" + mimeType);
        return mimeType;
    }

    /**
     * 根据Uri获取真实的文件路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        ContentResolver resolver = context.getContentResolver();
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            if (pfd == null) {
                return null;
            }
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);


            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("image", "tmp", outputDir);
            String tempFilename = outputFile.getAbsolutePath();
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }

            return new File(tempFilename).getAbsolutePath();
        } catch (Exception ignored) {

            ignored.getStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (Throwable t) {
                // Do nothing
            }
        }
        return null;
    }

    /**
     * 在系统相册中创建以App名称为名字的文件夹，并将图片保存到该文件中
     *
     * @param context 上下文
     * @param imgUrl  网络图片地址
     * @param bitmap  bitmap
     */
    public static void saveNetImgToAlbum(Context context, String imgUrl, Bitmap bitmap) {
        //图片后缀
        String ext;
        if (StringUtil.isNetUrl(imgUrl)) {
            ext = MimeTypeMap.getFileExtensionFromUrl(imgUrl);
        } else {
            ext = FileUtil.getExtension(imgUrl);
        }
        boolean isPng = "png".equalsIgnoreCase(ext);
        if (!isPng) {
            ext = "jpg";
        }
        //随机UUID+时间戳作为文件名
        String saveFileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + "." + ext;
        File saveFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + "/" + context.getString(R.string.app_name));
        if (!saveFileDir.exists()) {
            saveFileDir.mkdirs();
        }
        File saveFile = new File(saveFileDir, saveFileName);
        //保存图片
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveFile);
            bitmap.compress(isPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, fos);
            ToastHelper.showCommonToast("图片已保存至" + saveFileDir.getAbsolutePath() + "文件夹");
            //发广播更新图库
            Uri uri = Uri.fromFile(saveFile);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            LogHelper.e(TAG, "saveImage Exception!", e);
            ToastHelper.showCommonToast("图片保存失败！");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
