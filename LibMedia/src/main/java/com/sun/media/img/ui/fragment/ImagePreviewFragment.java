package com.sun.media.img.ui.fragment;

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
import com.sun.media.R;
import com.sun.media.databinding.FragmentImagePreviewBinding;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.i.ImageLoadListener;
import com.sun.media.img.model.bean.ImageItem;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 单张图片显示Fragment
 */
public class ImagePreviewFragment extends BaseMvpFragment<FragmentImagePreviewBinding> {

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
    }

    @Override
    public void initView() {
        mImageView = bind.image;
        mLoadingBar = bind.loading;
    }

    @Override
    public void initData() {
        initClick();
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
                showFailToast(R.string.loading_image_failed);
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
        FragmentActivity activity = getActivity();
        mImageView.setOnPhotoTapListener((view, x, y) -> {
            //单击退出当前页面
            close();
        });
        try {
            mImageView.setOnLongClickListener(view -> {
                if (mOriImgBitmap == null) {
                    return true;
                }
                //弹出保存选项
                new BottomDialogFragment.Builder().addDialogItem(new BottomDialogFragment.DialogItem(getResources().getString(R.string.save_to_album),
                        view1 -> {
                            if (PermissionUtil.checkWriteStorage()) {
                                //保存图片操作
                                saveImg(activity);
                            } else {
                                PermissionUtil.requestWriteStorage(activity, state -> {
                                    if (state) {
                                        saveImg(activity);
                                    }
                                });
                            }
                        })).build().show(getChildFragmentManager(), TAG);
                return true;
            });
        } catch (Exception e) {
            LogHelper.e(TAG, "Exception ->" + e);
        }
    }

    private void saveImg(FragmentActivity activity) {
        FileUtil.saveNetImgToAlbum(activity, mImageItem.getImageOri(), mOriImgBitmap);
    }
}
