package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.ViewGroup;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.dialog.BottomDialogFragment;
import com.sun.base.util.LogHelper;
import com.sun.base.util.PermissionUtil;
import com.sun.base.status.StatusBarUtil;
import com.sun.common.bean.MagicInt;
import com.sun.common.toast.ToastHelper;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityPictureSplicingBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: Harper
 * @date: 2022/4/2
 * @note: 图片拼接
 */
public class PictureSplicingActivity extends BaseMvpActivity {

    private ActivityPictureSplicingBinding mBind;
    private ViewGroup mRelativeLayout;

    public static void start(Context context) {
        Intent intent = new Intent(context, PictureSplicingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_picture_splicing;
    }

    @Override
    public void initView() {
        StatusBarUtil.setImmersiveStatusBar(getWindow(),true);
        mBind = (ActivityPictureSplicingBinding) mViewDataBinding;
        mRelativeLayout = mBind.container;
    }

    @Override
    public void initData() {
        mBind.container.setOnClickListener(v -> {
            new BottomDialogFragment.Builder().addDialogItem(new BottomDialogFragment.DialogItem(getResources().getString(R.string.save_to_album),
                    view1 -> {
                        if (PermissionUtil.checkStorage()) {
                            saveImage();
                        } else {
                            PermissionUtil.requestStorage(this, state -> {
                                if (state == MagicInt.ONE) {
                                    saveImage();
                                }
                            });
                        }
                    })).build().show(getSupportFragmentManager(), "PictureSplicingActivity");
        });
    }

    private Bitmap saveLayoutAsImg() {
        mRelativeLayout.setDrawingCacheEnabled(true);
        mRelativeLayout.buildDrawingCache();
        return mRelativeLayout.getDrawingCache();
    }

    @Override
    protected void onDestroy() {
        mRelativeLayout.destroyDrawingCache();
        super.onDestroy();
    }

    private void saveImage() {
        Bitmap bitmap = saveLayoutAsImg();
        if (bitmap != null) {
            saveImage(bitmap);
        }
    }

    /**
     * 保存图片
     */
    private void saveImage(Bitmap bitmap) {
        //保存的文件名以原文件路径md5值命名/保证生成多个
        String saveFileName = "img_head." + "jpg";
        File saveFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String saveFilePath = new File(saveFileDir, saveFileName).getAbsolutePath();
        //保存图片
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            runOnUiThread(() -> {
                //发广播更新图库
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(saveFilePath));
                intent.setData(uri);
                sendBroadcast(intent);
                ToastHelper.showCommonToast("图片已保存至" + saveFileDir.getAbsolutePath() + "文件夹");
            });
        } catch (Exception e) {
            LogHelper.e("saveImage", "saveImage Exception!", e);
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