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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.bean.MediaFile;
import com.sun.base.bean.TDevice;
import com.sun.base.util.CollectionUtil;
import com.sun.base.widget.SpacesItemDecoration;
import com.sun.media.R;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.i.ImageLoadListener;
import com.sun.media.img.ui.activity.ImagePreviewActivity;
import com.sun.media.video.model.SuperPlayerModel;
import com.sun.media.video.ui.activity.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Harper
 * @date 2022/8/26
 * note: 图片或视频展示
 */
public class MediaDisplayWidget extends FrameLayout {

    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final ViewGroup mViewGroup;
    private final ImageView mImageView;
    private final ImageView mIvPlay;

    public MediaDisplayWidget(@NonNull Context context) {
        this(context, null);
    }

    public MediaDisplayWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaDisplayWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_media_display, this);
        mRecyclerView = findViewById(R.id.widget_media_display_recycler_view);
        mViewGroup = findViewById(R.id.widget_media_display_fl_container);
        mImageView = findViewById(R.id.widget_media_display_image_view);
        mIvPlay = findViewById(R.id.widget_media_display_image_view_play);
    }

    public void setWidgetData(List<MediaFile> list) {
        if (CollectionUtil.notEmpty(list)) {
            if (CollectionUtil.size(list) == 1) {
                mRecyclerView.setVisibility(GONE);
                mViewGroup.setVisibility(VISIBLE);
                MediaFile mediaFile = list.get(0);
                if (mediaFile != null && !TextUtils.isEmpty(mediaFile.url)) {
                    int minSize = getResources().getDimensionPixelSize(R.dimen.dp109);
                    int realWidth = mediaFile.width;
                    int realHeight = mediaFile.height;
                    int width;
                    int height;
                    int maxHeight = mImageView.getMaxHeight();
                    //说明高度超出最大高度了
                    height = Math.min(realHeight, maxHeight);
                    if (height < minSize) {
                        height = minSize;
                    }
                    width = (int) Math.ceil((float) realWidth * (float) height / (float) realHeight);
                    int maxWidth = getMaxWidth();
                    //说明宽度超出能显示的最大宽度了
                    if (width > maxWidth) {
                        width = maxWidth;
                        height = (int) Math.ceil((float) realHeight * (float) width / (float) realWidth);
                    }
                    if (width == 0) {
                        width = getResources().getDimensionPixelOffset(R.dimen.dp150);
                    }
                    if (height == 0) {
                        height = getResources().getDimensionPixelOffset(R.dimen.dp150);
                    }
                    String url = mediaFile.url;
                    ViewGroup.LayoutParams ivLayoutParams = mImageView.getLayoutParams();
                    ivLayoutParams.width = width;
                    ivLayoutParams.height = height;
                    loadSingleFile(url, width, height, mediaFile.itemType);
                }
            } else {
                mRecyclerView.setVisibility(VISIBLE);
                mViewGroup.setVisibility(GONE);
                Adapter adapter = new Adapter(mContext, list);
                int space = getResources().getDimensionPixelSize(R.dimen.dp6);
                mRecyclerView.addItemDecoration(new SpacesItemDecoration(space, space, false));
                mRecyclerView.setAdapter(adapter);
            }
        }
    }

    private void loadSingleFile(String url, int width, int height, int mediaType) {
        if (mediaType == MediaFile.PHOTO) {
            mIvPlay.setVisibility(GONE);
            mImageView.setOnClickListener(v -> ImagePreviewActivity.start(mContext, 0, url));
            ImageLoader.getInstance().loadImage(url, width, height, mImageView, new ImageLoadListener() {
                @Override
                public void onLoadingStarted() {
                    mImageView.setImageResource(R.drawable.color_blank);
                    mImageView.setTag(url);
                }

                @Override
                public void onLoadingFailed(Exception e) {
                    if (TextUtils.equals(url, (String) mImageView.getTag())) {
                        mImageView.setImageResource(R.drawable.color_blank);
                    }
                }

                @Override
                public void onLoadingComplete(Bitmap bitmap) {
                    if (TextUtils.equals(url, (String) mImageView.getTag())) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }
            });
        } else if (mediaType == MediaFile.VIDEO) {
            mIvPlay.setVisibility(VISIBLE);
            mIvPlay.setOnClickListener(v -> VideoPlayActivity.start(mContext, new SuperPlayerModel(url)));
            ImageLoader.getInstance().loadVideo(url, mImageView, new ImageLoadListener() {
                @Override
                public void onLoadingStarted() {
                    mImageView.setImageResource(R.drawable.color_blank);
                    mImageView.setTag(url);
                }

                @Override
                public void onLoadingFailed(Exception e) {
                    if (TextUtils.equals(url, (String) mImageView.getTag())) {
                        mImageView.setImageResource(R.drawable.color_blank);
                    }
                }

                @Override
                public void onLoadingComplete(Bitmap bitmap) {
                    if (TextUtils.equals(url, (String) mImageView.getTag())) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }
            });
        }
    }

    /**
     * 获取最大宽度
     */
    private int getMaxWidth() {
        return TDevice.getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.dp30);
    }

    static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private final Context mContext;
        private List<MediaFile> mList;

        public Adapter(Context context, List<MediaFile> list) {
            mContext = context;
            mList = new ArrayList<>();
            mList = list;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_display, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            MediaFile item = mList.get(position);
            int size = CollectionUtil.size(mList);
            if (size > 9 && position == 8) {
                holder.tvCount.setVisibility(VISIBLE);
                String count = size - 9 + "+";
                holder.tvCount.setText(count);
            } else {
                holder.tvCount.setVisibility(GONE);
            }
            if (item != null && !TextUtils.isEmpty(item.url)) {
                String url = item.url;
                if (item.itemType == MediaFile.PHOTO) {
                    holder.ivPlay.setVisibility(GONE);
                    holder.imageView.setOnClickListener(v -> {
                        List<String> strings = new ArrayList<>();
                        int clickIndex = 0;
                        for (int i = 0; i < size; i++) {
                            MediaFile file = mList.get(i);
                            if (file != null && !TextUtils.isEmpty(file.url) && file.itemType == MediaFile.PHOTO) {
                                strings.add(file.url);
                                if (TextUtils.equals(url, file.url)) {
                                    clickIndex = i;
                                }
                            }
                        }
                        ImagePreviewActivity.start(mContext, clickIndex, strings);
                    });
                    ImageLoader.getInstance().loadImage(url, holder.imageView, new ImageLoadListener() {
                        @Override
                        public void onLoadingStarted() {
                            holder.imageView.setImageResource(R.drawable.color_blank);
                            holder.imageView.setTag(url);
                        }

                        @Override
                        public void onLoadingFailed(Exception e) {
                            if (TextUtils.equals(url, (String) holder.imageView.getTag())) {
                                holder.imageView.setImageResource(R.drawable.color_blank);
                            }
                        }

                        @Override
                        public void onLoadingComplete(Bitmap bitmap) {
                            if (TextUtils.equals(url, (String) holder.imageView.getTag())) {
                                holder.imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                } else if (item.itemType == MediaFile.VIDEO) {
                    holder.ivPlay.setVisibility(VISIBLE);
                    holder.ivPlay.setOnClickListener(v -> VideoPlayActivity.start(mContext, new SuperPlayerModel(url)));
                    ImageLoader.getInstance().loadVideo(url, holder.imageView, new ImageLoadListener() {
                        @Override
                        public void onLoadingStarted() {
                            holder.imageView.setImageResource(R.drawable.color_blank);
                            holder.imageView.setTag(R.id.img, url);
                        }

                        @Override
                        public void onLoadingFailed(Exception e) {
                            if (TextUtils.equals((String) holder.imageView.getTag(R.id.img), url)) {
                                holder.imageView.setImageResource(R.drawable.color_blank);
                            }
                        }

                        @Override
                        public void onLoadingComplete(Bitmap bitmap) {
                            if (TextUtils.equals((String) holder.imageView.getTag(R.id.img), url)) {
                                holder.imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            } else {
                holder.imageView.setImageResource(R.drawable.color_blank);
            }
        }

        @Override
        public int getItemCount() {
            int size = CollectionUtil.size(mList);
            return Math.min(size, 9);
        }

        static class Holder extends RecyclerView.ViewHolder {

            private final ImageView imageView;
            private final TextView tvCount;
            private final ImageView ivPlay;

            public Holder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.item_media_display_iv);
                tvCount = itemView.findViewById(R.id.item_media_display_tv_count);
                ivPlay = itemView.findViewById(R.id.item_media_display_iv_play);
            }
        }
    }
}
