package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.telephony.mbms.FileInfo;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityResumeDownloadBinding;
import com.sun.demo2.view.widget.ClearEditText;

import java.util.ArrayList;
import java.util.List;

public class ResumeDownloadActivity extends BaseMvpActivity<ActivityResumeDownloadBinding> {

    RecyclerView recyclerview;
    ClearEditText clearEt;
    Button btnStart;

    private List<FileInfo> mFileList;
    private DelegateAdapter mAdapter;
//    private CommonDelegateAdapter<FileInfo> commonDelegateAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ResumeDownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_resume_download;
    }

    @Override
    public void initView() {
        recyclerview = bind.recyclerview;
        clearEt = bind.clearEt;
        btnStart = bind.btnStart;
    }

    @Override
    public void initData() {
        mFileList = new ArrayList<>();
    }
}