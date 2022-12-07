package com.sun.base.util;

import android.text.TextUtils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.sun.base.BuildConfig;
import com.sun.base.bean.Constant;
import com.sun.base.bean.DiskLogHandler;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 自定义 打印日志工具
 */
public final class LogHelper {

    /**
     * 默认日志tag
     */
    private static final String TAG = LogHelper.class.getSimpleName();
    /**
     * 默认提供打印服务
     */
    private static boolean mEnableLog = false;
    private static final int V = 1;
    private static final int D = 2;
    private static final int I = 3;
    private static final int W = 4;
    private static final int E = 5;
    private static int mMaxLogLevel = E;

    /**
     * 需要在Application中初始化
     */
    public static void init() {
        mEnableLog = BuildConfig.DEBUG;
        setAndroidLog();
        setDiskLog();
    }

    /**
     * Verbose级别
     *
     * @param tag 标记
     * @param msg 信息
     */
    public static void v(String tag, String msg) {
        if (mEnableLog && mMaxLogLevel >= V && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).v(msg);
        }
    }

    /**
     * Verbose级别
     *
     * @param msg 信息
     */
    public static void v(String msg) {
        if (mEnableLog && mMaxLogLevel >= V && !TextUtils.isEmpty(msg)) {
            Logger.v(msg);
        }
    }

    /**
     * debug级别
     *
     * @param tag 标记
     * @param msg 信息
     */
    public static void d(String tag, String msg) {
        if (mEnableLog && mMaxLogLevel >= D && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).d(msg);
        }
    }

    /**
     * debug级别
     *
     * @param msg 信息
     */
    public static void d(String msg) {
        if (mEnableLog && mMaxLogLevel >= D && !TextUtils.isEmpty(msg)) {
            Logger.d(msg);
        }
    }

    /**
     * info级别
     *
     * @param tag 标记
     * @param msg 信息
     */
    public static void i(String tag, String msg) {
        if (mEnableLog && mMaxLogLevel >= I && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).i(msg);
        }
    }

    /**
     * info级别
     *
     * @param msg 信息
     */
    public static void i(String msg) {
        if (mEnableLog && mMaxLogLevel >= I && !TextUtils.isEmpty(msg)) {
            Logger.i(msg);
        }
    }

    /**
     * warn级别
     *
     * @param tag 标记
     * @param msg 信息
     */
    public static void w(String tag, String msg) {
        if (mEnableLog && mMaxLogLevel >= W && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).w(msg);
        }
    }

    /**
     * warn级别
     *
     * @param msg 信息
     */
    public static void w(String msg) {
        if (mEnableLog && mMaxLogLevel >= W && !TextUtils.isEmpty(msg)) {
            Logger.w(msg);
        }
    }

    /**
     * error级别
     *
     * @param tag 标记
     * @param msg 信息
     */
    public static void e(String tag, String msg) {
        if (mEnableLog && E <= mMaxLogLevel && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).e(msg);
        }
    }

    /**
     * error级别
     *
     * @param msg 信息
     */
    public static void e(String msg) {
        if (mEnableLog && mMaxLogLevel >= E && !TextUtils.isEmpty(msg)) {
            Logger.e(msg);
        }
    }

    /**
     * error级别
     *
     * @param tag 标记
     * @param msg 信息
     * @param e   异常
     */
    public static void e(String tag, String msg, Throwable e) {
        if (mEnableLog && mMaxLogLevel >= E && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).e(e, msg);
        }
    }

    /**
     * @param tag 标记
     * @param msg 信息
     */
    public static void json(String tag, String msg) {
        if (mEnableLog && !TextUtils.isEmpty(msg)) {
            Logger.t(tag).json(msg);
        }
    }

    /**
     * @param msg 信息
     */
    public static void json(String msg) {
        if (mEnableLog && !TextUtils.isEmpty(msg)) {
            Logger.json(msg);
        }
    }

    /**
     * 设置打印等级
     *
     * @param level 级别
     */
    public static void setLogLevel(int level) {
        mMaxLogLevel = level;
    }

    /**
     * 设置打印日志到Android控制台（Console）
     */
    private static void setAndroidLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)
                // (Optional) How many method line to show. Default 2
                .methodOffset(7)
                // (Optional) Hides internal method calls up to offset. Default 5
//                    .logStrategy(customLog)
                // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(TAG)
                // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return mEnableLog;
            }
        });
    }

    /**
     * 设置打印日志到本地
     */
    private static void setDiskLog() {
        //添加日志输出到本地功能
        // 日志输出文件夹路径
        String logFolderPath = FileUtil.getExternalFileDir(Constant.DIRECTORY_NAME_LOGGER).getAbsolutePath();
        //单个日志文件最大大小
        long maxPerLogFileSize = DiskLogHandler.DEFAULT_MAX_FILE_BYTES;
        //日志文件夹输出最大大小
        long maxLogFolderSize = DiskLogHandler.DEFAULT_MAX_FOLDER_BYTES;
        LogStrategy diskLogStrategy = new DiskLogStrategy(new DiskLogHandler(logFolderPath, maxPerLogFileSize, maxLogFolderSize));
        // (Optional) Global tag for every log. Default PRETTY_LOGGER
        CsvFormatStrategy csvFormatStrategy = CsvFormatStrategy.newBuilder().logStrategy(diskLogStrategy).tag(TAG).build();
        Logger.addLogAdapter(new DiskLogAdapter(csvFormatStrategy));
    }
}
