package com.sun.demo2;

import android.app.Application;
import android.content.Context;

import com.rich.text.XRichText;
import com.sun.base.bean.BaseConfig;
import com.sun.base.bean.TDevice;
import com.sun.base.disk.CacheFileRule;
import com.sun.base.disk.DiskCacheConst;
import com.sun.base.disk.DiskCacheManager;
import com.sun.base.net.NetWork;
import com.sun.base.service.IAccountService;
import com.sun.base.service.ServiceFactory;
import com.sun.base.util.BaseUtil;
import com.sun.base.util.LogUtil;
import com.sun.base.util.RetrofitUtils;
import com.sun.base.util.XRichEditorUtil;
import com.sun.db.entity.UserInfo;
import com.sun.db.table.manager.UserInfoManager;
import com.sun.demo2.model.response.LoginResponse;
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

    public static Context getContext() {
        return ctx;
    }

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
        //将 AccountService 类的实例注册到 ServiceFactory
        initAccountService();
        initRichEditor();
        initDiskCacheManager();
    }

    private void initUmSdk() {
        UMConfigure.init(ctx, "5cbad65a570df39ea70012b8", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        PlatformConfig.setWeixin("wx18719ce4d83c714a", "c766c2b5d6151ccf926dd03cbc8e56a5");
        PlatformConfig.setWXFileProvider("com.sun.demo2.fileprovider");
    }

    private BaseConfig getBaseConfig() {
        return new BaseConfig(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE,
                BuildConfig.VERSION_NAME, BuildConfig.Base_URL);
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

    private void initDiskCacheManager() {
        DiskCacheManager.init(ctx, () -> {
            UserInfo currentUserInfo = getCurrentUserInfo();
            return currentUserInfo == null ? null : String.valueOf(currentUserInfo.getUserId());
        });
        DiskCacheManager.registerClassCacheFileRule(LoginResponse.class,
                new CacheFileRule(DiskCacheConst.Login.FILE_NAME, DiskCacheConst.Login.DIR_NAME, false));
    }

    private void initAccountService() {
        ServiceFactory.getInstance().setAccountService(new IAccountService() {
            @Override
            public String getAccountId() {
                if (null != mUserInfo) {
                    return mUserInfo.getUserId() + "";
                }
                return null;
            }

            @Override
            public String getToken() {
                if (null != mUserInfo) {
                    return mUserInfo.getAccessToken();
                }
                return null;
            }

            @Override
            public String getDisplayName() {
                return null;
            }
        });
    }

    private void initRichEditor() {
        XRichText.getInstance().setImageLoader((imagePath, imageView, imageHeight, root, isLocalUpload)
                -> XRichEditorUtil.loadImage(getApplicationContext(), imagePath, imageView, imageHeight, root, isLocalUpload));
    }
}
