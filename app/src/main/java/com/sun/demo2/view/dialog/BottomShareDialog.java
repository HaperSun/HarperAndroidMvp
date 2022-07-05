package com.sun.demo2.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sun.common.toast.ToastHelper;
import com.sun.demo2.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * @author: Harper
 * @date: 2021/10/29
 * @note: 底部分享弹窗
 */
public class BottomShareDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private String mTitle;
    private String mDescription;
    private String mShareUrl;
    private String mThumbUrl;

    public BottomShareDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_share);
        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(true);
        //设置窗体位置以及动画
        Window window = getWindow();
        if (null != window) {
            window.setWindowAnimations(R.style.main_menu_animstyle);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        findViewById(R.id.tv_wechat).setOnClickListener(this);
        findViewById(R.id.tv_wechat_moments).setOnClickListener(this);
        findViewById(R.id.tv_qq).setOnClickListener(this);
        findViewById(R.id.tv_qq_zone).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;
        switch (v.getId()) {
            case R.id.tv_wechat:
                //分享到微信好友
                shareMedia = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.tv_wechat_moments:
                //分享到微信朋友圈
                shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case R.id.tv_qq:
                //分享到QQ好友
                shareMedia = SHARE_MEDIA.QQ;
                break;
            case R.id.tv_qq_zone:
                //分享到QQ空间
                shareMedia = SHARE_MEDIA.QZONE;
                break;
            default:
                break;
        }
        goToShare(shareMedia);
    }

    private void setConfig(String title, String description, String shareUrl, String thumbUrl) {
        this.mTitle = title;
        this.mDescription = description;
        this.mShareUrl = shareUrl;
        this.mThumbUrl = thumbUrl;
    }

    /**
     * 开始分享
     *
     * @param shareMedia shareMedia
     */
    private void goToShare(SHARE_MEDIA shareMedia) {
        if (shareMedia == SHARE_MEDIA.QQ) {
            ToastHelper.showToast("暂不支持分享到QQ~");
            return;
        }
        if (shareMedia == SHARE_MEDIA.QZONE) {
            ToastHelper.showToast("暂不支持分享到QQ空间~");
            return;
        }
        if (!TextUtils.isEmpty(mShareUrl)) {
            UMWeb umWeb = new UMWeb(mShareUrl);
            if (!TextUtils.isEmpty(mTitle)) {
                umWeb.setTitle(mTitle);
            }
            if (TextUtils.isEmpty(mDescription)) {
                umWeb.setDescription(" ");
            } else {
                umWeb.setDescription(mDescription);
            }
            if (!TextUtils.isEmpty(mThumbUrl)) {
                UMImage umImage = new UMImage(getContext(), mThumbUrl);
                umWeb.setThumb(umImage);
            } else {
                UMImage umImage = new UMImage(mContext, R.mipmap.ic_app_logo);
                umImage.compressFormat = Bitmap.CompressFormat.PNG;
                umWeb.setThumb(umImage);
            }
            new ShareAction((Activity) mContext)
                    .setPlatform(shareMedia)
                    .withMedia(umWeb)
                    .share();
        } else {
            ToastHelper.showToast(R.string.share_failed);
        }
    }

    public static class Builder {

        private final BottomShareDialog mDialog;

        public Builder(Context context) {
            mDialog = new BottomShareDialog(context);
        }

        public Builder setConfig(String title, String description, String shareUrl, String thumbUrl) {
            mDialog.setConfig(title, description, shareUrl, thumbUrl);
            return this;
        }

        public BottomShareDialog build() {
            return mDialog;
        }
    }

}
