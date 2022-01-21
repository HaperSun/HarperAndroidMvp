package com.sun.base.util;

import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.sun.base.BuildConfig;

import com.sun.base.bean.DiskLogHandler;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 自定义 打印日志工具
 */
public final class LogUtil {
    /**
     * 默认日志tag <br/>
     */
    private static final String TAG = "LogUtil";

    /**
     * 默认提供打印服务
     */
    private static boolean mEnableLog = false;


    public static final int V = 1;
    public static final int I = 2;
    public static final int D = 3;
    public static final int W = 4;
    public static final int E = 5;
    public static final int ALL = 6;

    private static int mLogLevel = ALL;

    /**
     * 打印 verbose级别
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        try {
            if (mEnableLog && mLogLevel >= V) {
                Logger.t(tag).v(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 verbose级别
     *
     * @param msg
     */
    public static void v(String msg) {
        try {
            if (mEnableLog && mLogLevel >= V) {
                Logger.v(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 info级别
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        try {
            if (mEnableLog && mLogLevel >= I) {
                Logger.t(tag).i(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void i(String msg) {
        try {
            if (mEnableLog && mLogLevel >= I) {
                Logger.i(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 debug级别
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        try {
            if (mEnableLog && mLogLevel == D) {
                Logger.t(tag).d(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 debug级别
     *
     * @param msg
     */
    public static void d(String msg) {
        try {
            if (mEnableLog && mLogLevel == D) {
                Logger.d(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 warn级别
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        try {
            if (mEnableLog && mLogLevel >= W) {
                Logger.t(tag).w(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 warn级别
     *
     * @param msg
     */
    public static void w(String msg) {
        try {
            if (mEnableLog && mLogLevel >= W) {
                Logger.w(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 warn级别
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg, Throwable e) {
        if (mEnableLog && mLogLevel >= W) {
            Logger.t(tag).e(e, msg);
        }
    }


    /**
     * 打印 error级别
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        try {
            if (mEnableLog && E <= mLogLevel) {
                Logger.t(tag).e(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 error级别
     *
     * @param msg
     */
    public static void e(String msg) {
        try {
            if (mEnableLog && mLogLevel >= E) {
                Logger.e(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印 error级别
     *
     * @param tag
     * @param msg
     * @param e
     */
    public static void e(String tag, String msg, Throwable e) {
        if (mEnableLog && mLogLevel >= E) {
            Logger.t(tag).e(e, msg);
        }
    }

    public static void json(String tag, String msg) {
        if (mEnableLog && mLogLevel >= D) {
            Logger.t(tag).json(msg);
        }
    }

    public static void json(String msg) {
        if (mEnableLog && mLogLevel >= D) {
            Logger.json(msg);
        }
    }

    /**
     * 设置打印等级
     *
     * @param level 级别
     */
    public static void setLogLevel(int level) {
        mLogLevel = level;
    }

    /**
     * 需要在Application中初始化
     *
     * @param applicationContext applicationContext
     */
    public static void init(Context applicationContext, int logLevel) {
        mEnableLog = BuildConfig.DEBUG;
        mLogLevel = logLevel;
        setAndroidLog();
        setDiskLog(applicationContext);
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
     *
     * @param applicationContext applicationContext
     */
    private static void setDiskLog(Context applicationContext) {
        //添加日志输出到本地功能
        // 日志输出文件夹路径
        String logFolderPath = FileUtil.getExternalFileDir(applicationContext, DiskLogHandler.DEFAULT_LOG_DIR_NAME)
                .getAbsolutePath();
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
