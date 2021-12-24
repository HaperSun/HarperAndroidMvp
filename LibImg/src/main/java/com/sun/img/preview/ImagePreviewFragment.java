package com.sun.img.preview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.base.dialog.BottomDialogFragment;
import com.sun.base.dialog.CommonAlertDialog;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogUtil;
import com.sun.base.util.MD5;
import com.sun.base.util.StringUtils;
import com.sun.common.toast.CustomToast;
import com.sun.common.toast.ToastHelper;
import com.sun.img.R;
import com.sun.img.bean.ImageItem;
import com.sun.img.databinding.FragmentImagePreviewBinding;
import com.sun.img.load.ImageLoadListener;
import com.sun.img.load.ImageLoader;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 单张图片显示Fragment
 */
public class ImagePreviewFragment extends BaseMvpFragment {

    private static final String EXTRA_IMAGE_ITEM = "EXTRA_IMAGE_ITEM";
    private static final int REQ_CODE_PERMISSION_WRITE_STORAGE = 180;
    /**
     * 显示原图
     */
    private PhotoView mImageView;
    private ProgressBar mLoadingBar;
    private ImageItem mImageItem;
    /**
     * 原图
     */
    private Bitmap mOriImgBitmap;
    private FragmentActivity mActivity;

    public static ImagePreviewFragment newInstance(ImageItem imageItem) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_IMAGE_ITEM, imageItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_image_preview;
    }

    @Override
    public void initBundle() {
        Bundle args = getArguments();
        if (args != null) {
            mImageItem = args.getParcelable(EXTRA_IMAGE_ITEM);
        }
        mActivity = getActivity();
    }

    @Override
    public void initView() {
        FragmentImagePreviewBinding binding = (FragmentImagePreviewBinding) mViewDataBinding;
        mImageView = binding.image;
        mLoadingBar = binding.loading;
    }

    @Override
    public void initData() {
        initClick();
    }

    private void initClick() {
        mImageView.setOnPhotoTapListener((view, x, y) -> {
            //单击退出当前页面
            FragmentActivity activity = mActivity;
            if (activity != null) {
                activity.onBackPressed();
            }
        });
        try {
            mImageView.setOnLongClickListener(view -> {
                if (mOriImgBitmap == null) {
                    return true;
                }
                //弹出保存选项
                new BottomDialogFragment.Builder().addDialogItem(new BottomDialogFragment.DialogItem(getResources().getString(R.string.save_to_album),
                        view1 -> applyPermission())).build().show(getChildFragmentManager(), "ImagePreviewFragment");
                return true;
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception ->" + e);
        }
    }

    private void applyPermission() {
        if (AndPermission.hasPermissions(getActivity(), Permission.WRITE_EXTERNAL_STORAGE)) {
            //判断已经获取了sd卡的写入权限
            saveImage();
        } else {
            //申请sd卡的写入权限
            AndPermission.with(getActivity())
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(permissions -> {
                        //申请sd卡的写入权限通过了
                        saveImage();
                    })
                    .onDenied(permissions -> {
                        //申请sd卡的写入权限被拒绝了
                        if (AndPermission.hasAlwaysDeniedPermission(mActivity, permissions)) {
                            //用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权
                            new CommonAlertDialog.Builder(mActivity)
                                    .setTitle(getResources().getString(R.string.reminder))
                                    .setMessage(getResources().getString(R.string.permission_tips_write_read))
                                    .setNegativeText(getResources().getString(R.string.cancel))
                                    .setPositiveText(getResources().getString(R.string.confirm), view ->
                                            AndPermission.with(mActivity).runtime().setting().start(REQ_CODE_PERMISSION_WRITE_STORAGE))
                                    .build().show();
                        }
                    })
                    .start();
        }
        saveImage();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String imgOri = mImageItem.getImageOri();
        ImageLoader.getInstance().loadImage(imgOri, mImageView, new ImageLoadListener() {
            @Override
            public void onLoadingStarted() {
                mLoadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(Exception e) {
                mLoadingBar.setVisibility(View.GONE);
                ToastHelper.showCustomToast(mActivity, R.string.loading_image_failed, CustomToast.WARNING);
            }

            @Override
            public void onLoadingComplete(Bitmap bitmap) {
                mLoadingBar.setVisibility(View.GONE);
                mOriImgBitmap = bitmap;
                mImageView.setImageBitmap(mOriImgBitmap);
            }
        });
    }

    /**
     * 保存图片操作
     */
    private void saveImage() {
        String imgOri = mImageItem.getImageOri();
        String ext;//图片后缀
        if (StringUtils.isWebUrlString(imgOri)) {
            ext = MimeTypeMap.getFileExtensionFromUrl(imgOri);
        } else {
            ext = FileUtil.getExtension(imgOri);
        }
        boolean isPng = "png".equalsIgnoreCase(ext);
        if (!isPng) {
            ext = "jpg";
        }
        //保存的文件名以原文件路径md5值命名/保证生成多个
        String saveFileName = MD5.getMD5Code(imgOri) + UUID.randomUUID() + "." + ext;
        File saveFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String saveFilePath = new File(saveFileDir, saveFileName).getAbsolutePath();
        //保存图片
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveFilePath);
            mOriImgBitmap.compress(isPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                    100, fos);
            ToastHelper.showCommonToast(mActivity, "图片已保存至" + saveFileDir.getAbsolutePath() + "文件夹");
            //发广播更新图库
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(saveFilePath));
            intent.setData(uri);
            mActivity.sendBroadcast(intent);
        } catch (Exception e) {
            LogUtil.e(TAG, "saveImage Exception!", e);
            ToastHelper.showCommonToast(mActivity, "图片保存失败！");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
