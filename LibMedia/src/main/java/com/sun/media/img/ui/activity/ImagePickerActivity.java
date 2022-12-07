package com.sun.media.img.ui.activity;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseActivity;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.executors.CommonExecutor;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.DataUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.MediaFileUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.base.util.RotateUtil;
import com.sun.base.util.TimeHelp;
import com.sun.base.util.ViewUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityImagePickerBinding;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.i.IMediaLoadCallback;
import com.sun.media.img.manager.MediaConfig;
import com.sun.media.img.model.MediaFolder;
import com.sun.media.img.model.bean.AlbumIndexBean;
import com.sun.media.img.provider.ImagePickerProvider;
import com.sun.media.img.task.ImageLoadTask;
import com.sun.media.img.task.MediaLoadTask;
import com.sun.media.img.task.VideoLoadTask;
import com.sun.media.img.ui.adapter.ImagePickerAdapter;
import com.sun.media.video.ui.activity.VideoEditActivity;

import java.io.File;
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
    private boolean mOnlySelectImage;
    private boolean mOnlySelectVideo;
    private boolean mSelectBoth;
    private int mMaxCount;
    private Context mContext;
    private BaseActivity mActivity;
    private ProgressDialog mProgressDialog;
    private GridLayoutManager mGridLayoutManager;
    private ImagePickerAdapter mImagePickerAdapter;
    //图片数据源
    private List<MediaFile> mMediaFileList;
    //文件夹数据源
    private List<MediaFolder> mMediaFolderList;
    //是否显示时间
    private boolean isShowTime;
    private final Handler mMyHandler = new Handler();
    private final Runnable mHideRunnable = this::hideImageTime;
    private int mSurplusCount;
    private ArrayList<MediaFile> mCurrentSelectedFiles;
    private int mAlreadySelectVideoCount;
    private List<AlbumIndexBean> mAlbumIndexBeans;
    private boolean mAlbumCanTakePhoto;
    private String mLocalPhotoPath;

    /**
     * 通过Activity启动
     *
     * @param activity    activity
     * @param requestCode requestCode
     */
    public static void startActivityResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 通过Fragment启动
     *
     * @param fragment    fragment
     * @param requestCode requestCode
     */
    public static void startActivityResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), ImagePickerActivity.class);
        fragment.startActivityForResult(intent, requestCode);
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
        mActivity = this;
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
        vdb.rvMainImages.setLayoutManager(mGridLayoutManager);
        //注释说当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        vdb.rvMainImages.setHasFixedSize(true);
        vdb.rvMainImages.setItemViewCacheSize(60);
        vdb.layoutActionBar.bringToFront();
        mImagePickerAdapter = new ImagePickerAdapter(mContext, mMediaFileList);
        mImagePickerAdapter.setOnItemClickListener(this);
        vdb.rvMainImages.setAdapter(mImagePickerAdapter);
        vdb.ivActionBarBack.setOnClickListener(v -> onBackPressed());
        ArrayList<MediaFile> selectFileList = MediaSelector.getInstance().getSelectedFiles();
        mSurplusCount = mediaConfig.maxCount - CollectionUtil.size(selectFileList);
        mAlreadySelectVideoCount = getAlreadySelectVideoCount(selectFileList);
        setConfirmText();
        initAlbumIndexBean();
        //进行权限的判断
        if (!PermissionUtil.checkCamera()) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, Parameter.REQUEST_CODE_PERMISSION_CAMERA);
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
        vdb.tvActionBarCommit.setOnClickListener(v -> commitSelection());
        vdb.selectContainer.setOnClickListener(view -> {
            if (mCurrentType == RotateUtil.CLICK_ARROW_DOWN) {
                showDirectoryList();
            } else if (mCurrentType == RotateUtil.CLICK_ARROW_UP) {
                hideDirectoryList();
            }
        });
        vdb.rvMainImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        vdb.viewMask.setOnClickListener(view -> hideDirectoryList());
    }

    private void setConfirmText() {
        if (CollectionUtil.size(mCurrentSelectedFiles) == 0) {
            vdb.tvActionBarCommit.setEnabled(false);
            vdb.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_5e5e5e_radius_dp5);
        } else {
            vdb.tvActionBarCommit.setEnabled(true);
            vdb.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
        }
        vdb.tvActionBarCommit.setText(mContext.getString(R.string.confirm_selected_count,
                CollectionUtil.size(mCurrentSelectedFiles), mSurplusCount));
    }

    /**
     * 显示文件夹
     */
    private int mCurrentType = RotateUtil.CLICK_ARROW_DOWN;

    private void showDirectoryList() {
        setLightMode(LIGHT_OFF);
        mCurrentType = RotateUtil.CLICK_ARROW_UP;
        RotateUtil.rotateArrow(vdb.ivArrow, mCurrentType);
        int height = ViewUtil.getHeight(vdb.switchDirectory);
        ObjectAnimator animator = ObjectAnimator.ofFloat(vdb.switchDirectory, "translationY", -height, 0).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                vdb.switchDirectory.setVisibility(View.VISIBLE);
                vdb.viewMask.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    /**
     * 隐藏文件夹
     */
    public void hideDirectoryList() {
        setLightMode(LIGHT_ON);
        mCurrentType = RotateUtil.CLICK_ARROW_DOWN;
        RotateUtil.rotateArrow(vdb.ivArrow, mCurrentType);
        ObjectAnimator animator = ObjectAnimator.ofFloat(vdb.switchDirectory, "translationY",
                0, -vdb.switchDirectory.getHeight()).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                vdb.switchDirectory.setVisibility(View.GONE);
                vdb.viewMask.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    /**
     * 权限申请回调
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Parameter.REQUEST_CODE_PERMISSION_CAMERA) {
            if (grantResults.length >= 1) {
                try {
                    //相机权限
                    int cameraResult = grantResults[0];
                    //音频权限
                    int audioResult = grantResults[1];
                    //sd卡读取权限
                    int sdReadResult = grantResults[2];
                    //sd卡写入权限
                    int sdWriteResult = grantResults[3];
                    //拍照权限
                    boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;
                    //音频权限
                    boolean audioGranted = audioResult == PackageManager.PERMISSION_GRANTED;
                    //sd卡读取权限
                    boolean sdReadGranted = sdReadResult == PackageManager.PERMISSION_GRANTED;
                    //sd卡写入权限
                    boolean sdWriteGranted = sdWriteResult == PackageManager.PERMISSION_GRANTED;
                    if (cameraGranted && audioGranted && sdReadGranted && sdWriteGranted) {
                        //具有拍照权限，sd卡权限，开始扫描任务
                        startScannerTask();
                    } else {
                        //没有权限
                        ToastHelper.showToast(R.string.permission_tip);
                        close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                    vdb.switchDirectory.setData(mMediaFolderList);
                    vdb.switchDirectory.setOnImageFolderChangeListener((view, position) -> {
                        MediaFolder mediaFolder = mMediaFolderList.get(position);
                        //更新当前文件夹名
                        String folderName = mediaFolder.getFolderName();
                        if (!TextUtils.isEmpty(folderName)) {
                            vdb.tvSelect.setText(folderName);
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
            ObjectAnimator.ofFloat(vdb.tvImageTime, "alpha", 1, 0).setDuration(300).start();
        }
    }

    /**
     * 显示时间
     */
    private void showImageTime() {
        if (!isShowTime) {
            isShowTime = true;
            ObjectAnimator.ofFloat(vdb.tvImageTime, "alpha", 0, 1).setDuration(300).start();
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
                if (vdb.tvImageTime.getVisibility() != View.VISIBLE) {
                    vdb.tvImageTime.setVisibility(View.VISIBLE);
                }
                String time = TimeHelp.getImageTime(mediaFile.getDateToken());
                vdb.tvImageTime.setText(time);
                showImageTime();
                mMyHandler.removeCallbacks(mHideRunnable);
                mMyHandler.postDelayed(mHideRunnable, 1500);
            }
        }
    }

    /**
     * 设置屏幕的亮度模式
     *
     * @param lightMode lightMode
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
     * @param position 位置
     */
    @Override
    public void onMediaClick(int position, int itemType) {
        if (itemType == MediaFile.BUTTON_CAMERA) {
            //启用本地相机拍照
            takePhoto();
//            //启用CameraView拍照
//            CameraActivity.startActivityResult(this, Parameter.REQUEST_CODE_MEDIA, MediaSelector.getInstance().config.mediaFileType);
        } else {
            MediaFile mediaFile = mMediaFileList.get(position);
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            if (itemType == MediaFile.VIDEO) {
                mediaFiles.add(mediaFile);
                DataUtil.getInstance().setMediaData(mediaFiles);
                VideoEditActivity.startForResult(mActivity, Parameter.REQUEST_CODE_MEDIA);
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
                ImageEditActivity.startForResult(mActivity, Parameter.REQUEST_CODE_MEDIA, index);
            }
        }
    }

    private void takePhoto() {
        //拍照存放路径
        mLocalPhotoPath = FileUtil.getMediaFileName() + File.separator + System.currentTimeMillis() + ".jpg";
        //启用本地相机拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(mContext, ImagePickerProvider.getFileProviderName(mContext), new File(mLocalPhotoPath));
        } else {
            uri = Uri.fromFile(new File(mLocalPhotoPath));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, Parameter.REQUEST_CODE_TAKE_PHOTO);
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
        if (requestCode == Parameter.REQUEST_CODE_TAKE_PHOTO) {
            //拍照成功后通知媒体库刷新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mLocalPhotoPath)));
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFolderName("temp");
            mediaFile.setMime("image/jpg");
            mediaFile.setPath(mLocalPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(mLocalPhotoPath);
            if (bitmap != null) {
                mediaFile.setWidth(bitmap.getWidth());
                mediaFile.setHeight(bitmap.getHeight());
                bitmap.recycle();
            }
            mediaFile.setItemType(MediaFile.PHOTO);
            mediaFiles.add(mediaFile);
            DataUtil.getInstance().setMediaData(mediaFiles);
            //跳转到图片编辑页面
            ImageEditActivity.startForResult(mActivity, Parameter.REQUEST_CODE_MEDIA, 0);
        } else if (data != null) {
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
        if (mCurrentType == RotateUtil.CLICK_ARROW_UP) {
            hideDirectoryList();
            return;
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}