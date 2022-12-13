package com.sun.demo2.update.ivew;

import com.sun.base.base.iview.IPresenterView;
import com.sun.base.net.exception.ApiException;
import com.sun.demo2.update.model.GetUpdateInfoResponse;

public interface IGetUpdateInfoView extends IPresenterView {

    void onGetUpdateInfoReturned(GetUpdateInfoResponse getUpdateInfoResponse);

    void onGetUpdateInfoError(ApiException e);
}
