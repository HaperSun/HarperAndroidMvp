package com.sun.demo2.model;

import com.sun.demo2.update.model.GetUpdateInfoResponse;

/**
 * @author Harper
 * @date 2022/12/7
 * note:
 */
public class AppEvent {

    public static class UpgradeApkDownloadSuccessEvent{

        private final GetUpdateInfoResponse.DataBean mUpdateInfo;

        public UpgradeApkDownloadSuccessEvent(GetUpdateInfoResponse.DataBean updateInfo) {
            mUpdateInfo = updateInfo;
        }

        public GetUpdateInfoResponse.DataBean getUpdateInfo() {
            return mUpdateInfo;
        }
    }
}
