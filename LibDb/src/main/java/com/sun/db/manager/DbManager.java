package com.sun.db.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sun.common.util.AppUtil;
import com.sun.db.greendao.DaoMaster;
import com.sun.db.greendao.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @author Harper
 * @date 2021/11/9
 * note:
 */
public class DbManager {

    /**
     * 数据库名字
     */
    private static final String DB_NAME = "test.db";
    private static volatile DbManager sInstance = null;
    @SuppressLint("StaticFieldLeak")
    private static DbOpenHelper mDbOpenHelper;
    private static volatile DaoMaster mDaoMaster;
    private static volatile DaoSession mDaoSession;

    private DbManager(Context context) {
        // 初始化数据库信息
        mDbOpenHelper = new DbOpenHelper(context, DB_NAME);
        //debug 模式下打印sql信息，方便查找错误
        boolean isDebug = AppUtil.isApkDebugEnable();
        QueryBuilder.LOG_SQL = isDebug;
        QueryBuilder.LOG_VALUES = isDebug;
    }

    private static DbManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DbManager.class) {
                if (sInstance == null) {
                    sInstance = new DbManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取可写入的数据库
     *
     * @param context
     * @return
     */
    private static SQLiteDatabase getWritableDatabase(Context context) {
        if (null == mDbOpenHelper) {
            getInstance(context);
        }
        return mDbOpenHelper.getWritableDatabase();
    }

    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    private static DaoMaster getDaoMaster(Context context) {
        if (null == mDaoMaster) {
            synchronized (DbManager.class) {
                if (null == mDaoMaster) {
                    mDaoMaster = new DaoMaster(getWritableDatabase(context));
                }
            }
        }
        return mDaoMaster;
    }

    /**
     * 获取DaoSession 对外暴露这个方法就可以了
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (null == mDaoSession) {
            synchronized (DbManager.class) {
                if (null == mDaoSession) {
                    mDaoSession = getDaoMaster(context).newSession();
                }
            }
        }
        return mDaoSession;
    }
}
