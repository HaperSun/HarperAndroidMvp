package com.sun.media.img.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.bean.MediaFile;
import com.sun.base.toast.CustomToast;
import com.sun.base.toast.ToastHelper;
import com.sun.base.upload.UpLoadFileHelper;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.DataUtil;
import com.sun.base.widget.RoundLayout;
import com.sun.base.widget.SpacesItemDecoration;
import com.sun.media.R;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.i.ImageLoadListener;
import com.sun.media.img.ui.activity.ImagePreviewActivity;
import com.sun.media.video.model.SuperPlayerModel;
import com.sun.media.video.ui.activity.VideoEditActivity;
import com.sun.media.video.ui.activity.VideoPlayActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Harper
 * @date 2022/7/22
 * note: 选择图片或视频控件
 */
public class MediaSelectorWidget extends FrameLayout {

    private final ArrayList<MediaFile> mModels;
    private MediaSelectorListener listener;
    private final Context mContext;
    private final Adapter mAdapter;
    private UpLoadFileHelper mUpLoadFileHelper;

    public MediaSelectorWidget(@NonNull Context context) {
        this(context, null);
    }

    public MediaSelectorWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaSelectorWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_media_selector, this);
        RecyclerView recyclerView = findViewById(R.id.widget_media_selector_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        mModels = new ArrayList<>();
        mContext = context;
        mAdapter = new Adapter();
        int space = getResources().getDimensionPixelSize(R.dimen.dp1);
        recyclerView.addItemDecoration(new SpacesItemDecoration(space, space, false));
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化空间时必须调用
     *
     * @param mediaFiles 媒体文件数据
     * @param listener   选择器的监听
     */
    public void initWidgetData(ArrayList<MediaFile> mediaFiles, MediaSelectorListener listener) {
        this.listener = listener;
        int maxCount = MediaSelector.getInstance().config.maxCount;
        ArrayList<MediaFile> models = checkPhotoVideoModels(mediaFiles);
        if (CollectionUtil.notEmpty(mModels)) {
            mModels.clear();
        }
        if (CollectionUtil.isEmpty(models)) {
            mModels.add(new MediaFile(MediaFile.BUTTON_ADD));
        } else {
            if (models.size() < maxCount) {
                mModels.addAll(models);
                mModels.add(new MediaFile(MediaFile.BUTTON_ADD));
            } else {
                for (MediaFile model : models) {
                    if (mModels.size() < maxCount) {
                        mModels.add(model);
                    }
                }
            }
        }
        if (!MediaSelector.getInstance().config.needUploadFile) {
            mAdapter.notifyDataSetChanged();
        } else {
            boolean[] uploadFail = {false};
            int size = mModels.size();
            for (int i = 0; i < size; i++) {
                if (uploadFail[0]) {
                    break;
                }
                MediaFile model = mModels.get(i);
                if (!model.fromNet && model.path != null) {
                    if (model.itemType == MediaFile.PHOTO || model.itemType == MediaFile.VIDEO) {
                        if (mUpLoadFileHelper == null) {
                            mUpLoadFileHelper = new UpLoadFileHelper("", "");
                        }
                        int finalI = i;
                        mUpLoadFileHelper.setUploadResultListener(new UpLoadFileHelper.IUploadResultListener() {
                            @Override
                            public void onUploadSuccess(String localPath, String url) {
                                model.setFromNet(true);
                                model.url = url;
                                if (finalI == size - 1) {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onUploadFail(String localPath, Exception e, String text) {
                                if (!uploadFail[0]) {
                                    ToastHelper.showCustomToast("文件上传失败", CustomToast.WARNING);
                                    uploadFail[0] = true;
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 获取控件中图片或视频数据
     *
     * @return ArrayList
     */
    public ArrayList<MediaFile> getWidgetData() {
        ArrayList<MediaFile> models = new ArrayList<>();
        if (CollectionUtil.notEmpty(mModels)) {
            for (MediaFile file : mModels) {
                if (file.itemType == MediaFile.PHOTO || file.itemType == MediaFile.VIDEO) {
                    models.add(file);
                }
            }
        }
        return models;
    }

    private ArrayList<MediaFile> checkPhotoVideoModels(ArrayList<MediaFile> models) {
        if (CollectionUtil.notEmpty(models)) {
            Iterator<MediaFile> iterator = models.iterator();
            while (iterator.hasNext()) {
                MediaFile model = iterator.next();
                if (model == null) {
                    iterator.remove();
                } else {
                    if (model.fromNet && TextUtils.isEmpty(model.url)) {
                        iterator.remove();
                    } else if (TextUtils.isEmpty(model.path)) {
                        iterator.remove();
                    }
                }
            }
        }
        return models;
    }

    public void refreshWidgetData(ArrayList<MediaFile> models) {
        if (CollectionUtil.isEmpty(models)) {
            return;
        }
        if (!MediaSelector.getInstance().config.needUploadFile) {
            reallyRefreshData(models);
        } else {
            boolean[] uploadFail = {false};
            int size = models.size();
            for (int i = 0; i < size; i++) {
                if (uploadFail[0]) {
                    break;
                }
                MediaFile model = models.get(i);
                if (!model.fromNet && model.path != null) {
                    if (model.itemType == MediaFile.PHOTO || model.itemType == MediaFile.VIDEO) {
                        if (mUpLoadFileHelper == null) {
                            mUpLoadFileHelper = new UpLoadFileHelper("", "");
                        }
                        int finalI = i;
                        mUpLoadFileHelper.setUploadResultListener(new UpLoadFileHelper.IUploadResultListener() {
                            @Override
                            public void onUploadSuccess(String localPath, String url) {
                                model.setFromNet(true);
                                model.url = url;
                                if (finalI == size - 1) {
                                    reallyRefreshData(models);
                                }
                            }

                            @Override
                            public void onUploadFail(String localPath, Exception e, String text) {
                                if (!uploadFail[0]) {
                                    ToastHelper.showCustomToast("文件上传失败", CustomToast.WARNING);
                                    uploadFail[0] = true;
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void reallyRefreshData(ArrayList<MediaFile> models) {
        if (CollectionUtil.isEmpty(models)) {
            return;
        }
        int maxCount = MediaSelector.getInstance().config.maxCount;
        for (int i = 0; i < mModels.size(); i++) {
            MediaFile model = mModels.get(i);
            if (model.itemType == MediaFile.BUTTON_ADD) {
                //移除ADD按钮的数据
                mModels.remove(model);
                mAdapter.notifyItemRemoved(i);
                mAdapter.notifyItemRangeChanged(i, mModels.size() - i);
                break;
            }
        }
        //插入新数据
        int startPosition = mModels.size();
        if (mModels.size() + models.size() < maxCount) {
            mModels.addAll(models);
            mModels.add(new MediaFile(MediaFile.BUTTON_ADD));
        } else {
            for (MediaFile model : models) {
                if (mModels.size() < maxCount) {
                    mModels.add(model);
                }
            }
        }
        mAdapter.notifyItemRangeInserted(startPosition, mModels.size() - startPosition - 1);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_photo_video, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            MediaFile model = mModels.get(position);
            if (model.itemType == MediaFile.BUTTON_ADD) {
                //添加按钮
                holder.rlAdd.setVisibility(VISIBLE);
                holder.rlImg.setVisibility(GONE);
                holder.rlAdd.setOnClickListener(v -> {
                    if (listener != null) {
                        MediaSelector.getInstance().setSelectedFiles(getSelectedFiles());
                        listener.onAddMedia();
                    }
                });
            } else {
                holder.rlAdd.setVisibility(GONE);
                holder.rlImg.setVisibility(VISIBLE);
                holder.img.setVisibility(VISIBLE);
                //删除按钮
                boolean showDelete = MediaSelector.getInstance().config.showDelete;
                if (showDelete) {
                    holder.ivDelete.setVisibility(VISIBLE);
                    holder.ivDelete.setOnClickListener(v -> {
                        //点击删除
                        doClickDelete(position);
                    });
                } else {
                    holder.ivDelete.setVisibility(GONE);
                }
                if (model.itemType == MediaFile.VIDEO) {
                    //显示视频
                    showVideo(holder, model);
                } else if (model.itemType == MediaFile.PHOTO) {
                    //显示图片
                    showImg(holder, model);
                }
            }
        }

        private void showVideo(Holder holder, MediaFile model) {
            holder.ivPlay.setVisibility(VISIBLE);
            String url = model.fromNet ? model.url : model.path;
            holder.ivPlay.setOnClickListener(v -> {
                //点击视频
                doClickVideo(url, model);
            });
            holder.img.setTag(R.id.iv_picture, url);
            //为了解决图片加载错乱问题
            ImageLoader.load().loadVideo(url, holder.img, new ImageLoadListener() {
                @Override
                public void onLoadingStarted() {
                    holder.img.setImageResource(R.drawable.color_blank);
                    holder.img.setTag(R.id.img, url);
                }

                @Override
                public void onLoadingFailed(Exception e) {
                    if (TextUtils.equals((String) holder.img.getTag(R.id.img), url)) {
                        holder.img.setImageResource(R.drawable.color_blank);
                    }
                }

                @Override
                public void onLoadingComplete(Bitmap bitmap) {
                    if (TextUtils.equals((String) holder.img.getTag(R.id.img), url)) {
                        holder.img.setImageBitmap(bitmap);
                    }
                }
            });
        }

        private void showImg(Holder holder, MediaFile model) {
            holder.ivPlay.setVisibility(GONE);
            String url = model.fromNet ? model.url : model.path;
            holder.img.setOnClickListener(v -> {
                //点击图片
                doClickImg(url);
            });
            //为了解决图片加载错乱问题
            ImageLoader.load().loadImage(url, holder.img, new ImageLoadListener() {
                @Override
                public void onLoadingStarted() {
                    holder.img.setImageResource(R.drawable.color_blank);
                    holder.img.setTag(url);
                }

                @Override
                public void onLoadingFailed(Exception e) {
                    if (TextUtils.equals((String) holder.img.getTag(), url)) {
                        holder.img.setImageResource(R.drawable.color_blank);
                    }
                }

                @Override
                public void onLoadingComplete(Bitmap bitmap) {
                    if (TextUtils.equals((String) holder.img.getTag(), url)) {
                        holder.img.setImageBitmap(bitmap);
                    }
                }
            });
        }

        private void doClickVideo(String url, MediaFile model) {
            if (!TextUtils.isEmpty(url)) {
                if (model.fromNet) {
                    VideoPlayActivity.start(mContext, new SuperPlayerModel("", url, ""));
                } else {
                    ArrayList<MediaFile> mediaFiles = new ArrayList<>();
                    mediaFiles.add(model);
                    DataUtil.getInstance().setMediaData(mediaFiles);
                    VideoEditActivity.start(mContext, true);
                }
            }
        }

        private void doClickImg(String url) {
            if (!TextUtils.isEmpty(url)) {
                List<String> urls = new ArrayList<>();
                for (MediaFile file : mModels) {
                    if (file != null && file.itemType == MediaFile.PHOTO) {
                        if (file.fromNet && !TextUtils.isEmpty(file.url)) {
                            urls.add(file.url);
                        } else {
                            if (!TextUtils.isEmpty(file.path)) {
                                urls.add(file.path);
                            }
                        }
                    }
                }
                int index = 0;
                for (int i = 0; i < urls.size(); i++) {
                    String s = urls.get(i);
                    if (!TextUtils.isEmpty(s) && TextUtils.equals(s, url)) {
                        index = i;
                        break;
                    }
                }
                if (CollectionUtil.notEmpty(urls)) {
                    ImagePreviewActivity.start(mContext, index, urls);
                }
            }
        }

        private ArrayList<MediaFile> getSelectedFiles() {
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            if (CollectionUtil.notEmpty(mModels)) {
                for (MediaFile model : mModels) {
                    if (model != null) {
                        if (model.fromNet && !TextUtils.isEmpty(model.url) || !TextUtils.isEmpty(model.path)) {
                            mediaFiles.add(model);
                        }
                    }
                }
            }
            return mediaFiles;
        }

        /**
         * 点击删除按钮
         *
         * @param position 位置
         */
        private void doClickDelete(int position) {
            boolean hasAddButton = hasAddButton();
            mModels.remove(position);
            //调用notifyItemRemoved或notifyItemRangeRemoved方法时
            // 需要同时调用notifyItemRangeChanged（startPosition，allCount-startPosition），不然会出现位置异常。
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mModels.size() - position);
            if (!hasAddButton) {
                //插入一条ADD的数据
                mModels.add(new MediaFile(MediaFile.BUTTON_ADD));
                int insertedIndex = mModels.size() - 1;
                notifyItemInserted(insertedIndex);
            }
        }

        private boolean hasAddButton() {
            return mModels.get(mModels.size() - 1).itemType == MediaFile.BUTTON_ADD;
        }

        @Override
        public void onViewRecycled(@NonNull Holder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            RoundLayout rlImg;
            ImageView img;
            ImageView ivDelete;
            ImageView ivPlay;
            RoundLayout rlAdd;

            public Holder(@NonNull View itemView) {
                super(itemView);
                rlImg = itemView.findViewById(R.id.rl_img);
                img = itemView.findViewById(R.id.img);
                ivDelete = itemView.findViewById(R.id.iv_delete);
                ivPlay = itemView.findViewById(R.id.iv_play);
                rlAdd = itemView.findViewById(R.id.rl_add);
            }
        }
    }

    /**
     * 选择图片或视频控件的监听
     */
    public interface MediaSelectorListener {
        /**
         * 点击添加按钮
         */
        void onAddMedia();
    }
}
