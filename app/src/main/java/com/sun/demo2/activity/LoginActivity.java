package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.disk.DiskCacheManager;
import com.sun.base.net.exception.ApiException;
import com.sun.base.net.state.NetStateChangeObserver;
import com.sun.base.net.state.NetworkStateChangeReceiver;
import com.sun.base.net.state.NetworkUtil;
import com.sun.common.toast.CustomToast;
import com.sun.common.toast.ToastHelper;
import com.sun.db.entity.UserInfo;
import com.sun.db.table.manager.UserInfoManager;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityLoginBinding;
import com.sun.demo2.iview.LoginView;
import com.sun.demo2.model.response.LoginResponse;
import com.sun.demo2.present.LoginPresenter;
import com.sun.demo2.sp.LoginInfoSp;
import com.sun.img.load.ImageLoader;
import com.sun.img.preview.ImagePreviewActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/6
 * @note:
 */
public class LoginActivity extends BaseMvpActivity implements LoginView, NetStateChangeObserver {

    private Context mContext;
    private LoginPresenter mLoginPresenter;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mContext = LoginActivity.this;
        ActivityLoginBinding binding = (ActivityLoginBinding) mViewDataBinding;
        binding.login.setOnClickListener(v -> doLogin());
        binding.commonToast.setOnClickListener(v -> ToastHelper.showCommonToast(R.string.copied_to_pasteboard));
        binding.customToast.setOnClickListener(v -> ToastHelper.showCustomToast(R.string.copied_to_pasteboard));
        String img1 = "https://qiniu.fxgkpt.com/hycg/1639356784663.jpg";
        String img2 = "http://pic.ntimg.cn/file/20180211/7259105_125622777789_2.jpg";
        ImageLoader.getInstance().loadImage(img2, binding.imgView);
        binding.imgView.setOnClickListener(v -> {
            ImagePreviewActivity.actionStart(mContext, img2);
        });
    }

    @Override
    public void initData() {
        if (mLoginPresenter == null) {
            mLoginPresenter = new LoginPresenter(this);
        }
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if ("bb".equals(iterator.next())) {
                iterator.remove();
            }
        }
        System.out.print(list.toString());
        if (NetworkUtil.hasNet()) {
            ToastHelper.showCommonToast("当前网络可用~");
        }
        NetworkStateChangeReceiver.registerObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkStateChangeReceiver.registerReceiver(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetworkStateChangeReceiver.unRegisterReceiver(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkStateChangeReceiver.unRegisterObserver(this);
    }

    @Override
    public void onNetDisconnected() {
        ToastHelper.showCommonToast("网络断开了~");
    }

    @Override
    public void onNetConnected(String networkType) {
        ToastHelper.showCommonToast(networkType);
    }


    private void doLogin() {
        mLoginPresenter.getLoginInfo("用户名", "888888");
    }

    @Override
    public void onAtLoginStart() {

    }

    @Override
    public void onAtLoginReturned(LoginResponse response) {
        if (response != null && response.code == 1) {
            LoginResponse.Object object = response.object;
            if (object != null) {
                //三种保存数据
                //1、登录成功后保存数据到本地数据库中去
                UserInfo userInfo = new UserInfo(object.userName, object.password, object.token,
                        object.id, UserInfo.LoginState.LOGIN);
                UserInfoManager.getInstance(this).loginAndSaveUserInfo(userInfo);
                ToastHelper.showCustomToast("登陆成功", CustomToast.CORRECT);

                //2、使用磁盘缓存保存和获取登录成功后的信息
                DiskCacheManager.getInstance().saveCacheData(response);
                LoginResponse loginResponse = DiskCacheManager.getInstance().getCacheData(LoginResponse.class);

                //3、SharedPreferences保存和获取登录成功后的信息
                new LoginInfoSp().save(new Gson().toJson(response));
                String json = new LoginInfoSp().get();
                LoginResponse response1 = new Gson().fromJson(json, LoginResponse.class);
            }
        }
    }

    @Override
    public void onAtLoginError(ApiException e) {

    }
}