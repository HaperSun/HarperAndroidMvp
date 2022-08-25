package com.sun.media.img.ui.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.executors.CommonExecutor;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.DataUtil;
import com.sun.base.util.MediaFileUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.base.util.RotateUtils;
import com.sun.base.util.TimeHelp;
import com.sun.base.util.ViewUtils;
import com.sun.media.R;
import com.sun.media.camera.CameraActivity;
import com.sun.media.databinding.ActivityImagePickerBinding;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.i.IMediaLoadCallback;
import com.sun.media.img.manager.MediaConfig;
import com.sun.media.img.model.MediaFolder;
import com.sun.media.img.model.bean.AlbumIndexBean;
import com.sun.media.img.task.ImageLoadTask;
import com.sun.media.img.task.MediaLoadTask;
import com.sun.media.img.task.VideoLoadTask;
import com.sun.media.img.ui.adapter.ImagePickerAdapter;
import com.sun.media.video.ui.activity.VideoEditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 多图选择器主页面
 */
public class ImagePickerActivity extends BaseMvpActivity<ActivityImagePickerBinding> implements
        ImagePickerAdapter.OnItemClickListener {

    //表示屏幕亮暗
    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_ON = 1;
    //用于在大图预览页中点击提交按钮标识
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;
    //权限相关
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x03;
    private boolean mOnlySelectImage;
    private boolean mOnlySelectVideo;
    private boolean mSelectBoth;
    private int mMaxCount;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private GridLayoutManager mGridLayoutManager;
    private ImagePickerAdapter mImagePickerAdapter;
    //图片数据源
    private List<MediaFile> mMediaFileList;
    //文件夹数据源
    private List<MediaFolder> mMediaFolderList;
    //是否显示时间
    private boolean isShowTime;
    private Handler mMyHandler = new Handler();
    private Runnable mHideRunnable = () -> hideImageTime();
    private int mSurplusCount;
    private ArrayList<MediaFile> mCurrentSelectedFiles;
    private int mAlreadySelectVideoCount;
    private List<AlbumIndexBean> mAlbumIndexBeans;
    private boolean mAlbumCanTakePhoto;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

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
        mContext = this;
        mMediaFileList = new ArrayList<>();
        mCurrentSelectedFiles = new ArrayList<>();
        mAlbumIndexBeans = new ArrayList<>();
        baseBind.title.setVisibility(View.GONE);
        MediaConfig mediaConfig = MediaSelector.getInstance().config;
        int type = mediaConfig.mediaFileType;
        mAlbumCanTakePhoto = mediaConfig.albumCanTakePhoto;
        mOnlySelectImage = type == MediaConfig.PHOTO;
        mOnlySelectVideo = type == MediaConfig.VIDEO;
        mSelectBoth = type == MediaConfig.BOTH;
        mMaxCount = mediaConfig.maxCount;
        mProgressDialog = ProgressDialog.show(mContext, null, getString(R.string.scanner_image));
        mGridLayoutManager = new GridLayoutManager(mContext, 4);
        bind.rvMainImages.setLayoutManager(mGridLayoutManager);
        //注释说当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        bind.rvMainImages.setHasFixedSize(true);
        bind.rvMainImages.setItemViewCacheSize(60);
        bind.layoutActionBar.bringToFront();
        mImagePickerAdapter = new ImagePickerAdapter(mContext, mMediaFileList);
        mImagePickerAdapter.setOnItemClickListener(this);
        bind.rvMainImages.setAdapter(mImagePickerAdapter);
        bind.ivActionBarBack.setOnClickListener(v -> onBackPressed());
        ArrayList<MediaFile> selectFileList = MediaSelector.getInstance().getSelectedFiles();
        mSurplusCount = mediaConfig.maxCount - CollectionUtil.size(selectFileList);
        mAlreadySelectVideoCount = getAlreadySelectVideoCount(selectFileList);
        setConfirmText();
        initAlbumIndexBean();
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

    private void initAlbumIndexBean() {
        for (int i = 0; i < mSurplusCount; i++) {
            mAlbumIndexBeans.add(new AlbumIndexBean(i + 1 + ""));
        }
    }

    private int getAlreadySelectVideoCount(ArrayList<MediaFile> selectFileList) {
        int count = 0;
        if (CollectionUtil.notEmpty(selectFileList)) {
            for (int i = 0; i < selectFileList.size(); i++) {
                MediaFile file = selectFileList.get(i);
                if (file != null && file.itemType == MediaFile.VIDEO) {
                    count++;
                }
            }
        }
        return count;
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

    private void setConfirmText() {
        if (CollectionUtil.size(mCurrentSelectedFiles) == 0) {
            bind.tvActionBarCommit.setEnabled(false);
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_5e5e5e_radius_dp5);
        } else {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
        }
        bind.tvActionBarCommit.setText(mContext.getString(R.string.confirm_selected_count,
                CollectionUtil.size(mCurrentSelectedFiles), mSurplusCount));
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
                //相机权限
                int cameraResult = grantResults[0];
                //sd卡权限
                int sdResult = grantResults[1];
                //拍照权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;
                //拍照权限
                boolean sdGranted = sdResult == PackageManager.PERMISSION_GRANTED;
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
        if (mSelectBoth) {
            mediaLoadTask = new MediaLoadTask(mContext, new IMediaLoader());
        } else if (mOnlySelectVideo) {
            //只加载视频
            mediaLoadTask = new VideoLoadTask(mContext, new IMediaLoader());
        } else if (mOnlySelectImage) {
            //只加载图片
            mediaLoadTask = new ImageLoadTask(mContext, new IMediaLoader());
        }
        if (mediaLoadTask == null) {
            //不符合以上场景，采用照片、视频全部加载
            mediaLoadTask = new MediaLoadTask(mContext, new IMediaLoader());
        }
        CommonExecutor.getInstance().execute(mediaLoadTask);
    }

    /**
     * 处理媒体数据加载成功后的UI渲染
     */
    class IMediaLoader implements IMediaLoadCallback {

        @Override
        public void loadMediaSuccess(final List<MediaFolder> mediaFolderList) {
            runOnUiThread(() -> {
                if (CollectionUtil.notEmpty(mediaFolderList)) {
                    //默认加载全部数据
                    if (mAlbumCanTakePhoto) {
                        mMediaFileList.add(new MediaFile(MediaFile.BUTTON_CAMERA));
                    }
                    ArrayList<MediaFile> mediaFiles = mediaFolderList.get(0).getMediaFileList();
                    if (CollectionUtil.notEmpty(mediaFiles)) {
                        for (MediaFile file : mediaFiles) {
                            String path = file.getPath();
                            if (!TextUtils.isEmpty(path)) {
                                file.setItemType(MediaFileUtil.isVideoFileType(path) ? MediaFile.VIDEO : MediaFile.PHOTO);
                            }
                        }
                        mMediaFileList.addAll(mediaFiles);
                    }
                    if (CollectionUtil.notEmpty(mMediaFileList)) {
                        mImagePickerAdapter.notifyDataSetChanged();
                    }
                    //某个文件夹中的数据
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
                        if (mAlbumCanTakePhoto) {
                            mMediaFileList.add(new MediaFile(MediaFile.BUTTON_CAMERA));
                        }
                        ArrayList<MediaFile> files = mediaFolder.getMediaFileList();
                        if (CollectionUtil.notEmpty(files)) {
                            for (MediaFile file : files) {
                                String path = file.getPath();
                                if (!TextUtils.isEmpty(path)) {
                                    file.setItemType(MediaFileUtil.isVideoFileType(path) ? MediaFile.VIDEO : MediaFile.PHOTO);
                                }
                            }
                            mMediaFileList.addAll(files);
                        }
                        if (CollectionUtil.notEmpty(mMediaFileList)) {
                            mImagePickerAdapter.notifyDataSetChanged();
                        }
                        hideDirectoryList();
                    });
                    setConfirmText();
                }
                mProgressDialog.cancel();
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
     * @param position
     */
    @Override
    public void onMediaClick(int position, int itemType) {
        if (itemType == MediaFile.BUTTON_CAMERA) {
            CameraActivity.startForResult(this, Parameter.REQUEST_CODE_MEDIA, MediaSelector.getInstance().config.mediaFileType);
        } else {
            MediaFile mediaFile = mMediaFileList.get(position);
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            if (itemType == MediaFile.VIDEO) {
                mediaFiles.add(mediaFile);
                DataUtil.getInstance().setMediaData(mediaFiles);
                VideoEditActivity.startForResult(this,Parameter.REQUEST_CODE_MEDIA);
            } else {
                for (MediaFile file : mMediaFileList) {
                    if (file != null && file.itemType != MediaFile.BUTTON_CAMERA && file.itemType != MediaFile.VIDEO) {
                        mediaFiles.add(file);
                    }
                }
                int index = 0;
                for (int i = 0; i < mediaFiles.size(); i++) {
                    MediaFile file = mediaFiles.get(i);
                    if (file != null && TextUtils.equals(mediaFile.path, file.path)) {
                        index = i;
                        break;
                    }
                }
                DataUtil.getInstance().setMediaData(mediaFiles);
                ImageEditActivity.startForResult(this, Parameter.REQUEST_CODE_MEDIA, index);
            }
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
        MediaFile mediaFile = mMediaFileList.get(position);
        boolean selected = mediaFile.isSelected();
        if (selected) {
            mCurrentSelectedFiles.remove(mediaFile);
            mediaFile.setSelected(false);
            mediaFile.setSelectedIndex(setSelectedFileIndex(mediaFile.getSelectedIndex()));
        } else {
            int fileType = mediaFile.getItemType();
            if (fileType == MediaFile.VIDEO) {
                int selectedVideoCount = 0;
                if (CollectionUtil.notEmpty(mCurrentSelectedFiles)) {
                    for (int i = 0; i < mCurrentSelectedFiles.size(); i++) {
                        MediaFile file = mCurrentSelectedFiles.get(i);
                        if (file != null && file.itemType == MediaFile.VIDEO) {
                            selectedVideoCount++;
                        }
                    }
                }
                int maxVideoSelectCount = MediaSelector.getInstance().config.maxVideoSelectCount;
                if (selectedVideoCount + mAlreadySelectVideoCount >= maxVideoSelectCount) {
                    ToastHelper.showToast(String.format(getString(R.string.select_video_max), maxVideoSelectCount));
                    return;
                }
            }
            if (mCurrentSelectedFiles.size() >= mSurplusCount) {
                ToastHelper.showToast(String.format(getString(R.string.select_max_count), mMaxCount));
                return;
            }
            mCurrentSelectedFiles.add(mediaFile);
            mediaFile.setSelected(true);
            mediaFile.setSelectedIndex(getSelectedFileIndex());
        }
        mImagePickerAdapter.notifyItemChanged(position);
        setConfirmText();
    }

    private String setSelectedFileIndex(String index) {
        if (!TextUtils.isEmpty(index)) {
            for (int i = 0; i < mAlbumIndexBeans.size(); i++) {
                AlbumIndexBean albumIndexBean = mAlbumIndexBeans.get(i);
                if (albumIndexBean != null && TextUtils.equals(index, albumIndexBean.getIndex())) {
                    albumIndexBean.setUsed(false);
                    break;
                }
            }
        }
        return "";
    }

    private String getSelectedFileIndex() {
        String index = "";
        for (int i = 0; i < mAlbumIndexBeans.size(); i++) {
            AlbumIndexBean albumIndexBean = mAlbumIndexBeans.get(i);
            if (albumIndexBean != null && !albumIndexBean.isUsed()) {
                albumIndexBean.setUsed(true);
                index = albumIndexBean.getIndex();
                break;
            }
        }
        return index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Parameter.REQUEST_CODE_MEDIA && resultCode == Parameter.RESULT_CODE_MEDIA) {
                Intent intent = new Intent();
                ArrayList<MediaFile> mediaFiles = data.getParcelableArrayListExtra(Parameter.FILE_PATH);
                intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
                setResult(Parameter.RESULT_CODE_MEDIA, intent);
                finish();
            }
        }
    }

    /**
     * 选择图片完毕，返回
     */
    private void commitSelection() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mCurrentSelectedFiles);
        setResult(Parameter.RESULT_CODE_MEDIA, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImagePickerAdapter.notifyDataSetChanged();
        setConfirmText();
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
}