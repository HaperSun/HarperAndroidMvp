package com.sun.demo2.present;


import com.sun.base.net.MvpSafetyObserver;
import com.sun.base.net.NetWork;
import com.sun.base.net.exception.ApiException;
import com.sun.base.net.response.Response;
import com.sun.base.presenter.BasePresenter;
import com.sun.demo2.iview.ILoginView;
import com.sun.demo2.manager.LoginManager;
import com.sun.demo2.model.response.LoginResponse;

import java.util.Map;

import retrofit2.adapter.rxjava2.Result;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 登陆
 */

public class LoginPresenter extends BasePresenter<ILoginView> {

    public LoginPresenter(ILoginView view) {
        super(view);
    }

    /**
     * 登录请求
     */
    public void getLoginInfo(String loginName, String password) {
        NetWork.getInstance()
                .sendRequest(LoginManager.getLoginInfo(loginName, password))
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


    /**
     * 获取隐患列表数据
     */
    public void getRiskList(Map<String, String> map) {
        NetWork.getInstance()
                .sendRequest(LoginManager.getRiskList(map))
                .subscribe(new MvpSafetyObserver<Result<Response>>(mView) {
                    @Override
                    protected void onSuccess(Result<Response> result) {
                        assert result.response() != null;
                        mView.get().onGetRiskListReturned(result.response().body());
                    }

                    @Override
                    protected void onDone() {

                    }

                    @Override
                    public void onError(ApiException e) {
                        mView.get().onGetRiskListError(e);
                    }
                });
    }
}
