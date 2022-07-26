package com.sun.base.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sun.base.db.greendao.AccountInfoDao;
import com.sun.base.db.greendao.DaoMaster;
import com.sun.base.db.greendao.UserInfoDao;

import org.greenrobot.greendao.database.Database;

/**
 * @author Harper
 * @date 2021/11/9
 * note: 数据库帮助类，数据库升级操作逻辑在此类里面
 */
public class DbOpenHelper extends DaoMaster.OpenHelper {

    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, UserInfoDao.class, AccountInfoDao.class);
    }
}
