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
import com.sun.common.widget.RoundLayout;
import com.sun.media.R;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.model.PhotoVideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harper
 * @date 2022/7/22
 * note: 选择图片或视频控件
 */
public class ChoosePhotoVideoWidget extends FrameLayout {

    private RecyclerView mRecyclerView;
    private List<PhotoVideoModel> mModels;

    public ChoosePhotoVideoWidget(@NonNull Context context) {
        this(context, null);
    }

    public ChoosePhotoVideoWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoosePhotoVideoWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_choose_photo_video, this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mModels = new ArrayList<>();
    }

    public void setWidgetData(List<PhotoVideoModel> models) {
        this.mModels = models;
        if (CollectionUtil.isEmpty(mModels)) {
            mModels.add(new PhotoVideoModel());
        }
        Adapter adapter = new Adapter();
        mRecyclerView.setAdapter(adapter);
    }

    private PhotoVideoModel getPhotoVideoModel() {
        return new PhotoVideoModel();
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
            if (!TextUtils.isEmpty(model.videoUrl)) {
                ImageLoader.getInstance().loadVideo(model.videoUrl, holder.img);
                holder.rlImg.setVisibility(VISIBLE);
                holder.ivPlay.setVisibility(VISIBLE);
                holder.rlAdd.setVisibility(GONE);
            } else {
                holder.ivPlay.setVisibility(GONE);
                if (!TextUtils.isEmpty(model.url) || !TextUtils.isEmpty(model.path)) {
                    holder.rlImg.setVisibility(VISIBLE);
                    holder.rlAdd.setVisibility(GONE);
                    String s;
                    if (!TextUtils.isEmpty(model.url)) {
                        s = model.url;
                    } else {
                        s = model.path;
                    }
                    ImageLoader.getInstance().loadImage(s, holder.img);
                }else {
                    holder.rlImg.setVisibility(GONE);
                    holder.rlAdd.setVisibility(VISIBLE);
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
}
