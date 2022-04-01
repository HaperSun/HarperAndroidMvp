package com.sun.demo2.update;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sun.base.base.widget.BaseMvpService;
import com.sun.base.bean.TDevice;
import com.sun.base.net.exception.ApiException;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.common.bean.Constant;
import com.sun.demo2.BuildConfig;
import com.sun.demo2.R;
import com.sun.demo2.event.UpgradeApkDownloadSuccessEvent;
import com.sun.demo2.update.ivew.IGetUpdateInfoView;
import com.sun.demo2.update.model.GetUpdateInfoResponse;
import com.sun.demo2.update.presenter.GetUpdateInfoPresenter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 更新服务
 */
public class UpdateService extends BaseMvpService implements IGetUpdateInfoView {

    private static final String TAG = UpdateService.class.getName();
    private static final String EXTRA_CMD = "cmd";
    public static final int CMD_CHECK_UPDATE = 1;
    public static final int CMD_START_DOWNLOAD = 2;
    public static final int CMD_STOP_DOWNLOAD = 3;
    public static final int CMD_INSTALL = 4;
    public static final int CMD_MANUAL_UPDATE = 5;
    private static String downloadDir;      //更新包要下载下来的目录地址
    private static String downloadFileName; //更新包要下载下来的文件名
    private static String appname;          //更新应用名
    private static String vendor;           //更新应用渠道名
    private static List<OnCheckUpdateListener> sOnCheckUpdateListenerList;
    private static List<OnForceInstallListener> sOnForceInstallListenerList;
    private static List<OnDownloadListener> sOnDownloadListenerList;

    /**
     * 是否是手动点击更新
     */
    private boolean isManualDownload = false;
    /**
     * 是否正在下载
     */
    private volatile boolean isDownloading = false;
    /**
     * 是否是手动检查更新
     */
    private boolean isManualCheckUpdate = false;
    /**
     * 是否取消下载
     */
    private volatile boolean isCancelDownloading = false;

    private GetUpdateInfoPresenter mGetUpdateInfoPresenter;
    private GetUpdateInfoResponse.DataBean mUpdateInfo;

    @IntDef({CMD_CHECK_UPDATE, CMD_START_DOWNLOAD,
            CMD_STOP_DOWNLOAD, CMD_INSTALL, CMD_MANUAL_UPDATE})
    public @interface ServiceCmd {
    }

    public static void start(Context context, @ServiceCmd int cmd) {
        try {
            if (context == null) {
                throw new RuntimeException("UpdateService start context cannot be null!");
            }
            Intent intent = new Intent(context, UpdateService.class);
            intent.putExtra(EXTRA_CMD, cmd);
            context.startService(intent);
        } catch (Exception e) {
            LogHelper.d("UpdateService-------start" + e.getMessage());
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGetUpdateInfoPresenter = new GetUpdateInfoPresenter(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // 分命令 做处理
            int cmd = intent.getIntExtra(EXTRA_CMD, CMD_CHECK_UPDATE);
            switch (cmd) {
                case CMD_CHECK_UPDATE:
                    mGetUpdateInfoPresenter.getGetUpdateInfo();
                    break;
                case CMD_START_DOWNLOAD:
                    isManualDownload = true;
                    if (!isDownloading) {
                        if (mUpdateInfo != null) {
                            downloadApk();
                        } else {
                            mGetUpdateInfoPresenter.getGetUpdateInfo();
                        }
                    }
                    break;
                case CMD_STOP_DOWNLOAD:
                    // 停止下载
                    isManualCheckUpdate = false;
                    isManualDownload = false;
                    isDownloading = false;
                    isCancelDownloading = true;
                    break;
                case CMD_INSTALL:
                    TDevice.installAPK(getApplicationContext(),
                            new File(downloadDir, downloadFileName));
                    break;
                case CMD_MANUAL_UPDATE:
                    isManualCheckUpdate = true;
                    mGetUpdateInfoPresenter.getGetUpdateInfo();
                    break;
                default:
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void dispatchOnNoNewVersionFounded(ApiException e) {
        if (CollectionUtil.isEmpty(sOnCheckUpdateListenerList)) {
            return;
        }
        Iterator<OnCheckUpdateListener> iterator = sOnCheckUpdateListenerList.iterator();
        while (iterator.hasNext()) {
            OnCheckUpdateListener onCheckUpdateListener = iterator.next();
            if (onCheckUpdateListener != null) {
                onCheckUpdateListener.onNoNewVersionFounded(e);
            }
        }
    }

    private void dispatchOnNewVersionFounded(GetUpdateInfoResponse.DataBean updateInfo, boolean isDownloaded) {
        if (CollectionUtil.isEmpty(sOnCheckUpdateListenerList)) {
            return;
        }
        Iterator<OnCheckUpdateListener> iterator = sOnCheckUpdateListenerList.iterator();
        while (iterator.hasNext()) {
            OnCheckUpdateListener onCheckUpdateListener = iterator.next();
            if (onCheckUpdateListener != null) {
                onCheckUpdateListener.onNewVersionFounded(updateInfo, isDownloaded);
            }
        }
    }

    private void dispatchOnForceInstall(GetUpdateInfoResponse.DataBean updateInfo, boolean isDownloaded) {
        if (CollectionUtil.isEmpty(sOnForceInstallListenerList)) {
            return;
        }
        Iterator<OnForceInstallListener> iterator = sOnForceInstallListenerList.iterator();
        while (iterator.hasNext()) {
            OnForceInstallListener onForceInstallListener = iterator.next();
            if (onForceInstallListener != null) {
                onForceInstallListener.onForceInstall(updateInfo, isDownloaded);
            }
        }
    }

    private void dispatchOnDownloadProgress(final int progress) {
        if (CollectionUtil.isEmpty(sOnDownloadListenerList)) {
            return;
        }
        Iterator<OnDownloadListener> iterator = sOnDownloadListenerList.iterator();
        while (iterator.hasNext()) {
            OnDownloadListener onDownloadListener = iterator.next();
            if (onDownloadListener != null) {
                onDownloadListener.onDownloadProgress(progress);
            }
        }
    }

    private void dispatchOnDownloadSuccess() {
        if (CollectionUtil.isEmpty(sOnDownloadListenerList)) {
            return;
        }
        Iterator<OnDownloadListener> iterator = sOnDownloadListenerList.iterator();
        while (iterator.hasNext()) {
            OnDownloadListener onDownloadListener = iterator.next();
            if (onDownloadListener != null) {
                onDownloadListener.onDownloadSuccess();
            }
        }
    }

    private void dispatchOnDownloadError() {
        if (CollectionUtil.isEmpty(sOnDownloadListenerList)) {
            return;
        }
        Iterator<OnDownloadListener> iterator = sOnDownloadListenerList.iterator();
        while (iterator.hasNext()) {
            OnDownloadListener onDownloadListener = iterator.next();
            if (onDownloadListener != null) {
                onDownloadListener.onDownloadError();
            }
        }
    }

    @Override
    public void onGetUpdateInfoReturned(GetUpdateInfoResponse getUpdateInfoResponse) {
        if (!getUpdateInfoResponse.isOK()) {
            int code = getUpdateInfoResponse.code;
            if (code == GetUpdateInfoResponse.CODE_NO_UPDATE_INFO) {
                dispatchOnNoNewVersionFounded(null);
            }
            return;
        }
        mUpdateInfo = getUpdateInfoResponse.getData();
        if (null == mUpdateInfo) {
            return;
        }
        //先比较更新信息的version和应用的version
        //小于等于的话认为没有更新信息
        if (mUpdateInfo.getVersion() <= BuildConfig.VERSION_CODE) {
            dispatchOnNoNewVersionFounded(null);
            return;
        }
        //下载App
        prepareDownLoad();
    }

    @Override
    public void onGetUpdateInfoError(ApiException e) {
        //调用检查更新接口出错，统一认为没有发现新版本
        int code = e.getCode();
        if (code == GetUpdateInfoResponse.CODE_NO_UPDATE_INFO) {
            dispatchOnNoNewVersionFounded(null);
        } else {
            dispatchOnNoNewVersionFounded(e);
        }
//        test();
    }

    private void test() {
        mUpdateInfo = new GetUpdateInfoResponse.DataBean();
        mUpdateInfo.setVersion(1.2);
        mUpdateInfo.setType(2);
        String url = "https://zhixue-ugc.oss-cn-hangzhou.aliyuncs.com/xxzy/commonResource/2020/07/15//apk/ICola_Teacher_v2.9.3.apk";
        mUpdateInfo.setUrl(url);
        mUpdateInfo.setInfo("修复bug");
        prepareDownLoad();
    }

    private void prepareDownLoad() {
        File file = new File(downloadDir, downloadFileName);
        String version = null;
        if (file.exists()) {
            version = BuildConfig.VERSION_NAME;
        }
        // apk文件不可用
        boolean apkFileCannotUse = !file.exists() || !TextUtils.equals(version, "v" + mUpdateInfo.getVersion());
        LogHelper.d(TAG, "apkFileCannotUse-->" + apkFileCannotUse);
        if (!apkFileCannotUse) {
            //最新的更新包已经下载下来了
            dispatchOnNewVersionFounded(mUpdateInfo, true);
            return;
        }
        //更新包不存在或者更新包不是最新的包

        if (isManualDownload) {// 手动点击下载
            if (!isDownloading) {
                downloadApk();
            }
            return;
        }

        if (isManualCheckUpdate) {// 手动点击检测更新，不论是什么类型的更新，都得弹框提醒
            dispatchOnNewVersionFounded(mUpdateInfo, false);
            return;
        }
        dispatchOnNewVersionFounded(mUpdateInfo, false);
    }

    private void downloadApk() {
        new Thread(() -> {
            LogHelper.d(TAG, "run: 开始下载新的apk包-->" + mUpdateInfo.getUrl());
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            File apkFile = null;

            try {
                isDownloading = true;
                isCancelDownloading = false;
                URL url = new URL(mUpdateInfo.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestProperty("Accept-Encoding", "musixmatch");
                urlConnection.setRequestMethod("GET");
                // TODO: 2017/12/6 断点续传属性设置
                urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");

                inputStream = urlConnection.getInputStream();

                File file = FileUtil.getExternalFileDir(getContext(), Constant.DirName.TEMP);
                apkFile = new File(file, getContext().getString(R.string.app_name) + "_" + mUpdateInfo.getVersion());
                if (apkFile.exists()) {
                    apkFile.delete();
                }
                apkFile.createNewFile();
                fileOutputStream = new FileOutputStream(apkFile);

                int totalSize = urlConnection.getContentLength();
                long downloadedSize = 0;
                int lastProgress = 0;
                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (isCancelDownloading) {
                        return;
                    }
                    fileOutputStream.write(buffer, 0, length);
                    downloadedSize += length;
                    int currProgress = (int) (downloadedSize * 100f / totalSize);
                    if (currProgress - lastProgress >= 1) {
                        LogHelper.d(TAG, "run: 下载进度 " + currProgress);
                        dispatchOnDownloadProgress(currProgress);
                        lastProgress = currProgress;
                    }
                }

                // 下载完成
                dispatchOnDownloadSuccess();
                LogHelper.d(TAG, "run: 下载完成，发送安装事件");
                EventBus.getDefault().post(new UpgradeApkDownloadSuccessEvent(mUpdateInfo));
            } catch (IOException e) {
                LogHelper.e(TAG, "IOException", e);
                dispatchOnDownloadError();
            } finally {
                isManualDownload = false;
                isDownloading = false;
                isManualCheckUpdate = false;
                isCancelDownloading = false;
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    LogHelper.e(TAG, "Exception", e);
                }
            }
        }).start();
    }

    public interface OnCheckUpdateListener {

        /**
         * 发现新版本
         *
         * @param updateInfo   新版本信息
         * @param isDownloaded 是否已经下载下来
         */
        void onNewVersionFounded(GetUpdateInfoResponse.DataBean updateInfo, boolean isDownloaded);

        /**
         * 未发现新版本
         *
         * @param e 检查更新api异常信息 如果不为空，代表调用检查更新接口出问题了（接口本身问题或者网络问题）
         */
        void onNoNewVersionFounded(ApiException e);
    }

    public interface OnForceInstallListener {
        void onForceInstall(GetUpdateInfoResponse.DataBean updateInfo, boolean isDownloaded);
    }

    public interface OnDownloadListener {

        void onDownloadError();

        void onDownloadProgress(int progress);

        void onDownloadSuccess();
    }


    /**
     * 初始化更新下载参数，使用更新服务，务必先调用此方法，一般放在Application的onCreate方法中
     *
     * @param downloadDir      更新包要下载下来的目录地址
     * @param downloadFileName 更新包要下载下来的文件名
     * @param appname          更新应用名
     * @param vendor           更新应用渠道名
     */
    public static void initDownloadParams(@NonNull String downloadDir, @NonNull String downloadFileName,
                                          @NonNull String appname, @NonNull String vendor) {
        UpdateService.downloadDir = downloadDir;
        UpdateService.downloadFileName = downloadFileName;
        UpdateService.appname = appname;
        UpdateService.vendor = vendor;
    }

    public static boolean addOnCheckUpdateListener(OnCheckUpdateListener onCheckUpdateListener) {
        if (onCheckUpdateListener == null) {
            return false;
        }
        if (sOnCheckUpdateListenerList == null) {
            sOnCheckUpdateListenerList = new ArrayList<>();
        }
        if (!sOnCheckUpdateListenerList.contains(onCheckUpdateListener)) {
            return sOnCheckUpdateListenerList.add(onCheckUpdateListener);
        }
        return false;
    }

    public static boolean removeOnCheckUpdateListener(OnCheckUpdateListener onCheckUpdateListener) {
        if (onCheckUpdateListener == null) {
            return false;
        }
        if (CollectionUtil.isEmpty(sOnCheckUpdateListenerList)) {
            return false;
        }
        return sOnCheckUpdateListenerList.remove(onCheckUpdateListener);
    }

    public static boolean addOnForceInstallListener(OnForceInstallListener onForceInstallListener) {
        if (onForceInstallListener == null) {
            return false;
        }
        if (sOnForceInstallListenerList == null) {
            sOnForceInstallListenerList = new ArrayList<>();
        }
        if (!sOnForceInstallListenerList.contains(onForceInstallListener)) {
            return sOnForceInstallListenerList.add(onForceInstallListener);
        }
        return false;
    }

    public static boolean removeOnForceInstallListener(OnForceInstallListener onForceInstallListener) {
        if (onForceInstallListener == null) {
            return false;
        }
        if (CollectionUtil.isEmpty(sOnForceInstallListenerList)) {
            return false;
        }
        return sOnForceInstallListenerList.remove(onForceInstallListener);
    }

    public static boolean addOnDownloadListener(OnDownloadListener onDownloadListener) {
        if (onDownloadListener == null) {
            return false;
        }
        if (sOnDownloadListenerList == null) {
            sOnDownloadListenerList = new ArrayList<>();
        }
        if (!sOnDownloadListenerList.contains(onDownloadListener)) {
            return sOnDownloadListenerList.add(onDownloadListener);
        }
        return false;
    }

    public static boolean removeOnDownloadListener(OnDownloadListener onDownloadListener) {
        if (onDownloadListener == null) {
            return false;
        }
        if (CollectionUtil.isEmpty(sOnDownloadListenerList)) {
            return false;
        }
        return sOnDownloadListenerList.remove(onDownloadListener);
    }
}
