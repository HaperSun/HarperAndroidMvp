package com.sun.demo2.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.google.gson.Gson;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.db.entity.UserInfo;
import com.sun.base.db.manager.UserInfoManager;
import com.sun.base.disk.DiskCacheManager;
import com.sun.base.net.exception.ApiException;
import com.sun.base.net.response.Response;
import com.sun.base.net.state.NetStateChangeObserver;
import com.sun.base.net.state.NetworkStateChangeReceiver;
import com.sun.base.net.state.NetworkUtil;
import com.sun.base.util.StringUtil;
import com.sun.base.util.TimeUtil;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityLoginBinding;
import com.sun.demo2.iview.ILoginView;
import com.sun.demo2.model.DataBean;
import com.sun.demo2.model.response.LoginResponse;
import com.sun.demo2.present.LoginPresenter;
import com.sun.demo2.util.sp.LoginInfoSp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: Harper
 * @date: 2021/12/6
 * @note:
 */
public class LoginActivity extends BaseMvpActivity<ActivityLoginBinding> implements ILoginView,
        NetStateChangeObserver, View.OnClickListener {

    private Context mContext;
    private LoginPresenter mLoginPresenter;
    private long mTime;
    private boolean mHasNetwork;
    private boolean mShowPassword;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initIntent() {
        if (mLoginPresenter == null) {
            mLoginPresenter = new LoginPresenter(this);
        }
    }

    @Override
    public void initView() {
        mContext = LoginActivity.this;
    }

    @Override
    public void initData() {
        mHasNetwork = NetworkUtil.hasNet();
        if (mHasNetwork) {
            showToast("当前网络可用~");
        }
        NetworkStateChangeReceiver.registerObserver(this);
        bind.ivClearAccount.setOnClickListener(this);
        bind.ivSwitchPassword.setOnClickListener(this);
        bind.btLogin.setOnClickListener(this);
        bind.etAccount.setText("13805242701");
        bind.etPassword.setText("111111a");
        listTest();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_clear_account:
                bind.etAccount.setText("");
                break;
            case R.id.iv_switch_password:
                if (mShowPassword) {
                    bind.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    bind.ivSwitchPassword.setImageResource(R.mipmap.ic_show_psw_press);
                } else {
                    bind.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    bind.ivSwitchPassword.setImageResource(R.mipmap.ic_show_psw);
                }
                mShowPassword = !mShowPassword;
                break;
            case R.id.bt_login:
                doLogin();
                break;
            default:
                break;
        }
    }

    private void listTest() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.clear();
        list.add("bb");
        List<DataBean> dataBeans = new ArrayList<>();
        dataBeans.add(new DataBean());
        dataBeans.add(null);
        if (dataBeans.get(1)== null){
            showToast("aa");
        }else {
            showToast("bb");
        }
    }

    private void deleteElementFromList() {
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
        mHasNetwork = false;
        showToast("网络断开了~");
    }

    @Override
    public void onNetConnected(String networkType) {
        mHasNetwork = true;
        showToast(networkType);
    }

    private void doLogin() {
        String account = bind.etAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showToast(R.string.input_user_account);
            bind.etAccount.requestFocus();
            return;
        }
        if (!StringUtil.isLegalPhone(account)) {
            showToast(R.string.account_not_exist);
            bind.etAccount.requestFocus();
            return;
        }
        String password = bind.etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            showToast(R.string.input_user_password);
            bind.etPassword.requestFocus();
            return;
        }
        if (!StringUtil.isLegalPassword(password)) {
            showToast("密码格式不正确！");
            bind.etPassword.requestFocus();
            return;
        }
        if (!mHasNetwork){
            showToast("当前网络不可用~");
            return;
        }
        showLoadingDialog(R.string.logging);
        mLoginPresenter.getLoginInfo(account, password);

        //风险点接口
        mTime = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("myUserId", "278656");
        map.put("myOrgId", "56");
        map.put("pageSize", "10");
        map.put("loginUserId", "278656");
        map.put("state", "1");
        map.put("enterpriseId", "571");
        map.put("page", "1");
        map.put("isHdNeedApprove", "0");
        mLoginPresenter.getRiskList(map);
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
                showSuccessToast("登陆成功");

                //2、使用磁盘缓存保存登录成功后的信息
                DiskCacheManager.getInstance().saveCacheData(response);

                //3、SharedPreferences保存登录成功后的信息
                LoginInfoSp.getInstance().save(new Gson().toJson(response));
            }
        }
        dismissLoadingDialog();
        jumpToHomepage();
    }

    @Override
    public void onAtLoginError(ApiException e) {
        //三种保存数据的取值
        //1、从本地数据库中取值
        UserInfo userInfo = UserInfoManager.getInstance(this).getCurrentLoginUser();

        //2、使用磁盘缓存获取登录成功后的信息
        LoginResponse loginResponse = DiskCacheManager.getInstance().getCacheData(LoginResponse.class);

        //3、SharedPreferences获取登录成功后的信息
        String json = LoginInfoSp.getInstance().get();
        LoginResponse response1 = new Gson().fromJson(json, LoginResponse.class);

        dismissLoadingDialog();
        jumpToHomepage();
    }

    private void jumpToHomepage(){
        HomepageActivity.start(mContext);
        close();
    }

    @Override
    public void onGetRiskListReturned(Response response) {
        mTime = System.currentTimeMillis() - mTime;
        String time = TimeUtil.long2StringMs(mTime);
        System.out.print(time);
    }

    @Override
    public void onGetRiskListError(ApiException e) {
        System.out.print(e.getDisplayMessage());
    }
}