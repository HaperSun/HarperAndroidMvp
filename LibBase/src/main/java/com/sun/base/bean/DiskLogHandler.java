package com.sun.base.bean;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.sun.base.util.FileUtil;
import com.sun.common.bean.Constant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 日志输出到本地Handler
 */
public class DiskLogHandler extends Handler {

    /**
     * 默认日志输出单个文件最大缓存大小，单位字节
     */
    public static final long DEFAULT_MAX_FILE_BYTES = 500 * 1024L; // 500K averages to a 4000 lines per file
    /**
     * 默认日志输出文件夹最大缓存大小，单位字节
     */
    public static final long DEFAULT_MAX_FOLDER_BYTES = 5 * 1024 * 1024L; // 5M

    private final String folder; //目录文件夹路径
    private final long maxFileSize;//单个文件最大缓存大小，单位字节
    private final long maxFolderSize;//日志文件夹最大大小，单位字节

    /**
     * Creates a disk log handler
     *
     * @param folder        The folder for logs
     * @param maxFileSize   Maximum file size
     * @param maxFolderSize Maximum folder size. Must be multiple of file.
     */
    public DiskLogHandler(String folder, long maxFileSize, long maxFolderSize) {
        this(getDefaultLooper(), folder, maxFileSize, maxFolderSize);
    }

    public DiskLogHandler(Looper looper, String folder, long maxFileSize, long maxFolderSize) {
        super(looper);
        if (TextUtils.isEmpty(folder)) {
            folder = Constant.DirName.LOGGER;
        }
        if (maxFileSize <= 0) {
            maxFileSize = DEFAULT_MAX_FILE_BYTES;
        }
        if (maxFolderSize < maxFileSize) {
            maxFolderSize = DEFAULT_MAX_FOLDER_BYTES;
        }
        this.folder = folder;
        this.maxFileSize = maxFileSize;
        this.maxFolderSize = maxFolderSize;
    }

    private static Looper getDefaultLooper() {
        HandlerThread ht = new HandlerThread("AndroidFileLogger");
        ht.start();
        return ht.getLooper();
    }

    @SuppressWarnings("checkstyle:emptyblock")
    @Override
    public void handleMessage(Message msg) {
        String content = (String) msg.obj;

        FileWriter fileWriter = null;
        File logFile = getLogFile(folder, "logs");

        try {
            fileWriter = new FileWriter(logFile, true);
            if (logFile.length() <= 0) {//说明待写入的文件内容还是空的，先写入额外信息
                writeLog(fileWriter, getExtraInfo());
            }
            writeLog(fileWriter, content);

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e1) { /* fail silently */ }
            }
        }
    }

    /**
     * This is always called on a single background thread.
     * Implementing classes must ONLY write to the fileWriter and nothing more.
     * The abstract class takes care of everything else including close the stream and catching IOException
     *
     * @param fileWriter an instance of FileWriter already initialised to the correct file
     */
    private void writeLog(FileWriter fileWriter, String content) throws IOException {
        fileWriter.append(content);
    }

    private File getLogFile(String folderName, String fileName) {

        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int newFileCount = 0;
        File newFile;
        File existingFile = null;

        checkFolderSize(folder);

        newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
        while (newFile.exists()) {
            existingFile = newFile;
            newFileCount++;
            newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));

            // This block is needed when we delete an old log and create a new one.
            if (existingFile.length() <= maxFileSize) {
                break;
            }
        }

        if (existingFile != null) {
            if (existingFile.length() >= maxFileSize) {
                return newFile;
            }
            return existingFile;
        }

        return newFile;
    }

    /**
     * If folder size is bigger than maximum size deletes the older log file.
     */
    private void checkFolderSize(File folder) {
        if (FileUtil.getDirSize(folder) >= this.maxFolderSize) {
            deleteOlderLog(folder);
        }
    }

    /**
     * Checks all files in folder and deletes the oldest modified.
     *
     * @param folder The target folder to check files
     */
    private void deleteOlderLog(File folder) {
        long lastModified = Long.MAX_VALUE;
        File choice = null;
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                if (file.lastModified() < lastModified) {
                    choice = file;
                    lastModified = file.lastModified();
                }
            }
        }
        if (choice != null) {
            choice.delete();
        }
    }

    /**
     * 获取一些其他要保存的额外信息，比如设备信息、版本信息等等
     *
     * @return
     */
    private String getExtraInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("这里开始记录一些额外信息---------------\n")
                .append("versionName:" + TDevice.getVersionName() + "\n")
                .append("versionCode:" + TDevice.getVersionCode() + "\n")
                .append("MANUFACTURER:" + Build.MANUFACTURER + "\n")
                .append("BRAND:" + Build.BRAND + "\n")
                .append("MODEL:" + Build.MODEL + "\n")
                .append("PRODUCT:" + Build.PRODUCT + "\n")
                .append("Android Version:" + Build.VERSION.RELEASE + "\n")
                .append("API:" + Build.VERSION.SDK_INT + "\n")
                .append("额外信息记录结束---------------\n");
        return stringBuilder.toString();
    }
}
