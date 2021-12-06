package com.sun.demo;

import android.app.Application;
import android.content.Context;

import com.sun.base.bean.BaseConfig;
import com.sun.base.bean.TDevice;
import com.sun.base.net.NetWork;
import com.sun.base.util.BaseUtil;
import com.sun.base.util.LogUtil;
import com.sun.base.util.RetrofitUtils;
import com.sun.db.entity.UserInfo;
import com.sun.db.table.manager.UserInfoManager;

/**
 * @author Harper
 * @date 2021/12/6
 * note:
 */
public class MainApplication extends Application implements UserInfoManager.OnUpdateUserInfoListener,
        UserInfoManager.OnGetCurrentUserInfoListener {

    private UserInfo mUserInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = MainApplication.this;
        BaseUtil.init(getBaseConfig());
        TDevice.initTDevice(context);
        NetWork.init(context);
        RetrofitUtils.initRetrofit(context);
        //初始化LogUtil
        LogUtil.init(context, LogUtil.ALL);
    }

    private BaseConfig getBaseConfig() {
        return new BaseConfig(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME,
                BuildConfig.TEST_URL, BuildConfig.RELEASE_URL);
    }

    @Override
    public void onUserInfoUpdated(UserInfo userInfo) {
        mUserInfo = UserInfoManager.getInstance(this).getCurrentLoginUser();
    }

    @Override
    public UserInfo getCurrentUserInfo() {
        if (mUserInfo == null) {
            mUserInfo = UserInfoManager.getInstance(this).getCurrentLoginUser();
        }
        return mUserInfo;
    }
}
