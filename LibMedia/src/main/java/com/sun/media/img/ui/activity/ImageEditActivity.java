package com.sun.media.img.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.util.DataUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityImageEditBinding;
import com.sun.media.img.ui.adapter.ImagePreViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 相册选择——图片编辑
 */
public class ImageEditActivity extends BaseMvpActivity<ActivityImageEditBinding> implements View.OnClickListener {

    private List<MediaFile> mMediaFileList;
    private int mPosition = 0;
    private MediaFile mCurMediaFile;

    public static void startForResult(Activity activity, int requestCode, int index) {
        Intent intent = new Intent(activity, ImageEditActivity.class);
        intent.putExtra(Parameter.INDEX, index);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_image_edit;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.cl_323232;
        mTitleColor = R.color.cl_323232;
        return true;
    }

    @Override
    protected void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mPosition = intent.getIntExtra(Parameter.INDEX, 0);
        }
    }

    @Override
    public void initView() {
        baseBind.title.setTitle("相册预览");
        baseBind.title.setOnTitleClickListener(view -> onBackPressed());
        vdb.ivCropper.setOnClickListener(this);
        mMediaFileList = DataUtil.getInstance().getMediaData();
        ImagePreViewAdapter mImagePreViewAdapter = new ImagePreViewAdapter(this, mMediaFileList);
        vdb.vpMainPreImage.setAdapter(mImagePreViewAdapter);
        vdb.vpMainPreImage.setCurrentItem(mPosition);
        mCurMediaFile = mMediaFileList.get(mPosition);
    }

    @Override
    public void initData() {
        vdb.vpMainPreImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurMediaFile = mMediaFileList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vdb.ivCropper.setOnClickListener(this);
        vdb.tvActionBarCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mCurMediaFile == null) {
            return;
        }
        int viewId = v.getId();
        if (viewId == R.id.iv_cropper) {
            if (mCurMediaFile.itemType == MediaFile.PHOTO) {
                //图片裁剪
                CropperPhotoActivity.startForResult(this, Parameter.REQUEST_CODE_MEDIA, mCurMediaFile);
            }
        } else if (viewId == R.id.tv_actionBar_commit) {
            Intent intent = new Intent();
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            mediaFiles.add(mCurMediaFile);
            intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
            setResult(Parameter.RESULT_CODE_MEDIA, intent);
            close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Parameter.REQUEST_CODE_MEDIA && resultCode == Parameter.RESULT_CODE_MEDIA) {
                Intent intent = new Intent();
                ArrayList<MediaFile> mediaFiles = data.getParcelableArrayListExtra(Parameter.FILE_PATH);
                intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
                setResult(Parameter.RESULT_CODE_MEDIA, intent);
                close();
            }
        }
    }
}