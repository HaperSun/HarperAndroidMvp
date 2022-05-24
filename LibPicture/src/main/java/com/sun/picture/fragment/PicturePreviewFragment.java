package com.sun.picture.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.base.dialog.BottomDialogFragment;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.base.util.PermissionUtil;
import com.sun.common.bean.MagicInt;
import com.sun.common.toast.CustomToast;
import com.sun.common.toast.ToastHelper;
import com.sun.picture.R;
import com.sun.picture.databinding.FragmentPicturePreviewBinding;
import com.sun.picture.img.ImageLoadListener;
import com.sun.picture.img.ImgLoader;
import com.sun.picture.model.bean.ImageItem;


/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 单张图片显示Fragment
 */
public class PicturePreviewFragment extends BaseMvpFragment {

    private static final String EXTRA_IMAGE_ITEM = "EXTRA_IMAGE_ITEM";
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

    public static PicturePreviewFragment newInstance(ImageItem imageItem) {
        PicturePreviewFragment fragment = new PicturePreviewFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_IMAGE_ITEM, imageItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_picture_preview;
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
        FragmentPicturePreviewBinding binding = (FragmentPicturePreviewBinding) mViewDataBinding;
        mImageView = binding.image;
        mLoadingBar = binding.loading;
    }

    @Override
    public void initData() {
        initClick();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String imgOri = mImageItem.getImageOri();
        ImgLoader.getInstance().loadImage(imgOri, mImageView, new ImageLoadListener() {
            @Override
            public void onLoadingStarted() {
                mLoadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(Exception e) {
                mLoadingBar.setVisibility(View.GONE);
                ToastHelper.showCustomToast(R.string.loading_image_failed, CustomToast.WARNING);
            }

            @Override
            public void onLoadingComplete(Bitmap bitmap) {
                mLoadingBar.setVisibility(View.GONE);
                mOriImgBitmap = bitmap;
                mImageView.setImageBitmap(mOriImgBitmap);
            }
        });
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
                        view1 -> {
                            if (PermissionUtil.checkStorage()){
                                saveImage();
                            }else {
                                PermissionUtil.requestStorage(mActivity, state -> {
                                    if (state== MagicInt.ONE){
                                        saveImage();
                                    }
                                });
                            }
                        })).build().show(getChildFragmentManager(), "ImagePreviewFragment");
                return true;
            });
        } catch (Exception e) {
            LogHelper.e(TAG, "Exception ->" + e);
        }
    }

    /**
     * 保存图片操作
     */
    private void saveImage() {
        FileUtil.saveNetImgToAlbum(mActivity, mImageItem.getImageOri(), mOriImgBitmap);
    }
}
