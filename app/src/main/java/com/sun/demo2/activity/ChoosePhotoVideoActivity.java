package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityChoosePhotoVideoBinding;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.manager.MediaConfig;

import java.util.ArrayList;

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
        ArrayList<MediaFile> models = new ArrayList<>();
        bind.msw.initWidgetData(models, () -> MediaSelector.builder(this)
                .operationType(MediaConfig.FROM_ALBUM)
                .mediaFileType(MediaConfig.BOTH)
                .albumCanTakePhoto(false)
                .maxCount(9)
                .maxVideoCount(2)
                .build()
                .show());
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Parameter.REQUEST_CODE_MEDIA && resultCode == Parameter.RESULT_CODE_MEDIA) {
                ArrayList<MediaFile> models = data.getParcelableArrayListExtra(Parameter.FILE_PATH);
                bind.msw.refreshWidgetData(models);
            }
        }
    }
}