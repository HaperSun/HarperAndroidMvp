package com.sun.media.img.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.bean.MediaFile;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.TimeHelp;
import com.sun.media.R;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.ui.view.SquareImageView;
import com.sun.media.img.ui.view.SquareRelativeLayout;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 列表适配器
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.BaseHolder> {

    private final Context mContext;
    private final List<MediaFile> mMediaFileList;
    private final boolean albumCanTakePhoto;
    private final long mMaxVideoLength;

    public ImagePickerAdapter(Context context, List<MediaFile> mediaFiles) {
        this.mContext = context;
        this.mMediaFileList = mediaFiles;
        this.albumCanTakePhoto = MediaSelector.getInstance().config.albumCanTakePhoto;
        mMaxVideoLength = MediaSelector.getInstance().config.maxVideoLength;
    }

    @Override
    public int getItemViewType(int position) {
        return mMediaFileList.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.size(mMediaFileList);
    }

    /**
     * 获取item所对应的数据源
     *
     * @param position
     * @return
     */
    public MediaFile getMediaFile(int position) {
        if (albumCanTakePhoto) {
            if (position == 0) {
                return null;
            }
            return mMediaFileList.get(position - 1);
        }
        return mMediaFileList.get(position);
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MediaFile.BUTTON_CAMERA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_camera, null);
            return new BaseHolder(view);
        } else if (viewType == MediaFile.VIDEO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_video, null);
            return new VideoHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_image, null);
            return new ImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, @SuppressLint("RecyclerView") final int position) {
        MediaFile mediaFile = mMediaFileList.get(position);
        if (mediaFile != null) {
            int itemType = mediaFile.getItemType();
            if (itemType == MediaFile.PHOTO || itemType == MediaFile.VIDEO) {
                MediaHolder mediaHolder = (MediaHolder) holder;
                bindMedia(mediaHolder, mediaFile);
            }
            //设置点击事件监听
            if (mOnItemClickListener != null) {
                holder.mSquareRelativeLayout.setOnClickListener(view -> {
                    //点击媒体文件
                    mOnItemClickListener.onMediaClick(position, itemType);
                });
                if (holder instanceof MediaHolder) {
                    ((MediaHolder) holder).mViewCheck.setOnClickListener(view -> {
                        if (mediaFile.getDuration() > mMaxVideoLength) {
                            //如果选择的视频时长超过最大值
                            ToastHelper.showToast(R.string.choose_video_tips);
                            return;
                        }
                        //点击圆圈
                        mOnItemClickListener.onMediaCheck(view, position);
                    });
                }
            }
        }
    }

    /**
     * 绑定数据（图片、视频）
     *
     * @param mediaHolder
     * @param mediaFile
     */
    private void bindMedia(MediaHolder mediaHolder, MediaFile mediaFile) {
        String path = mediaFile.getPath();
        if (!TextUtils.isEmpty(path)) {
            //选择状态（仅是UI表现，真正数据交给SelectionManager管理）
            if (mediaFile.isSelected()) {
                mediaHolder.mTvCheck.setBackgroundResource(R.drawable.shape_oval_write_yellow_4px);
                mediaHolder.mTvCheck.setText(mediaFile.getSelectedIndex());
            } else {
                if (mediaFile.getDuration() > mMaxVideoLength) {
                    mediaHolder.mTvCheck.setBackgroundResource(R.drawable.shape_oval_gray_4px);
                } else {
                    mediaHolder.mTvCheck.setBackgroundResource(R.drawable.shape_oval_write_4px);
                }
                mediaHolder.mTvCheck.setText("");
            }
            if (mediaHolder instanceof ImageHolder) {
                //如果是gif图，显示gif标识
                ImageHolder holder = ((ImageHolder) mediaHolder);
                String suffix = path.substring(path.lastIndexOf(".") + 1);
                if (suffix.toUpperCase().equals("GIF")) {
                    holder.mImageGif.setVisibility(View.VISIBLE);
                } else {
                    holder.mImageGif.setVisibility(View.GONE);
                }
                try {
                    ImageLoader.getInstance().loadImage(path, mediaHolder.mImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mediaHolder instanceof VideoHolder) {
                //如果是视频，需要显示视频时长
                VideoHolder holder = ((VideoHolder) mediaHolder);
                String duration = TimeHelp.getVideoDuration(mediaFile.getDuration());
                holder.mVideoDuration.setText(duration);
                try {
                    ImageLoader.getInstance().loadVideo(path, mediaHolder.mImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 图片Item
     */
    class ImageHolder extends MediaHolder {

        public ImageView mImageGif;

        public ImageHolder(View itemView) {
            super(itemView);
            mImageGif = itemView.findViewById(R.id.iv_item_gif);
        }
    }

    /**
     * 视频Item
     */
    class VideoHolder extends MediaHolder {

        TextView mVideoDuration;

        VideoHolder(View itemView) {
            super(itemView);
            mVideoDuration = itemView.findViewById(R.id.tv_item_videoDuration);
        }
    }

    /**
     * 媒体Item
     */
    class MediaHolder extends BaseHolder {

        SquareImageView mImageView;
        View mViewCheck;
        TextView mTvCheck;

        MediaHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item_image);
            mViewCheck = itemView.findViewById(R.id.frame_checked);
            mTvCheck = itemView.findViewById(R.id.tv_checked);
        }
    }

    /**
     * 基础Item
     */
    class BaseHolder extends RecyclerView.ViewHolder {

        SquareRelativeLayout mSquareRelativeLayout;

        BaseHolder(View itemView) {
            super(itemView);
            mSquareRelativeLayout = itemView.findViewById(R.id.srl_item);
        }
    }

    /**
     * 接口回调，将点击事件向外抛
     */
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onMediaClick(int position, int itemType);

        void onMediaCheck(View view, int position);
    }
}
