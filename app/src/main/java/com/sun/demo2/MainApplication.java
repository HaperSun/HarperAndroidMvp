package com.sun.demo2;

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
import com.sun.img.load.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

/**
 * @author Harper
 * @date 2021/12/6
 * note:
 */
public class MainApplication extends Application implements UserInfoManager.OnUpdateUserInfoListener,
        UserInfoManager.OnGetCurrentUserInfoListener {

    private static MainApplication ctx;
    private UserInfo mUserInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = MainApplication.this;
        BaseUtil.init(getBaseConfig());
        TDevice.initTDevice(ctx);
        NetWork.init(ctx);
        RetrofitUtils.initRetrofit(ctx);
        //初始化LogUtil
        LogUtil.init(ctx, LogUtil.ALL);
        //初始化图片加载组件
        ImageLoader.getInstance().setStrategy();
        initUmSdk();
    }

    private void initUmSdk() {
        UMConfigure.init(ctx, "5cbad65a570df39ea70012b8", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        PlatformConfig.setWeixin("wx18719ce4d83c714a", "c766c2b5d6151ccf926dd03cbc8e56a5");
        PlatformConfig.setWXFileProvider("com.sun.demo2.fileprovider");
    }

    private BaseConfig getBaseConfig() {
        return new BaseConfig(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE,
                BuildConfig.VERSION_NAME, BuildConfig.TEST_URL, BuildConfig.RELEASE_URL);
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

    public static MainApplication getInstance() {
        return ctx;
    }

    public static Context getContext() {
        return ctx;
    }
}
