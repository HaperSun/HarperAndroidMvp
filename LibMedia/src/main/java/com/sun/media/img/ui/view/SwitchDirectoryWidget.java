package com.sun.media.img.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.media.R;
import com.sun.media.img.model.bean.MediaFolder;
import com.sun.media.img.ui.adapter.SwitchDirectoryAdapter;

import java.util.List;


/**
 * @author: Harper
 * @date: 2022/7/19
 * @note:
 */
public class SwitchDirectoryWidget extends LinearLayout {

    //默认选中文件夹
    private static final int DEFAULT_IMAGE_FOLDER_SELECT = 0;
    private Context mContext;
    private RecyclerView mRvStu;

    public SwitchDirectoryWidget(Context context) {
        this(context, null);
    }

    public SwitchDirectoryWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchDirectoryWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_switch_directory, this);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        mRvStu = findViewById(R.id.rv_stu);
    }

    /**
     * 设置数据
     *
     * @param stuList
     */
    private SwitchDirectoryAdapter mSwitchTabAdapter;

    public void setData(List<MediaFolder> childrenList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSwitchTabAdapter = new SwitchDirectoryAdapter(mContext, childrenList, DEFAULT_IMAGE_FOLDER_SELECT);
        mRvStu.setAdapter(mSwitchTabAdapter);
        mRvStu.setLayoutManager(layoutManager);
        mSwitchTabAdapter.setOnImageFolderChangeListener(new SwitchDirectoryAdapter.OnImageFolderChangeListener() {
            @Override
            public void onImageFolderChange(View view, int position) {
                if (mImageFolderChangeListener != null) {
                    mImageFolderChangeListener.onImageFolderChange(view, position);
                }
            }
        });

    }

    /**
     * 接口回调，Item点击事件
     */
    private OnImageFolderChangeListener mImageFolderChangeListener;

    public void setOnImageFolderChangeListener(OnImageFolderChangeListener onItemClickListener) {
        this.mImageFolderChangeListener = onItemClickListener;
    }

    public interface OnImageFolderChangeListener {
        void onImageFolderChange(View view, int position);
    }
}
