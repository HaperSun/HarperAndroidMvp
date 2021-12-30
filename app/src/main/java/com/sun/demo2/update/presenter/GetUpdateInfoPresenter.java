package com.sun.demo2.update.presenter;


import com.sun.base.net.MvpSafetyObserver;
import com.sun.base.net.NetWorks;
import com.sun.base.net.exception.ApiException;
import com.sun.base.presenter.BasePresenter;
import com.sun.demo2.manager.LoginManager;
import com.sun.demo2.update.ivew.IGetUpdateInfoView;
import com.sun.demo2.update.model.GetUpdateInfoResponse;
import com.sun.demo2.update.model.request.GetUpdateInfoRequest;

import retrofit2.adapter.rxjava2.Result;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 获取更新接口信息
 */
public class GetUpdateInfoPresenter extends BasePresenter<IGetUpdateInfoView> {

    public GetUpdateInfoPresenter(IGetUpdateInfoView view) {
        super(view);
    }

    public void getGetUpdateInfo() {
        GetUpdateInfoRequest getUpdateInfoRequest = new GetUpdateInfoRequest();
        NetWorks.getInstance()
                .commonSendRequest(LoginManager.getAppUpdateInfo(getUpdateInfoRequest))
                .subscribe(new MvpSafetyObserver<Result<GetUpdateInfoResponse>>(mView) {
                    @Override
                    protected void onSuccess(Result<GetUpdateInfoResponse> result) {
                        if (!isDetached()) {
                            assert result.response() != null;
                            mView.get().onGetUpdateInfoReturned(result.response().body());
                        }
                    }

                    @Override
                    protected void onDone() {
                        //do nothing
                    }

                    @Override
                    public void onError(ApiException e) {
                        if (!isDetached()) {
                            mView.get().onGetUpdateInfoError(e);
                        }
                    }
                });
    }


}
