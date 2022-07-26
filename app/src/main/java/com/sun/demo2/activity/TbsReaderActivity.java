package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.DownloadUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.base.bean.Constant;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityTbsReaderBinding;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * @author: Harper
 * @date: 2022/3/21
 * @note: 在App内浏览PDF、docx文件
 */
public class TbsReaderActivity extends BaseMvpActivity<ActivityTbsReaderBinding> {

    private String mFileName;
    TbsReaderView mTbsReaderView;

    public static void start(Context context) {
        Intent intent = new Intent(context, TbsReaderActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_tbs_reader;
    }

    @Override
    public void initIntent() {
        mFileName = "民用爆炸物品安全事故（事件）专项应急救援预案1591682851055.docx";
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(mFileName)) {
            showFailToast("文件地址异常~");
        } else {
            if (PermissionUtil.checkWriteStorage()) {
                prepareOpenFile();
            } else {
                PermissionUtil.requestWriteStorage(this, state -> {
                    if (state) {
                        prepareOpenFile();
                    }
                });
            }
        }
    }

    private void prepareOpenFile() {
        mFileName = mFileName.substring(mFileName.lastIndexOf(File.separator) + 1);
        File folderPath = FileUtil.getExternalFileDir(getContext(), Constant.DirName.TEMP);
        String folderPathStr = folderPath.getAbsolutePath();
        String fileUrl = Constant.Url.QI_NIU + mFileName;
        String saveFilePath = folderPathStr + "/" + mFileName;
        DownloadUtil.getInstance().download(fileUrl, saveFilePath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadStart() {
                showLoadingDialog(R.string.downloading);
            }

            @Override
            public void onDownloadSuccess(String path) {
                dismissLoadingDialog();
                openFile(path);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed(Throwable e) {
                dismissLoadingDialog();
            }

            @Override
            public void onDownloadStopped(int progress) {

            }
        });
//        //使用xUtil下载文件到本地
//        RequestParams params = new RequestParams(fileUrl);
//        params.setSaveFilePath(saveFilePath);
//        showLoadingDialog(R.string.downloading);
//        x.http().get(params, new Callback.CacheCallback<File>() {
//            @Override
//            public void onSuccess(File result) {
//                dismissLoadingDialog();
//                openFile(saveFilePath);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                dismissLoadingDialog();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                dismissLoadingDialog();
//            }
//
//            @Override
//            public void onFinished() {
//                dismissLoadingDialog();
//            }
//
//            @Override
//            public boolean onCache(File result) {
//                return false;
//            }
//        });
    }

    private void openFile(String localFileName) {
        mTbsReaderView = new TbsReaderView(this, (integer, o, o1) -> {
        });
        bind.fragmentContainer.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        File file = new File(localFileName);
        if (!file.exists()) {
            showToast("文件不存在~");
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", localFileName);
        String str = Environment.getExternalStorageDirectory().getPath();
        bundle.putString("tempPath", str);
        boolean result = mTbsReaderView.preOpen(localFileName, false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    @Override
    protected void onDestroy() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
        super.onDestroy();
    }
}