package com.sun.demo2.event;


import com.sun.demo2.update.model.GetUpdateInfoResponse;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note:
 */
public class UpgradeApkDownloadSuccessEvent {

    private GetUpdateInfoResponse.DataBean mUpdateInfo;

    public UpgradeApkDownloadSuccessEvent(GetUpdateInfoResponse.DataBean updateInfo) {
        mUpdateInfo = updateInfo;
    }

    public GetUpdateInfoResponse.DataBean getUpdateInfo() {
        return mUpdateInfo;
    }
}
