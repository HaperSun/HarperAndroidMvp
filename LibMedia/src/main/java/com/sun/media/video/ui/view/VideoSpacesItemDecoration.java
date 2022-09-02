package com.sun.media.video.ui.view;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: Harper
 * @date: 2022/9/2
 * @note: 视频裁剪的RecyclerView等间距分割
 */
public class VideoSpacesItemDecoration extends RecyclerView.ItemDecoration{

  private final int space;
  private final int thumbnailsCount;

  public VideoSpacesItemDecoration(int space, int thumbnailsCount) {
    this.space = space;
    this.thumbnailsCount = thumbnailsCount;
  }

  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
    int position = parent.getChildAdapterPosition(view);
    if (position == 0) {
      outRect.left = space;
      outRect.right = 0;
    } else if (thumbnailsCount > 10 && position == thumbnailsCount - 1) {
      outRect.left = 0;
      outRect.right = space;
    } else {
      outRect.left = 0;
      outRect.right = 0;
    }
  }
}
