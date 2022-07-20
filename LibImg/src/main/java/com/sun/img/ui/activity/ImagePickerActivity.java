package com.sun.img.ui.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.manager.SelectionManager;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.MediaFileUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.base.util.TimeHelp;
import com.sun.common.bean.MediaFile;
import com.sun.common.executors.CommonExecutor;
import com.sun.common.toast.ToastHelper;
import com.sun.common.util.DataUtil;
import com.sun.common.util.RotateUtils;
import com.sun.common.util.ViewUtils;
import com.sun.img.ImagePicker;
import com.sun.img.R;
import com.sun.img.databinding.ActivityImagePickerBinding;
import com.sun.img.i.MediaLoadCallback;
import com.sun.img.manager.ConfigManager;
import com.sun.img.model.bean.MediaFolder;
import com.sun.img.provider.ImagePickerProvider;
import com.sun.img.task.ImageLoadTask;
import com.sun.img.task.MediaLoadTask;
import com.sun.img.task.VideoLoadTask;
import com.sun.img.ui.adapter.ImagePickerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 多图选择器主页面
 */
public class ImagePickerActivity extends BaseMvpActivity<ActivityImagePickerBinding> implements ImagePickerAdapter.OnItemClickListener {

    /**
     * 启动参数
     */
    private String mTitle;
    private boolean isShowCamera;
    private boolean isShowImage;
    private boolean isShowVideo;
    private boolean isSingleType;
    private int mMaxCount;
    private List<String> mImagePaths;

    private ProgressDialog mProgressDialog;
    private GridLayoutManager mGridLayoutManager;
    private ImagePickerAdapter mImagePickerAdapter;
    //图片数据源
    private List<MediaFile> mMediaFileList;
    //文件夹数据源
    private List<MediaFolder> mMediaFolderList;
    //是否显示时间
    private boolean isShowTime;
    //表示屏幕亮暗
    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_ON = 1;

    private Handler mMyHandler = new Handler();
    private Runnable mHideRunnable = () -> hideImageTime();

    //用于在大图预览页中点击提交按钮标识
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;
    //拍照相关
    private String mFilePath;
    //点击拍照标识
    private static final int REQUEST_CODE_CAPTURE = 0x02;
    //权限相关
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x03;


    @Override
    public int layoutId() {
        return R.layout.activity_image_picker;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.cl_323232;
        mTitleColor = R.color.cl_323232;
        return true;
    }

    @Override
    public void initView() {
        mTitle = ConfigManager.getInstance().getTitle();
        isShowCamera = ConfigManager.getInstance().isShowCamera();
        isShowImage = ConfigManager.getInstance().isShowImage();
        isShowVideo = ConfigManager.getInstance().isShowVideo();
        isSingleType = ConfigManager.getInstance().isSingleType();
        mMaxCount = ConfigManager.getInstance().getMaxCount();
        //载入历史选择记录
        mImagePaths = ConfigManager.getInstance().getImagePaths();
        if (mImagePaths != null && !mImagePaths.isEmpty()) {
            SelectionManager.getInstance().addImagePathsToSelectList(mImagePaths);
        } else {
            SelectionManager.getInstance().removeAll();
        }
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.scanner_image));
        mBaseBind.title.setTitle(TextUtils.isEmpty(mTitle) ? "" : mTitle);
        mGridLayoutManager = new GridLayoutManager(this, 4);
        bind.rvMainImages.setLayoutManager(mGridLayoutManager);
        //注释说当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        bind.rvMainImages.setHasFixedSize(true);
        bind.rvMainImages.setItemViewCacheSize(60);
        bind.layoutActionBar.bringToFront();

        mMediaFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mMediaFileList);
        mImagePickerAdapter.setOnItemClickListener(this);
        bind.rvMainImages.setAdapter(mImagePickerAdapter);
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());
        //进行权限的判断
        boolean hasPermission = PermissionUtil.checkCamera();
        if (!hasPermission) {
            PermissionUtil.requestCamera(this, state -> {
                if (state) {
                    startScannerTask();
                }
            });
        } else {
            startScannerTask();
        }
    }

    @Override
    public void initData() {
        bind.tvActionBarCommit.setOnClickListener(v -> commitSelection());
        bind.selectContainer.setOnClickListener(view -> {
            if (mCurrentType == RotateUtils.CLICK_ARROW_DOWN) {
                showDirectoryList();
            } else if (mCurrentType == RotateUtils.CLICK_ARROW_UP) {
                hideDirectoryList();
            }
        });
        bind.rvMainImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateImageTime();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateImageTime();
            }
        });

        bind.viewMask.setOnClickListener(view -> hideDirectoryList());
    }

    /**
     * 显示文件夹
     */
    private int mCurrentType = RotateUtils.CLICK_ARROW_DOWN;

    private void showDirectoryList() {
        setLightMode(LIGHT_OFF);
        mCurrentType = RotateUtils.CLICK_ARROW_UP;
        RotateUtils.rotateArrow(bind.ivArrow, mCurrentType);
        int height = ViewUtils.getHeight(bind.switchDirectory);
        ObjectAnimator animator = ObjectAnimator.ofFloat(bind.switchDirectory, "translationY", -height, 0).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                bind.switchDirectory.setVisibility(View.VISIBLE);
                bind.viewMask.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    /**
     * 隐藏文件夹
     */
    public void hideDirectoryList() {
        setLightMode(LIGHT_ON);
        mCurrentType = RotateUtils.CLICK_ARROW_DOWN;
        RotateUtils.rotateArrow(bind.ivArrow, mCurrentType);
        ObjectAnimator animator = ObjectAnimator.ofFloat(bind.switchDirectory, "translationY",
                0, -bind.switchDirectory.getHeight()).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bind.switchDirectory.setVisibility(View.GONE);
                bind.viewMask.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    /**
     * 权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults.length >= 1) {
                int cameraResult = grantResults[0];//相机权限
                int sdResult = grantResults[1];//sd卡权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                boolean sdGranted = sdResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                if (cameraGranted && sdGranted) {
                    //具有拍照权限，sd卡权限，开始扫描任务
                    startScannerTask();
                } else {
                    //没有权限
                    ToastHelper.showToast(R.string.permission_tip);
                    finish();
                }
            }
        }
    }


    /**
     * 开启扫描任务
     */
    private void startScannerTask() {
        Runnable mediaLoadTask = null;

        //照片、视频全部加载
        if (isShowImage && isShowVideo) {
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }

        //只加载视频
        if (!isShowImage && isShowVideo) {
            mediaLoadTask = new VideoLoadTask(this, new MediaLoader());
        }
        //只加载图片
        if (isShowImage && !isShowVideo) {
            mediaLoadTask = new ImageLoadTask(this, new MediaLoader());
        }

        //不符合以上场景，采用照片、视频全部加载
        if (mediaLoadTask == null) {
            mediaLoadTask = new MediaLoadTask(this, new MediaLoader());
        }
        CommonExecutor.getInstance().execute(mediaLoadTask);
    }


    /**
     * 处理媒体数据加载成功后的UI渲染
     */
    class MediaLoader implements MediaLoadCallback {

        @Override
        public void loadMediaSuccess(final List<MediaFolder> mediaFolderList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mediaFolderList.isEmpty()) {
                        //默认加载全部照片
                        mMediaFileList.addAll(mediaFolderList.get(0).getMediaFileList());
                        mImagePickerAdapter.notifyDataSetChanged();

                        //图片文件夹数据
                        mMediaFolderList = new ArrayList<>(mediaFolderList);
                        bind.switchDirectory.setData(mMediaFolderList);
                        bind.switchDirectory.setOnImageFolderChangeListener((view, position) -> {
                            MediaFolder mediaFolder = mMediaFolderList.get(position);
                            //更新当前文件夹名
                            String folderName = mediaFolder.getFolderName();
                            if (!TextUtils.isEmpty(folderName)) {
                                bind.tvSelect.setText(folderName);
                            }
                            //更新图片列表数据源
                            mMediaFileList.clear();
                            mMediaFileList.addAll(mediaFolder.getMediaFileList());
                            mImagePickerAdapter.notifyDataSetChanged();
                            hideDirectoryList();
                        });
                        updateCommitButton();
                    }
                    mProgressDialog.cancel();
                }
            });
        }
    }


    /**
     * 隐藏时间
     */
    private void hideImageTime() {
        if (isShowTime) {
            isShowTime = false;
            ObjectAnimator.ofFloat(bind.tvImageTime, "alpha", 1, 0).setDuration(300).start();
        }
    }

    /**
     * 显示时间
     */
    private void showImageTime() {
        if (!isShowTime) {
            isShowTime = true;
            ObjectAnimator.ofFloat(bind.tvImageTime, "alpha", 0, 1).setDuration(300).start();
        }
    }

    /**
     * 更新时间
     */
    private void updateImageTime() {
        int position = mGridLayoutManager.findFirstVisibleItemPosition();
        if (position != RecyclerView.NO_POSITION) {
            MediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
            if (mediaFile != null) {
                if (bind.tvImageTime.getVisibility() != View.VISIBLE) {
                    bind.tvImageTime.setVisibility(View.VISIBLE);
                }
                String time = TimeHelp.getImageTime(mediaFile.getDateToken());
                bind.tvImageTime.setText(time);
                showImageTime();
                mMyHandler.removeCallbacks(mHideRunnable);
                mMyHandler.postDelayed(mHideRunnable, 1500);
            }
        }
    }

    /**
     * 设置屏幕的亮度模式
     *
     * @param lightMode
     */
    private void setLightMode(int lightMode) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        switch (lightMode) {
            case LIGHT_OFF:
                layoutParams.alpha = 0.5f;
                break;
            case LIGHT_ON:
                layoutParams.alpha = 1.0f;
                break;
        }
        getWindow().setAttributes(layoutParams);
    }

    /**
     * 点击图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onMediaClick(View view, int position) {
        if (isShowCamera) {
            if (position == 0) {
                if (!SelectionManager.getInstance().isCanChoose()) {
                    ToastHelper.showToast(String.format(getString(R.string.select_image_max), mMaxCount));
                    return;
                }
                showCamera();
                return;
            }
        }

        if (mMediaFileList != null) {
            DataUtil.getInstance().setMediaData(mMediaFileList);
            Intent intent = null;
            if (isShowCamera) {
                ImagePreActivity.start(this, position - 1);
            } else {
                MediaFile mediaFile = mMediaFileList.get(position);
                if (mediaFile.getDuration() > 0) {
                    VideoPreActivity.start(this, position);
                } else {
                    ImagePreActivity.start(this, position);
                }
            }
            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
        }
    }

    /**
     * 选中/取消选中图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onMediaCheck(View view, int position) {
        if (isShowCamera) {
            if (position == 0) {
                if (!SelectionManager.getInstance().isCanChoose()) {
                    ToastHelper.showToast(String.format(getString(R.string.select_image_max), mMaxCount));
                    return;
                }
                showCamera();
                return;
            }
        }

        //执行选中/取消操作
        MediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
        if (mediaFile != null) {
            String imagePath = mediaFile.getPath();
            if (isSingleType) {
                //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
                ArrayList<String> selectPathList = SelectionManager.getInstance().getSelectPaths();
                if (!selectPathList.isEmpty()) {
                    //判断选中集合中第一项是否为视频
                    if (!SelectionManager.isCanAddSelectionPaths(imagePath, selectPathList.get(0))) {
                        //类型不同
                        ToastHelper.showToast(getString(R.string.single_type_choose));
                        return;
                    }
                    if (MediaFileUtil.isVideoFileType(selectPathList.get(0))) {
                        if (TextUtils.equals(imagePath, selectPathList.get(0))) {
                            SelectionManager.getInstance().removeAll();
                            mImagePickerAdapter.notifyDataSetChanged();
                            updateCommitButton();
                        } else {
                            ToastHelper.showToast(getString(R.string.choose_one_video));
                        }
                        return;
                    }
                }
            }
            boolean addSuccess = SelectionManager.getInstance().addImageToSelectList(imagePath);
            if (addSuccess) {
                mImagePickerAdapter.notifyDataSetChanged();
            } else {
                ToastHelper.showToast(String.format(getString(R.string.select_image_max), mMaxCount));
            }
        }
        updateCommitButton();
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectPaths().size();
        if (selectCount == 0) {
            bind.tvActionBarCommit.setEnabled(false);
            bind.tvActionBarCommit.setText(getString(R.string.confirm));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_select_finish_default);
            return;
        }
        if (selectCount < mMaxCount) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_select_finish);
            return;
        }
        if (selectCount == mMaxCount) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_select_finish);
            return;
        }
    }

    /**
     * 跳转相机拍照
     */
    private void showCamera() {

        if (isSingleType) {
            //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
            ArrayList<String> selectPathList = SelectionManager.getInstance().getSelectPaths();
            if (!selectPathList.isEmpty()) {
                if (MediaFileUtil.isVideoFileType(selectPathList.get(0))) {
                    //如果存在视频，就不能拍照了
                    ToastHelper.showToast(getString(R.string.single_type_choose));
                    return;
                }
            }
        }

        //拍照存放路径
        File fileDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        mFilePath = fileDir.getAbsolutePath() + "/IMG_" + System.currentTimeMillis() + ".jpg";

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, ImagePickerProvider.getFileProviderName(this), new File(mFilePath));
        } else {
            uri = Uri.fromFile(new File(mFilePath));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                //通知媒体库刷新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mFilePath)));
                //添加到选中集合
                SelectionManager.getInstance().addImageToSelectList(mFilePath);

                ArrayList<String> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
                setResult(RESULT_OK, intent);
                SelectionManager.getInstance().removeAll();//清空选中记录
                finish();
            }

            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection();
            }
        }
    }

    /**
     * 选择图片完毕，返回
     */
    private void commitSelection() {
        ArrayList<String> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
        if (CollectionUtil.size(list) == 1) {
            intent.putExtra(ImagePicker.EXTRA_SELECT_VIDEO, MediaFileUtil.isVideoFileType(list.get(0)));
        }
        setResult(RESULT_OK, intent);
        SelectionManager.getInstance().removeAll();//清空选中记录
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mImagePickerAdapter.notifyDataSetChanged();
        updateCommitButton();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentType == RotateUtils.CLICK_ARROW_UP) {
            hideDirectoryList();
            return;
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ConfigManager.getInstance().getImageLoader().clearMemoryCache();
            SelectionManager.getInstance().removeAll();//清空选中记录
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}