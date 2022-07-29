package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityChoosePhotoVideoBinding;
import com.sun.media.camera.CameraActivity;
import com.sun.media.camera.view.CameraView;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.manager.MediaConfig;
import com.sun.media.img.model.PhotoVideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/28
 * @note: 图片或视频的选择和展示
 */
public class ChoosePhotoVideoActivity extends BaseMvpActivity<ActivityChoosePhotoVideoBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, ChoosePhotoVideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_choose_photo_video;
    }

    @Override
    public void initView() {
        bind.tvCamera.setOnClickListener(v -> CameraActivity.start(this, CameraView.TAKE_PHOTO));
        List<PhotoVideoModel> models = new ArrayList<>();
        bind.msw.setWidgetData(models, () -> MediaSelector.builder(this)
                .selectType(MediaConfig.TAKE_PHOTO)
                .build()
                .show());
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            List<PhotoVideoModel> models = new ArrayList<>();
            bind.msw.addMedia(models);
        }
    }
}