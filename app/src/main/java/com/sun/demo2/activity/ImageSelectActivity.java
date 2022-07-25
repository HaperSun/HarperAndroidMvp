package com.sun.demo2.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.TDevice;
import com.sun.base.dialog.BottomDialogFragment;
import com.sun.base.manager.SelectionManager;
import com.sun.base.status.StatusBarUtil;
import com.sun.base.util.BitmapUtil;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.base.util.MediaUtils;
import com.sun.common.bean.MediaFile;
import com.sun.common.bean.Parameter;
import com.sun.common.util.DataUtil;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityImageSelectBinding;
import com.sun.demo2.model.LocalVideoModel;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.ImagePicker;
import com.sun.media.img.loader.GlideLoader;
import com.sun.media.img.ui.activity.ImageEditActivity;
import com.sun.media.img.ui.view.ChoosePictureWidget;
import com.sun.media.video.ui.activity.VideoRecordActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/27
 * @note:
 */
public class ImageSelectActivity extends BaseMvpActivity<ActivityImageSelectBinding> implements
        View.OnLongClickListener, ChoosePictureWidget.OnChoosePictureListener, View.OnClickListener {

    private static final int MAX_CHOOSE_COUNT = 9;
    private static final int REQUEST_CODE_RECORD_VIDEO = 1003;
    //作业视频内容最长10分钟，以秒为单位
    public static final int MAX_HOMEWORK_VIDEO_LENGTH = 30;
    //用于在大图预览页中点击提交按钮标识
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;
    private Context mContext;
    private List<ChoosePictureWidget.ImageItem> mGalleryPaths;
    private List<LocalVideoModel> mLocalVideos;

    public static void start(Context context) {
        Intent intent = new Intent(context, ImageSelectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        return true;
    }

    @Override
    public int layoutId() {
        return R.layout.activity_image_select;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        mContext = this;
        bind.ivMusic.setOnClickListener(v -> bind.drawerLayout.openDrawer(Gravity.END));
        bind.tvLeft.setOnClickListener(v -> bind.drawerLayout.closeDrawer(Gravity.START));
        bind.tvRight.setOnClickListener(v -> bind.drawerLayout.closeDrawer(Gravity.END));
        bind.ivMusic.setOnLongClickListener(this);
        initStatusBar();

        bind.container.addContainer.setOnClickListener(this);
        bind.container.cpw.setOnChoosePictureListener(this);
        bind.container.cpw.setMaxChooseCount(9);
    }

    private void initStatusBar() {
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, bind.drawerLayout, ContextCompat.getColor(mContext, R.color.lightPink));
        ViewGroup.LayoutParams layoutParams = bind.viewStatus.getLayoutParams();
        layoutParams.height = StatusBarUtil.getStatusBarHeight(mContext);
        bind.viewStatus.setLayoutParams(layoutParams);
    }

    @Override
    public void initData() {
        setSupportActionBar(bind.toolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setTitle("");

        mGalleryPaths = new ArrayList<>();
        mLocalVideos = new ArrayList<>();
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Menu icon
        if (item.getItemId() == android.R.id.home) {
            bind.drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onLongClick(View v) {
        bind.drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onClick(View v) {
        onShowPublishWay();
    }

    @Override
    public void onPictureChanged(List<ChoosePictureWidget.ImageItem> picList) {
        mGalleryPaths = picList;
        if (CollectionUtil.isEmpty(mGalleryPaths)) {
            bind.container.addContainer.setVisibility(View.VISIBLE);
            bind.container.cpw.setVisibility(View.GONE);
        } else {
            bind.container.addContainer.setVisibility(View.GONE);
            bind.container.cpw.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShowPublishWay() {
        BottomDialogFragment.Builder builder = new BottomDialogFragment.Builder();
        builder.addDialogItem(new BottomDialogFragment.DialogItem(getString(R.string.take_photo), view -> {
            //拍照
            takePicture();
        })).addDialogItem(new BottomDialogFragment.DialogItem(getString(R.string.take_video), view -> {
            //拍视频
            VideoRecordActivity.startForResult(this, REQUEST_CODE_RECORD_VIDEO, MAX_HOMEWORK_VIDEO_LENGTH);
        })).addDialogItem(new BottomDialogFragment.DialogItem(getString(R.string.choose_from_gallery), view -> {
            //从相册选择
            chooseFromGallery();
        })).build().show(getSupportFragmentManager(), TAG);
    }

    private File mImageFile;
    private Uri mImageUri, mImageUriFromFile;
    private static final String FILE_PROVIDER_AUTHORITY = "com.sun.demo2.fileprovider";

    private void takePicture() {
        //打开相机的Intent
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            //创建用来保存照片的文件
            mImageFile = createImageFile();
            mImageUriFromFile = Uri.fromFile(mImageFile);
            LogHelper.i(TAG, "takePhoto: uriFromFile " + mImageUriFromFile);
            if (mImageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    mImageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, mImageFile);
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    mImageUri = Uri.fromFile(mImageFile);
                }
                //将用于输出的文件Uri传递给相机
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                //打开相机
                startActivityForResult(takePhotoIntent, ChoosePictureWidget.REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    /**
     * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
     *
     * @return 创建的图片文件
     */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = FileUtil.getExternalFileDir(mContext, "temp");
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private void chooseFromGallery() {
        ImagePicker.getInstance()
                //设置标题
                .setTitle("")
                //设置是否显示拍照按钮
                .showCamera(false)
                //设置是否展示图片
                .showImage(true)
                //设置是否展示视频
                .showVideo(CollectionUtil.isEmpty(mGalleryPaths))
                //设置是否过滤gif图片
                .filterGif(true)
                //设置最大选择图片数目(默认为1，单选)
                .setMaxCount(MAX_CHOOSE_COUNT - CollectionUtil.size(mGalleryPaths))
                //设置图片视频不能同时选择
                .setSingleType(true)
                //设置自定义图片加载器
                .setImageLoader(new GlideLoader())
                //REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
                .start(this, ChoosePictureWidget.REQUEST_CODE_CHOOSE_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == ChoosePictureWidget.REQUEST_CODE_CHOOSE_FROM_GALLERY || requestCode == REQUEST_CODE_RECORD_VIDEO) {
            boolean isVideo = data.getBooleanExtra(ImagePicker.EXTRA_SELECT_VIDEO, false);
            if (isVideo) {
                List<String> videoList = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                //处理视频
                dealVideo(videoList);
            } else {
                bind.container.cpw.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == ChoosePictureWidget.REQUEST_CODE_TAKE_PHOTO) {
            try {
                /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
                LogHelper.i(TAG, "onActivityResult: imageUri " + mImageUri);
                galleryAddPic(mImageUriFromFile);
                String localImagePath = mImageUriFromFile.getPath();
                //去预览
                List<MediaFile> mMediaFileList = new ArrayList<>();
                MediaFile mediaFile = new MediaFile();
                mediaFile.setFolderName("temp");
                mediaFile.setMime("image/jpg");
                mediaFile.setPath(localImagePath);
                mMediaFileList.add(mediaFile);
                DataUtil.getInstance().setMediaData(mMediaFileList);
                SelectionManager.getInstance().removeAll();
                Intent intent = new Intent(this, ImageEditActivity.class);
                intent.putExtra(Parameter.INDEX, 0);
                startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
            bind.container.cpw.onActivityResult(ChoosePictureWidget.REQUEST_CODE_TAKE_PHOTO, resultCode, data);
        }
    }

    /**
     * 将拍的照片添加到相册
     *
     * @param uri 拍的照片的Uri
     */
    private void galleryAddPic(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        sendBroadcast(intent);
    }

    /**
     * 视频显示
     *
     * @param videoList
     */
    private void dealVideo(List<String> videoList) {
        if (CollectionUtil.isEmpty(videoList)) {
            return;
        }
        String videoPath = videoList.get(0);
        new Thread(() -> MediaUtils.getImageForVideo(videoPath, file -> runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            if (file == null || !file.exists()) {
                //本地生成一个默认图
                createLocalCover(videoPath);
                return;
            }
            String localVideoPicPath = file.getAbsolutePath();
            if (TextUtils.isEmpty(localVideoPicPath) || !FileUtil.isFileExist(localVideoPicPath)) {
                createLocalCover(videoPath);
                return;
            }
            Point point = BitmapUtil.getBitmapSize(localVideoPicPath);
            if (mLocalVideos == null) {
                mLocalVideos = new ArrayList<>();
            } else {
                mLocalVideos.clear();
            }
            LocalVideoModel localVideoModel = new LocalVideoModel(point.x, point.y,
                    localVideoPicPath, videoPath);
            mLocalVideos.add(localVideoModel);
            realShowVideo();
        }))).start();
    }

    /**
     * 本地生成默认封面
     */
    private void createLocalCover(String videoPath) {
        Bitmap coverBitMap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_local_video_cover);
        File tempDir = FileUtil.getExternalFileDir(mContext,"temp");
        Bitmap bmp640 = BitmapUtil.getBitmapNewSize(coverBitMap, 640, 480, false);
        String path640 = new File(tempDir, "video_cover" + "_" + "cache180.jpg")
                .getAbsolutePath();
        BitmapUtil.saveBitmap(bmp640, path640);
        String localVideoPicPath = path640;
        Point point = BitmapUtil.getBitmapSize(localVideoPicPath);
        if (mLocalVideos == null) {
            mLocalVideos = new ArrayList<>();
        } else {
            mLocalVideos.clear();
        }
        LocalVideoModel localVideoModel = new LocalVideoModel(point.x, point.y, localVideoPicPath, videoPath);
        mLocalVideos.add(localVideoModel);
        realShowVideo();
    }

    /**
     * 展示视频
     */
    private void realShowVideo() {
        if(CollectionUtil.isEmpty(mLocalVideos)){
            return;
        }
        bind.container.addContainer.setVisibility(View.GONE);
        bind.container.reVideoContainer.setVisibility(View.VISIBLE);
        LocalVideoModel localVideoModel = mLocalVideos.get(0);
        int height = localVideoModel.getHeight();
        int width = localVideoModel.getWidth();
        if (width > 0 && height > 0) {
            //根据返回的宽高信息设置图片和占位图的宽高
            int realWidth;
            int realHeight;
            int maxHeight = bind.container.ivVideoCover.getMaxHeight();
            if (height > maxHeight) {//说明高度超出最大高度了
                realHeight = maxHeight;
            } else {
                realHeight = height;
            }
            realWidth = (int) Math.ceil((float) width * (float) realHeight / (float) height);
            int maxWidth = TDevice.getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.dp42);
            //说明宽度超出能显示的最大宽度了
            if (realWidth > maxWidth) {
                realWidth = maxWidth;
                realHeight = (int) Math.ceil((float) height * (float) realWidth / (float) width);
            }
            ViewGroup.LayoutParams ivCoverLayoutParams = bind.container.ivVideoCover.getLayoutParams();
            ivCoverLayoutParams.width = realWidth;
            ivCoverLayoutParams.height = realHeight;
            bind.container.ivVideoCover.setLayoutParams(ivCoverLayoutParams);
            ViewGroup.LayoutParams layoutParams = bind.container.reVideoContainer.getLayoutParams();
            layoutParams.width = realWidth;
            layoutParams.height = realHeight;
            bind.container.reVideoContainer.setLayoutParams(layoutParams);
        }
        ImageLoader.getInstance().loadImage(localVideoModel.getThumbUrl(), bind.container.ivVideoCover);
//        compressVideo(localVideoModel.getVideoUrl(),localVideoModel);
    }
}