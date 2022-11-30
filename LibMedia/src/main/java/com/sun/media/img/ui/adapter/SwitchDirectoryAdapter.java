package com.sun.media.img.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.util.CollectionUtil;
import com.sun.media.R;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.model.MediaFolder;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note:
 */
public class SwitchDirectoryAdapter extends RecyclerView.Adapter<SwitchDirectoryAdapter.ViewHolder> {

    private final Context mContext;
    private final List<MediaFolder> mMediaFolderList;
    private int mCurrentImageFolderIndex;

    public SwitchDirectoryAdapter(Context context, List<MediaFolder> mediaFolderList, int position) {
        this.mContext = context;
        this.mMediaFolderList = mediaFolderList;
        this.mCurrentImageFolderIndex = position;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_recyclerview_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.size(mMediaFolderList);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageCover;
        private TextView mFolderName;
        private TextView mImageSize;
        private ImageView mImageFolderCheck;

        ViewHolder(View itemView) {
            super(itemView);
            mImageCover = itemView.findViewById(R.id.iv_item_imageCover);
            mFolderName = itemView.findViewById(R.id.tv_item_folderName);
            mImageSize = itemView.findViewById(R.id.tv_item_imageSize);
            mImageFolderCheck = itemView.findViewById(R.id.iv_item_check);
        }

        /**
         * 绑定数据操作
         *
         * @param position
         */
        void bindData(final int position) {
            final MediaFolder mediaFolder = mMediaFolderList.get(position);
            String folderCover = mediaFolder.getFolderCover();
            String folderName = mediaFolder.getFolderName();
            int imageSize = mediaFolder.getMediaFileList().size();

            if (!TextUtils.isEmpty(folderName)) {
                mFolderName.setText(folderName);
            }

            mImageSize.setText(String.format(mContext.getString(R.string.image_num), imageSize));

            if (mCurrentImageFolderIndex == position) {
                mImageFolderCheck.setVisibility(View.VISIBLE);
            } else {
                mImageFolderCheck.setVisibility(View.GONE);
            }
            //加载图片
            try {
                ImageLoader.load().loadImage(folderCover, mImageCover);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mImageFolderChangeListener != null) {
                itemView.setOnClickListener(view -> {
                    mCurrentImageFolderIndex = position;
                    notifyDataSetChanged();
                    mImageFolderChangeListener.onImageFolderChange(view, position);
                });
            }
        }
    }

    /**
     * 更新条目背景
     *
     * @param currentPosition
     */
    public void updateItemBg(int currentPosition) {

        notifyDataSetChanged();
    }

    /**
     * 接口回调，Item点击事件
     */
    private OnImageFolderChangeListener mImageFolderChangeListener;

    public void setOnImageFolderChangeListener(OnImageFolderChangeListener onItemClickListener) {
        this.mImageFolderChangeListener = onItemClickListener;
    }

    public interface OnImageFolderChangeListener {
        void onImageFolderChange(View view, int position);
    }
}
