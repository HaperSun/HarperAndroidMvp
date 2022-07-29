package com.sun.media.img.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.manager.SelectionManager;
import com.sun.base.util.CommonUtil;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.DataUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityImageEditBinding;
import com.sun.media.img.manager.ConfigManager;
import com.sun.media.img.provider.ImagePickerProvider;
import com.sun.media.img.ui.adapter.ImagePreViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 相册选择——图片编辑
 */
public class ImageEditActivity extends BaseMvpActivity<ActivityImageEditBinding> implements View.OnClickListener {

    private static final int CODE_CROP_IMAGE_REQUEST = 2001;
    private List<MediaFile> mMediaFileList;
    private int mPosition = 0;
    private MediaFile mCurMediaFile;

    public static void start(Context context, int index) {
        Intent intent = new Intent(context, ImageEditActivity.class);
        intent.putExtra(Parameter.INDEX, index);
        context.startActivity(intent);
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
        bind.ivCropper.setOnClickListener(this);
        mMediaFileList = DataUtil.getInstance().getMediaData();
        ImagePreViewAdapter imagePreViewAdapter = new ImagePreViewAdapter(this, mMediaFileList);
        bind.vpMainPreImage.setAdapter(imagePreViewAdapter);
        bind.vpMainPreImage.setCurrentItem(mPosition);
        mCurMediaFile = mMediaFileList.get(mPosition);
        //更新当前页面状态
        setIvPlayShow(mMediaFileList.get(mPosition));
        updateSelectButton(mMediaFileList.get(mPosition).getPath());
        updateCommitButton();
    }

    @Override
    public void initData() {
        bind.vpMainPreImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurMediaFile = mMediaFileList.get(position);
                if (null != mCurMediaFile) {
                    updateSelectButton(mCurMediaFile.getPath());
                    setIvPlayShow(mCurMediaFile);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bind.llPreSelect.setOnClickListener(v -> {
            //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
            if (ConfigManager.getInstance().isSingleType()) {
                ArrayList<String> selectPathList = SelectionManager.getInstance().getSelectPaths();
                if (!selectPathList.isEmpty()) {
                    //判断选中集合中第一项是否为视频
                    if (!SelectionManager.isCanAddSelectionPaths(mMediaFileList.get(bind.vpMainPreImage.getCurrentItem()).getPath(), selectPathList.get(0))) {
                        //类型不同
                        ToastHelper.showToast(R.string.single_type_choose);
                        return;
                    }
                }
            }
            boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(mMediaFileList.get(bind.vpMainPreImage.getCurrentItem()).getPath());
            if (addSuccess) {
                updateSelectButton(mMediaFileList.get(bind.vpMainPreImage.getCurrentItem()).getPath());
                updateCommitButton();
            } else {
                ToastHelper.showToast(String.format(getString(R.string.select_image_max), SelectionManager.getInstance().getMaxCount()));
            }
        });
        bind.tvActionBarCommit.setOnClickListener(v -> {
            int selectCount = SelectionManager.getInstance().getSelectPaths().size();
            if (selectCount == 0) {//没有选时候默认点击完成给当前的图片
                SelectionManager.getInstance().removeAll();
                SelectionManager.getInstance().addImageToSelectList(mCurMediaFile.getPath());
            }
            setResult(RESULT_OK, new Intent());
            finish();
        });
        bind.ivMainPlay.setOnClickListener(v -> {
            //实现播放视频的跳转逻辑(调用原生视频播放器)
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(this, ImagePickerProvider.getFileProviderName(this),
                    new File(mMediaFileList.get(bind.vpMainPreImage.getCurrentItem()).getPath()));
            intent.setDataAndType(uri, "video/*");
            //给所有符合跳转条件的应用授权
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivity(intent);
        });
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        int maxCount = SelectionManager.getInstance().getMaxCount();
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectPaths().size();
        bind.ivCropper.setVisibility((selectCount == 1 || selectCount == 0) ? View.VISIBLE : View.GONE);
        if (selectCount == 0) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(getString(R.string.confirm));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
            return;
        }
        if (selectCount < maxCount) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
            return;
        }
        if (selectCount == maxCount) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
            return;
        }
    }

    /**
     * 更新选择按钮状态
     */
    private void updateSelectButton(String imagePath) {
        boolean isSelect = SelectionManager.getInstance().isImageSelect(imagePath);
        CommonUtil.setViewBackground(bind.ivItemCheck, isSelect ? R.mipmap.icon_image_checked : R.mipmap.icon_image_check);
    }

    /**
     * 设置是否显示视频播放按钮
     *
     * @param mediaFile
     */
    private void setIvPlayShow(MediaFile mediaFile) {
        if (null == mediaFile) {
            return;
        }
        if (mediaFile.getDuration() > 0) {
            bind.ivMainPlay.setVisibility(View.VISIBLE);
        } else {
            bind.ivMainPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_cropper) {
            if (null != mCurMediaFile) {
                //图片裁剪
                CropperPhotoActivity.startForResult(this, mCurMediaFile.getPath(), CODE_CROP_IMAGE_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CODE_CROP_IMAGE_REQUEST) {
            if (null != data) {
                //单张裁剪过后点击裁剪的完成按钮，多张图暂时不支持裁剪
                String mCropImgPath = data.getStringExtra(Parameter.PICTURE_PATH);
                SelectionManager.getInstance().removeAll();
                SelectionManager.getInstance().addImageToSelectList(mCropImgPath);
                setResult(RESULT_OK, new Intent());
                finish();
            }
        }
    }
}