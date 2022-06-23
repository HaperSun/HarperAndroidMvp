package com.sun.demo2.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.util.CollectionUtil;
import com.sun.demo2.R;
import com.sun.img.img.ImgLoader;
import com.sun.video.model.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/23
 * @note: 点播视频Adapter
 */
public class ClickVideoPlayAdapter extends RecyclerView.Adapter<ClickVideoPlayAdapter.Holder> {

    private List<VideoModel> mModels = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public void setAdapterData(List<VideoModel> models) {
        mModels = models;
    }

    public void clearData(){
        if (CollectionUtil.notEmpty(mModels)){
            mModels.clear();
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_click_video_play, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        VideoModel model = mModels.get(position);
        if (model != null) {
            String thumb = model.placeholderImage;
            if (TextUtils.isEmpty(thumb)) {
                ImgLoader.getInstance().loadImage(R.drawable.superplayer_top_shadow, holder.ivThumb);
            } else {
                ImgLoader.getInstance().loadImage(thumb, holder.ivThumb);
            }
            int duration = model.duration;
            if (duration > 0) {
                holder.tvDuration.setText(formattedTime(duration));
            } else {
                holder.tvDuration.setText("");
            }
            holder.tvTitle.setText(TextUtils.isEmpty(model.title) ? "" : model.title);
            holder.itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(model);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView tvDuration;
        TextView tvTitle;
        ImageView ivThumb;

        public Holder(final View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.superplayer_iv);
            tvTitle = itemView.findViewById(R.id.superplayer_tv);
            tvDuration = itemView.findViewById(R.id.superplayer_tv_duration);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(VideoModel model);
    }

    private static String formattedTime(long second) {
        String hs, ms, ss, formatTime;
        long h, m, s;
        h = second / 3600;
        m = (second % 3600) / 60;
        s = (second % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }
        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }
        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
        if (h > 0) {
            formatTime = hs + ":" + ms + ":" + ss;
        } else {
            formatTime = ms + ":" + ss;
        }
        return formatTime;
    }
}
