package com.sun.base.db.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sun.base.db.entity.UserInfo.LoginState;
import com.sun.base.db.entity.UserInfo.StateConverter;

import com.sun.base.db.entity.UserInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_INFO".
*/
public class UserInfoDao extends AbstractDao<UserInfo, Long> {

    public static final String TABLENAME = "USER_INFO";

    /**
     * Properties of entity UserInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property PassWord = new Property(2, String.class, "passWord", false, "PASS_WORD");
        public final static Property AccessToken = new Property(3, String.class, "accessToken", false, "ACCESS_TOKEN");
        public final static Property UserId = new Property(4, long.class, "userId", false, "USER_ID");
        public final static Property LoginState = new Property(5, int.class, "loginState", false, "LOGIN_STATE");
    }

    private final StateConverter loginStateConverter = new StateConverter();

    public UserInfoDao(DaoConfig config) {
        super(config);
    }
    
    public UserInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_NAME\" TEXT NOT NULL ," + // 1: userName
                "\"PASS_WORD\" TEXT," + // 2: passWord
                "\"ACCESS_TOKEN\" TEXT," + // 3: accessToken
                "\"USER_ID\" INTEGER NOT NULL ," + // 4: userId
                "\"LOGIN_STATE\" INTEGER NOT NULL );"); // 5: loginState
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserName());
 
        String passWord = entity.getPassWord();
        if (passWord != null) {
            stmt.bindString(3, passWord);
        }
 
        String accessToken = entity.getAccessToken();
        if (accessToken != null) {
            stmt.bindString(4, accessToken);
        }
        stmt.bindLong(5, entity.getUserId());
        stmt.bindLong(6, loginStateConverter.convertToDatabaseValue(entity.getLoginState()));
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserName());
 
        String passWord = entity.getPassWord();
        if (passWord != null) {
            stmt.bindString(3, passWord);
        }
 
        String accessToken = entity.getAccessToken();
        if (accessToken != null) {
            stmt.bindString(4, accessToken);
        }
        stmt.bindLong(5, entity.getUserId());
        stmt.bindLong(6, loginStateConverter.convertToDatabaseValue(entity.getLoginState()));
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserInfo readEntity(Cursor cursor, int offset) {
        UserInfo entity = new UserInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // userName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // passWord
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // accessToken
            cursor.getLong(offset + 4), // userId
            loginStateConverter.convertToEntityProperty(cursor.getInt(offset + 5)) // loginState
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.getString(offset + 1));
        entity.setPassWord(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAccessToken(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUserId(cursor.getLong(offset + 4));
        entity.setLoginState(loginStateConverter.convertToEntityProperty(cursor.getInt(offset + 5)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}