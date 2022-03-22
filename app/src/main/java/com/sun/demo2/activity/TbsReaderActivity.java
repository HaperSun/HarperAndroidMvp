package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.FileUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.common.bean.Constant;
import com.sun.common.bean.MagicInt;
import com.sun.common.toast.CustomToast;
import com.sun.common.toast.ToastHelper;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityTbsReaderBinding;
import com.tencent.smtt.sdk.TbsReaderView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * @author: Harper
 * @date: 2022/3/21
 * @note: 在App内浏览PDF、docx文件
 */
public class TbsReaderActivity extends BaseMvpActivity {

    private ActivityTbsReaderBinding mBind;
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
        mBind = (ActivityTbsReaderBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(mFileName)) {
            ToastHelper.showCustomToast("文件地址异常~", CustomToast.WARNING);
        } else {
            if (PermissionUtil.checkStorage()) {
                prepareOpenFile();
            }else {
                PermissionUtil.requestStorage(this, state -> {
                    if (state == MagicInt.ONE){
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
        RequestParams params = new RequestParams(fileUrl);
        String saveFilePath = folderPathStr + "/" + mFileName;
        params.setSaveFilePath(saveFilePath);
        showLoadingDialog(R.string.downloading);
        x.http().get(params, new Callback.CacheCallback<File>() {
            @Override
            public void onSuccess(File result) {
                dismissLoadingDialog();
                openFile(saveFilePath);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissLoadingDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                dismissLoadingDialog();
            }

            @Override
            public void onFinished() {
                dismissLoadingDialog();
            }

            @Override
            public boolean onCache(File result) {
                return false;
            }
        });
    }

    private void openFile(String localFileName) {
        mTbsReaderView = new TbsReaderView(this, (integer, o, o1) -> {
        });
        mBind.fragmentContainer.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        File file = new File(localFileName);
        if (!file.exists()) {
            ToastHelper.showCommonToast("文件不存在~");
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