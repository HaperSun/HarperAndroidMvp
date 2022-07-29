package com.sun.media.img.ui.view;

import android.content.Context;
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

import com.sun.base.util.CollectionUtil;
import com.sun.base.widget.RoundLayout;
import com.sun.media.R;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.model.PhotoVideoModel;
import com.sun.media.img.ui.activity.ImagePreviewActivity;
import com.sun.media.video.model.SuperPlayerModel;
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

    private final RecyclerView mRecyclerView;
    private final List<PhotoVideoModel> mModels;
    private MediaSelectorListener listener;
    private final Context mContext;
    private Adapter mAdapter;

    public MediaSelectorWidget(@NonNull Context context) {
        this(context, null);
    }

    public MediaSelectorWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaSelectorWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_choose_photo_video, this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mModels = new ArrayList<>();
        mContext = context;
    }

    public void setWidgetData(List<PhotoVideoModel> photoVideoModels, MediaSelectorListener listener) {
        this.listener = listener;
        int maxCount = MediaSelector.getInstance().config.maxCount;
        List<PhotoVideoModel> models = checkPhotoVideoModels(photoVideoModels);
        if (CollectionUtil.notEmpty(mModels)) {
            mModels.clear();
        }
        if (CollectionUtil.isEmpty(models)) {
            mModels.add(new PhotoVideoModel());
        } else {
            if (models.size() < maxCount) {
                mModels.addAll(models);
                mModels.add(new PhotoVideoModel());
            } else {
                for (PhotoVideoModel model : models) {
                    if (mModels.size() < maxCount) {
                        mModels.add(model);
                    }
                }
            }
            mModels.addAll(models);
            if (models.size() < maxCount) {
                mModels.add(new PhotoVideoModel());
            }
        }
        if (mAdapter == null) {
            mAdapter = new Adapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void addMedia(List<PhotoVideoModel> photoVideoModels) {
        int maxCount = MediaSelector.getInstance().config.maxCount;
        List<PhotoVideoModel> models = checkPhotoVideoModels(photoVideoModels);
        if (CollectionUtil.isEmpty(mModels)) {
            if (CollectionUtil.notEmpty(models)) {
                if (models.size() < maxCount) {
                    mModels.addAll(models);
                    if (models.size() < maxCount) {
                        mModels.add(new PhotoVideoModel());
                    }
                } else {
                    for (PhotoVideoModel model : models) {
                        if (mModels.size() < maxCount) {
                            mModels.add(model);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (CollectionUtil.notEmpty(models)) {
                for (PhotoVideoModel model : mModels) {
                    if (model == null) {
                        mModels.remove(null);
                    } else if (TextUtils.isEmpty(model.videoUrl) && TextUtils.isEmpty(model.imgUrl)) {
                        mModels.remove(model);
                        break;
                    }
                }
                if (mModels.size() + models.size() < maxCount) {
                    mModels.addAll(models);
                    mModels.add(new PhotoVideoModel());
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        PhotoVideoModel model = models.get(i);
                        if (mModels.size() < maxCount) {
                            mModels.add(model);
                        }
                    }
                }
            }
        }
        if (mAdapter == null) {
            mAdapter = new Adapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private List<PhotoVideoModel> checkPhotoVideoModels(List<PhotoVideoModel> mModels) {
        if (CollectionUtil.notEmpty(mModels)) {
            Iterator<PhotoVideoModel> iterator = mModels.iterator();
            while (iterator.hasNext()) {
                PhotoVideoModel model = iterator.next();
                if (model == null) {
                    iterator.remove();
                } else if (TextUtils.isEmpty(model.videoUrl) && TextUtils.isEmpty(model.imgUrl)) {
                    iterator.remove();
                }
            }
        }
        return mModels;
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
            PhotoVideoModel model = mModels.get(position);
            if (TextUtils.isEmpty(model.videoUrl) && TextUtils.isEmpty(model.imgUrl)) {
                //添加按钮
                holder.rlAdd.setVisibility(VISIBLE);
                holder.rlImg.setVisibility(GONE);
                holder.rlAdd.setOnClickListener(v -> {
                    if (listener != null) {
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
                        mModels.remove(position);
                        notifyDataSetChanged();
                    });
                } else {
                    holder.ivDelete.setVisibility(GONE);
                }
                if (!TextUtils.isEmpty(model.videoUrl)) {
                    //显示视频
                    holder.ivPlay.setVisibility(VISIBLE);
                    ImageLoader.getInstance().loadVideo(model.videoUrl, holder.img);
                    holder.ivPlay.setOnClickListener(v -> {
                        if (!TextUtils.isEmpty(model.videoUrl)) {
                            if (model.videoUrl.contains("http")) {
                                VideoPlayActivity.start(mContext, new SuperPlayerModel(model.title,
                                        model.videoUrl, ""));
                            }
                        }
                    });
                } else {
                    //显示图片
                    holder.ivPlay.setVisibility(GONE);
                    ImageLoader.getInstance().loadImage(model.imgUrl, holder.img);
                    holder.img.setOnClickListener(v -> {
                        List<String> urls = new ArrayList<>();
                        for (PhotoVideoModel photoVideoModel : mModels) {
                            if (photoVideoModel != null) {
                                if (!TextUtils.isEmpty(photoVideoModel.imgUrl)) {
                                    urls.add(photoVideoModel.imgUrl);
                                }
                            }
                        }
                        ImagePreviewActivity.start(mContext, position, urls);
                    });
                }
            }
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
