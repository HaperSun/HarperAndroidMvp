package com.sun.base.db.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sun.base.db.entity.UserInfo;
import com.sun.base.db.greendao.UserInfoDao;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @author Harper
 * @date 2021/11/9
 * note: 操作UserInfo这张表统一管理类
 */
public class UserInfoManager {

    public static final String TAG = UserInfoManager.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile UserInfoManager sInstance = null;
    /**
     * 操作UserInfo表的类
     */
    private final UserInfoDao mUserInfoDao;
    private final Context mContext;

    public interface OnUpdateUserInfoListener {
        void onUserInfoUpdated(UserInfo userInfo);
    }

    public interface OnGetCurrentUserInfoListener {
        UserInfo getCurrentUserInfo();
    }

    private UserInfoManager(Context context) {
        mContext = context;
        mUserInfoDao = DbManager.getDaoSession(context).getUserInfoDao();
    }

    public static UserInfoManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserInfoManager.class) {
                if (sInstance == null) {
                    sInstance = new UserInfoManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取到当前登录的用户信息
     *
     * @return
     */
    public UserInfo getCurrentLoginUser() {
        QueryBuilder<UserInfo> qb = mUserInfoDao.queryBuilder();
        qb.where(UserInfoDao.Properties.LoginState.eq(UserInfo.LoginState.LOGIN.getValue()));
        return qb.build().unique();
    }

    /**
     * 登出当前用户
     *
     * @param clearPwd 是否需要清除密码
     */
    public boolean logout(boolean clearPwd) {
        UserInfo userInfo = getCurrentLoginUser();
        if (userInfo == null) {
            return false;
        }
        userInfo.setLoginState(UserInfo.LoginState.LOGOUT);
        if (clearPwd) {
            userInfo.setPassWord("");
        }
        mUserInfoDao.update(userInfo);
        if (mContext.getApplicationContext() instanceof OnUpdateUserInfoListener) {
            OnUpdateUserInfoListener onUpdateUserInfoListener = (OnUpdateUserInfoListener) mContext.getApplicationContext();
            onUpdateUserInfoListener.onUserInfoUpdated(userInfo);
        }
        return true;
    }

    /**
     * 向表中插入一条用户信息
     *
     * @param userInfo userInfo
     * @return
     */
    public long insertUser(UserInfo userInfo) {
        long rowId = mUserInfoDao.insert(userInfo);
        if (mContext.getApplicationContext() instanceof OnUpdateUserInfoListener) {
            OnUpdateUserInfoListener onUpdateUserInfoListener = (OnUpdateUserInfoListener) mContext.getApplicationContext();
            onUpdateUserInfoListener.onUserInfoUpdated(userInfo);
        }
        return rowId;
    }

    /**
     * 更新一条用户信息
     *
     * @param userInfo
     */
    public void updateUser(UserInfo userInfo) {
        mUserInfoDao.update(userInfo);
        if (mContext.getApplicationContext() instanceof OnUpdateUserInfoListener) {
            OnUpdateUserInfoListener onUpdateUserInfoListener = (OnUpdateUserInfoListener) mContext.getApplicationContext();
            onUpdateUserInfoListener.onUserInfoUpdated(userInfo);
        }
    }

    /**
     * 插入或更新一条用户信息
     *
     * @param userInfo
     */
    public void insertOrUpdateUser(UserInfo userInfo) {
        //在数据库表中根据userId查找是否有对应的记录
        UserInfo user = getUserInfo(String.valueOf(userInfo.getUserId()));
        if (user != null) {
            //在数据更新的时候需要传入ID
            userInfo.setId(user.getId());
            updateUser(userInfo);
        } else {
            insertUser(userInfo);
        }
    }

    /**
     * 根据userId查找用户信息
     *
     * @param userId 用户id
     * @return
     */
    public UserInfo getUserInfo(String userId) {
        return mUserInfoDao.queryBuilder()
                .where(UserInfoDao.Properties.UserId.eq(userId))
                .build()
                .unique();
    }

    /**
     * 处理用户登录事务
     */
    public void loginAndSaveUserInfo(UserInfo user) {
        // 1、将用户信息保存到本地数据库
        userManager().insertOrUpdateUser(user);
    }

    private UserInfoManager userManager() {
        return UserInfoManager.getInstance(mContext);
    }

    public void clear() {
        mUserInfoDao.deleteAll();
    }
}
