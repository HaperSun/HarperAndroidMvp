package com.sun.demo2;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.sun.base.bean.BaseEvent;
import com.sun.base.db.entity.UserInfo;
import com.sun.base.db.manager.UserInfoManager;
import com.sun.base.disk.CacheFileRule;
import com.sun.base.disk.DiskCacheConst;
import com.sun.base.disk.DiskCacheManager;
import com.sun.base.net.NetWork;
import com.sun.base.net.NetWorks;
import com.sun.base.net.exception.ERROR;
import com.sun.base.service.IAccountService;
import com.sun.base.service.ServiceFactory;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.AppUtil;
import com.sun.base.util.LogHelper;
import com.sun.base.util.XRichEditorUtil;
import com.sun.demo2.activity.LoginActivity;
import com.sun.demo2.model.response.LoginResponse;
import com.sun.demo2.observer.ApplicationObserver;
import com.sun.library.rich.text.XRichText;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.x;

import nl.bravobit.ffmpeg.FFmpeg;

/**
 * @author Harper
 * @date 2021/12/6
 * note:
 */
public class MainApplication extends Application implements UserInfoManager.OnUpdateUserInfoListener,
        UserInfoManager.OnGetCurrentUserInfoListener {

    private MainApplication ctx;
    private UserInfo mUserInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        ctx = this;
        initAppUtil();
        //初始化LogUtil,默认debug模式可打印所有级别的log
        LogHelper.init();
        NetWorks.init();
        NetWork.init();
        //初始化xUtil
        initUtil();
        initUmSdk();
        //将 AccountService 类的实例注册到 ServiceFactory
        initAccountService();
        //初始化富文本控件
        initRichEditor();
        //初始化磁盘缓存
        initDiskCacheManager();
        //初始化腾讯QbSdk
        initQbSdk();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
        FFmpeg.getInstance(ctx).isSupported();
    }

    private void initAppUtil() {
        AppUtil.init(ctx, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME,
                BuildConfig.Base_URL, BuildConfig.Base_URL_H5);
    }

    /**
     * 初始化xUtil
     */
    private void initUtil() {
        x.Ext.init(this);
        // 全局默认信任所有https域名 或 仅添加信任的https域名
        // 使用RequestParams#setHostnameVerifier(...)方法可设置单次请求的域名校验
        x.Ext.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    private void initUmSdk() {
        UMConfigure.init(ctx, ctx.getString(R.string.umeng_sha), "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        PlatformConfig.setWeixin(ctx.getString(R.string.wei_xin_sha), ctx.getString(R.string.wei_xin_sha1));
        PlatformConfig.setWXFileProvider("com.sun.demo2.fileprovider");
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

    private void initQbSdk() {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                LogHelper.e("QbSdk++onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                LogHelper.e("QbSdk++onViewInitFinished" + b);
            }
        };
        QbSdk.initX5Environment(ctx, cb);
    }

    /**
     * 处理 Token 失效
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenInvalid(BaseEvent.TokenInvalidEvent event) {
        if (event.getCode() == ERROR.WRONG_TOKEN && getCurrentUserInfo() != null) {
            UserInfoManager.getInstance(this).clear();
            LoginActivity.start(ctx);
            ToastHelper.showToast(R.string.token_invalid);
        }
    }
}
