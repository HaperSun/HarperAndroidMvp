package com.sun.media.video.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.media.R;
import com.sun.media.video.util.VideoTrimmerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/21
 * @note:
 */
public class VideoTrimmerAdapter extends RecyclerView.Adapter {
  private List<Bitmap> mBitmaps = new ArrayList<>();
  private LayoutInflater mInflater;
  private Context context;

  public VideoTrimmerAdapter(Context context) {
    this.context = context;
    this.mInflater = LayoutInflater.from(context);
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TrimmerViewHolder(mInflater.inflate(R.layout.item_video_thumb, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    ((TrimmerViewHolder) holder).thumbImageView.setImageBitmap(mBitmaps.get(position));
  }

  @Override
  public int getItemCount() {
    return mBitmaps.size();
  }

  public void addBitmaps(Bitmap bitmap) {
    mBitmaps.add(bitmap);
    notifyDataSetChanged();
  }

  private final class TrimmerViewHolder extends RecyclerView.ViewHolder {
    ImageView thumbImageView;

    TrimmerViewHolder(View itemView) {
      super(itemView);
      thumbImageView = itemView.findViewById(R.id.thumb);
      LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) thumbImageView.getLayoutParams();
      layoutParams.width = VideoTrimmerUtil.VIDEO_FRAMES_WIDTH / VideoTrimmerUtil.MAX_COUNT_RANGE;
      thumbImageView.setLayoutParams(layoutParams);
    }
  }
}
