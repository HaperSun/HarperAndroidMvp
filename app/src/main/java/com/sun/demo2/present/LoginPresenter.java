package com.sun.demo2.present;


import com.sun.base.net.MvpSafetyObserver;
import com.sun.base.net.NetWork;
import com.sun.base.net.exception.ApiException;
import com.sun.base.presenter.BasePresenter;
import com.sun.demo2.iview.LoginView;
import com.sun.demo2.manager.LoginManager;
import com.sun.demo2.model.response.LoginResponse;

import retrofit2.adapter.rxjava2.Result;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 登陆
 */

public class LoginPresenter extends BasePresenter<LoginView> {

    public LoginPresenter(LoginView view) {
        super(view);
    }

    /**
     * 登录请求
     */
    public void getLoginInfo(String loginName, String password) {
        mView.get().onAtLoginStart();
        NetWork.getInstance()
                .commonSendRequest(LoginManager.getLoginInfo(loginName,password))
                .subscribe(new MvpSafetyObserver<Result<LoginResponse>>(mView) {
                    @Override
                    protected void onSuccess(Result<LoginResponse> result) {
                        assert result.response() != null;
                        mView.get().onAtLoginReturned(result.response().body());
                    }

                    @Override
                    protected void onDone() {

                    }

                    @Override
                    public void onError(ApiException e) {
                        mView.get().onAtLoginError(e);
                    }
                });
    }


}
