package com.sun.media.img.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.FileUtil;
import com.sun.base.util.StringUtil;
import com.sun.common.bean.Parameter;
import com.sun.media.R;
import com.sun.media.databinding.ActivityCropperPhotoBinding;

import java.io.File;
import java.util.UUID;

/**
 * @author: Harper
 * @date: 2022/7/20
 * @note: 图片裁剪
 */
public class CropperPhotoActivity extends BaseMvpActivity<ActivityCropperPhotoBinding> implements View.OnClickListener {

    /**
     * 原图片地址
     */
    private String mOriPath;
    /**
     * 裁剪后的地址
     */
    private String mCropperPath;

    public static void startForResult(Context context, String picPath, int requestCode) {
        Intent intent = new Intent(context, CropperPhotoActivity.class);
        intent.putExtra(Parameter.INDEX, picPath);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_cropper_photo;
    }

    @Override
    protected void initIntent() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = window.getAttributes();
        window.setAttributes(params);
        Intent data = getIntent();
        if (null != data) {
            mOriPath = data.getStringExtra(Parameter.PICTURE_PATH);
        } else {
            onBackPressed();
        }
    }

    @Override
    public void initView() {
        Uri uri = Uri.fromFile(new File(mOriPath));
        bind.civImage.setImageUriAsync(uri);
        bind.civImage.setOnCropImageCompleteListener((view, result) -> {
            if (result.isSuccessful()) {
                Intent data = new Intent();
                data.putExtra(Parameter.PICTURE_PATH, mCropperPath);
                setResult(RESULT_OK, data);
                finish();
            } else {
                onBackPressed();
            }
        });
        bind.tvCropperCancel.setOnClickListener(this);
        bind.tvCropperSure.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, null);
        super.onBackPressed();
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
        mCropperPath = cropperFile.getAbsolutePath()
                .concat(File.separator)
                .concat(UUID.randomUUID() + "_" + StringUtil.getDataTime("yyyy-MM-dd HH-mm-ss"))
                .concat(".").concat(ext);
        Uri outputUri = Uri.fromFile(new File(mCropperPath));
        bind.civImage.saveCroppedImageAsync(outputUri, Bitmap.CompressFormat.JPEG, 100);
    }
}