package com.sun.demo2.update.ivew;

import com.sun.base.base.iview.IAddPresenterView;
import com.sun.base.net.exception.ApiException;
import com.sun.demo2.update.model.GetUpdateInfoResponse;

public interface IGetUpdateInfoView extends IAddPresenterView {

    void onGetUpdateInfoReturned(GetUpdateInfoResponse getUpdateInfoResponse);

    void onGetUpdateInfoError(ApiException e);
}
