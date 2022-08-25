package com.sun.media.img.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.util.FileUtil;
import com.sun.base.util.StringUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityCropperPhotoBinding;
import com.sun.media.img.crop.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author: Harper
 * @date: 2022/7/20
 * @note: 图片裁剪
 */
public class CropperPhotoActivity extends BaseMvpActivity<ActivityCropperPhotoBinding> implements View.OnClickListener,
        CropImageView.OnCropImageCompleteListener {

    private MediaFile mMediaFile;
    /**
     * 原图片地址
     */
    private String mOriPath;
    /**
     * 裁剪后的地址
     */
    private String mCropperPath;

    public static void startForResult(Activity activity, int requestCode, MediaFile mediaFile) {
        Intent intent = new Intent(activity, CropperPhotoActivity.class);
        intent.putExtra(Parameter.BEAN, mediaFile);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_cropper_photo;
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
        if (null != intent) {
            mMediaFile = intent.getParcelableExtra(Parameter.BEAN);
        }
        if (mMediaFile != null && !TextUtils.isEmpty(mMediaFile.path)){
            mOriPath = mMediaFile.path;
        }else {
            close();
        }
    }

    @Override
    public void initView() {
        baseBind.title.setTitle("相册裁剪");
        baseBind.title.setOnTitleClickListener(view -> onBackPressed());
        Uri uri = Uri.fromFile(new File(mOriPath));
        bind.civImage.setImageUriAsync(uri);
        bind.civImage.setOnCropImageCompleteListener(this);
        bind.tvCropperCancel.setOnClickListener(this);
        bind.tvCropperSure.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cropper_cancel) {
            onBackPressed();
        } else if (id == R.id.tv_cropper_sure) {
            clickCropperSure();
        }
    }

    /**
     * 确认裁剪
     */
    private void clickCropperSure() {
        String ext = FileUtil.getExtension(mOriPath);
        File cropperFile = FileUtil.getExternalCacheDir(this, null);
        mCropperPath = cropperFile.getAbsolutePath().concat(File.separator)
                .concat(UUID.randomUUID() + "_" + StringUtil.getDataTime("yyyy-MM-dd HH-mm-ss"))
                .concat(".").concat(ext);
        Uri outputUri = Uri.fromFile(new File(mCropperPath));
        bind.civImage.saveCroppedImageAsync(outputUri, Bitmap.CompressFormat.JPEG, 100);
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            Intent intent = new Intent();
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            mMediaFile.setFolderName("temp");
            mMediaFile.setMime("image/jpg");
            mMediaFile.path = mCropperPath;
            mediaFiles.add(mMediaFile);
            intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
            setResult(Parameter.RESULT_CODE_MEDIA, intent);
            close();
        } else {
            onBackPressed();
        }
    }
}