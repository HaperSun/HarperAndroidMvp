package com.sun.base.upload;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sun.base.R;
import com.sun.base.util.AppUtil;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author: Harper
 * @date: 2022/8/2
 * @note: 腾讯cos上传服务工具类
 */
public class UpLoadFileHelper {

    private static final String TAG = UpLoadFileHelper.class.getSimpleName();

    private final static int MSG_UPLOAD_SUCCESS = 1;
    private final static int MSG_UPLOAD_PROCESS = 2;
    private final static int MSG_UPLOAD_FAILED = 3;

    private final Context mContext;
    /**
     * 上传回调
     */
    private UploadFileListener mListener;
    /**
     * 是否取消上传
     */
    private boolean mIsCancelUpload = false;
    /**
     * 当前正在上传的文件下标
     */
    private int mCurUploadIndex = -1;
    /**
     * 等待上传的文件列表
     */
    private List<String> mWaitUploadFileItems;
    /**
     * 上传成功后的文件url
     */
    private List<String> mUploadFileUrls;

    private String mCurrentFilePath;
    //腾讯云存储位置
    private String module;//模块
    private String destPath;
    // 初始化 TransferManager
    private TransferManager mTransferManager;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPLOAD_SUCCESS:
                    if (mListener != null) {
                        mListener.onUploadFileSuccess((List<String>) msg.obj);
                    }
                    break;
                case MSG_UPLOAD_PROCESS:
                    if (mListener != null) {
                        mListener.onUploadFileProcess((Integer) msg.obj);
                    }
                    break;
                case MSG_UPLOAD_FAILED:
                    if (mListener != null) {
                        mListener.onUploadFileFail((Exception) msg.obj);
                    }
                    break;
            }
        }
    };


    public UpLoadFileHelper(String module, String destPath) {
        mContext = AppUtil.getCtx();
        this.module = module;
        this.destPath = destPath;
        initUploadService();
    }

    private void initUploadService() {
        // keyDuration 为请求中的密钥有效期，单位为秒
        QCloudCredentialProvider myCredentialProvider = new ShortTimeCredentialProvider(
                mContext.getString(R.string.qClound_secret_id), mContext.getString(R.string.qClound_secret_key), 300);
        // 创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(mContext.getString(R.string.qClound_region))
                // 使用 HTTPS 请求, 默认为 HTTP 请求
                .isHttps(true)
                .builder();
        // 初始化 COS Service，获取实例
        CosXmlService cosXmlService = new CosXmlService(mContext, serviceConfig, myCredentialProvider);
        // 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        // 初始化 TransferManager
        mTransferManager = new TransferManager(cosXmlService, transferConfig);
    }

    public void setQCloudUploadListener(UploadFileListener listener) {
        this.mListener = listener;
    }


    /**
     * 上传文件到腾讯云
     *
     * @param filePathList 本地文件地址数组
     */
    public void startUpload(List<String> filePathList) {
        if (CollectionUtil.isEmpty(filePathList)) {
            return;
        }
        if (mListener != null) {
            mListener.onUploadFileStart();
        }
        mIsCancelUpload = false;
        mCurUploadIndex = -1;
        if (mWaitUploadFileItems == null) {
            mWaitUploadFileItems = new ArrayList<>();
        } else {
            mWaitUploadFileItems.clear();
        }
        mWaitUploadFileItems.addAll(filePathList);
        if (mUploadFileUrls != null) {
            mUploadFileUrls.clear();
        }

        //批量获取完token之后，开始走阿里云上传逻辑
        new Thread(() -> uploadFile(), TAG + "_UploadThread").start();
    }

    /**
     * 上传文件逻辑
     */
    private void uploadFile() {
        if (!CollectionUtil.isEmpty(mWaitUploadFileItems) && !mIsCancelUpload) {
            mCurUploadIndex++;
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_PROCESS, mCurUploadIndex));
            //取第一个文件开始上传
            mCurrentFilePath = mWaitUploadFileItems.remove(0);
            try {
                //对象在存储桶中的位置标识符，即称对象键
                String cosPath = "mobile/" + module + "/" + destPath + "/android/" + UUID.randomUUID()
                        + "." + FileUtil.getExtension(mCurrentFilePath);
                String srcPath = mCurrentFilePath;
                LogHelper.i(TAG, "ossupload start_filepath:" + mCurrentFilePath);
                //若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
                String uploadId = null;
                // 上传文件
                COSXMLUploadTask cosxmlUploadTask = mTransferManager.upload(mContext.getString(R.string.qClound_bucket),
                        cosPath, srcPath, uploadId);
                //设置上传进度回调
                cosxmlUploadTask.setCosXmlProgressListener((complete, target) -> {
                    // todo Do something to update progress...
                });
                //设置返回结果回调
                cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                    @Override
                    public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                        String onLineUrl = result.accessUrl;
                        LogHelper.d("cosxmlUploadTask_onSuccess", onLineUrl);
                        if (mUploadFileUrls == null) {
                            mUploadFileUrls = new ArrayList<>();
                        }
                        mUploadFileUrls.add(onLineUrl);
                        uploadFile();
                    }

                    @Override
                    public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                        if (clientException != null) {
                            clientException.printStackTrace();
                            LogHelper.d("cosxmlUploadTask_fail", clientException.getMessage());
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_FAILED, clientException));
                        } else {
                            serviceException.printStackTrace();
                            LogHelper.d("cosxmlUploadTask_fail", serviceException.getMessage());
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_FAILED, serviceException));
                        }
                    }
                });
                //设置任务状态回调, 可以查看任务过程
                cosxmlUploadTask.setTransferStateListener(state -> {
                    // todo notify transfer state
                });
            } catch (Exception e) {
                LogHelper.e(TAG, "cosxmlUploadTask Exception", e);
                mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_FAILED, e));
            }
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_SUCCESS,
                    mUploadFileUrls));
        }
    }

    public interface IUploadResultListener {
        void onUploadSuccess(String localPath, String url);

        void onUploadFail(String localPath, Exception e, String text);
    }

    private IUploadResultListener iUploadListener;

    public void setUploadResultListener(IUploadResultListener iUploadListener) {
        this.iUploadListener = iUploadListener;
    }

    /**
     * 上传单个文件逻辑
     */
    public void uploadOneFile(String filePath) {
        try {
            //对象在存储桶中的位置标识符，即称对象键
            String cosPath = "mobile/" + module + "/" + destPath + "/android/" + UUID.randomUUID()
                    + "." + FileUtil.getExtension(filePath);
            String srcPath = filePath;
            LogHelper.i(TAG, "ossupload start_filepath:" + filePath);
            //若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
            String uploadId = null;
            // 上传文件
            COSXMLUploadTask cosxmlUploadTask = mTransferManager.upload(mContext.getString(R.string.qClound_bucket), cosPath, srcPath, uploadId);
            //设置上传进度回调
            cosxmlUploadTask.setCosXmlProgressListener((complete, target) -> {
                // todo Do something to update progress...
            });
            //设置返回结果回调
            cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    String onLineUrl = result.accessUrl;
                    LogHelper.d("cosxmlUploadTask_onSuccess", onLineUrl);
                    if (iUploadListener != null) {
                        iUploadListener.onUploadSuccess(filePath, onLineUrl);
                    }
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                    if (iUploadListener != null) {
                        iUploadListener.onUploadFail(filePath, clientException != null ? clientException : serviceException, "");
                    }
                    if (clientException != null) {
                        LogHelper.d("cosxmlUploadTask_fail", clientException.getMessage());
                    } else {
                        LogHelper.d("cosxmlUploadTask_fail", serviceException.getMessage());
                    }
                }
            });
            //设置任务状态回调, 可以查看任务过程
            cosxmlUploadTask.setTransferStateListener(state -> {
                // todo notify transfer state
            });
        } catch (Exception e) {
            LogHelper.e(TAG, "cosxmlUploadTask Exception", e);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_FAILED, e));
        }
    }

}
