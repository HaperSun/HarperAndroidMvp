package com.sun.img.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.sun.base.dialog.BottomDialogFragment;
import com.sun.base.manager.SelectionManager;
import com.sun.base.select.SelectableTextHelper;
import com.sun.base.util.BitmapUtil;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.CommonUtil;
import com.sun.base.util.WeakDataHolder;
import com.sun.common.toast.ToastHelper;
import com.sun.img.ImagePicker;
import com.sun.img.R;
import com.sun.img.img.ImageLoadListener;
import com.sun.img.img.ImgLoader;
import com.sun.img.ui.activity.ImgPreviewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/7
 * @note: 选择图片公共组件（支持拍照和从相册选择）
 */
public class ChoosePictureWidget extends FrameLayout {

    public static final String KEY_PHOTO = "photo";
    public static final int REQUEST_CODE_TAKE_PHOTO = 0x1001;
    public static final int REQUEST_CODE_CHOOSE_FROM_GALLERY = 0x1002;
    //最大选择数量，默认为9
    protected int mMaxChooseCount = 9;
    //是否可以编辑，默认可以编辑
    private boolean mCanEdit = true;
    //图片数组（可以是本地路径，也可以是url路径）
    private List<ImageItem> mPictureList;

    protected GridView mGvChoosePicture;
    private PictureAdapter mPictureAdapter;

    private Context mContext;
    //当前组件是否在Fragment使用
    private Fragment mFragment;

    public ChoosePictureWidget(@NonNull Context context) {
        this(context, null);
    }

    public ChoosePictureWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoosePictureWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_choose_picture, this);
        mGvChoosePicture = findViewById(R.id.gv_choose_picture);
    }

    private class PictureAdapter extends BaseAdapter {

        private static final int TYPE_PIC = 0;
        private static final int TYPE_ADD = 1;

        @Override
        public int getCount() {
            int size = getCurrentCount();
            if (mCanEdit && size < mMaxChooseCount) {
                //多了add按钮
                return size + 1;
            } else {
                return size;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mCanEdit && getCurrentCount() < mMaxChooseCount && position == getCount() - 1) {
                return TYPE_ADD;
            } else {
                return TYPE_PIC;
            }
        }

        @Override
        public int getViewTypeCount() {
            return mCanEdit && getCurrentCount() < mMaxChooseCount ? 2 : 1;
        }

        @Override
        public ImageItem getItem(int position) {
            if (mCanEdit && getCurrentCount() < mMaxChooseCount && position == getCount() - 1) {
                return null;
            } else {
                return mPictureList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemViewType = getItemViewType(position);
            int layoutId;
            if (itemViewType == TYPE_PIC) {
                PicViewHolder holder;
                layoutId = returnItemPictureLayoutId();
                if (convertView == null || convertView.getTag(layoutId) == null) {
                    convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
                    holder = new PicViewHolder(convertView);
                    convertView.setTag(layoutId, holder);
                } else {
                    holder = (PicViewHolder) convertView.getTag(layoutId);
                }
                holder.bindData(position);
            } else {
                AddPicHolder holder;
                layoutId = returnAddPictureLayoutId();
                if (convertView == null || convertView.getTag(layoutId) == null) {
                    convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
                    holder = new AddPicHolder(convertView);
                    convertView.setTag(layoutId, holder);
                } else {
                    holder = (AddPicHolder) convertView.getTag(layoutId);
                }
                holder.bindData();
            }
            return convertView;
        }

        /**
         * 图片ViewHolder
         */
        class PicViewHolder {
            final View itemView;
            final ImageView ivPicture;
            final ImageView ivDelete;

            PicViewHolder(View itemView) {
                this.itemView = itemView;
                this.ivPicture = itemView.findViewById(R.id.iv_picture);
                this.ivDelete = itemView.findViewById(R.id.iv_delete);
            }

            void bindData(final int position) {
                //加载图片
                ImageItem imageItem = getItem(position);
                final String thumbNail = imageItem.getValidThumbNail();
                if (!TextUtils.isEmpty(thumbNail)) {//说明有缩略图，优先加载缩略图
                    ImgLoader.getInstance().loadImage(thumbNail, ivPicture, new ImageLoadListener() {
                        @Override
                        public void onLoadingStarted() {
                            ivPicture.setImageResource(R.drawable.bg_blank);
                            //为了解决图片加载错乱问题
                            ivPicture.setTag(R.id.iv_picture, thumbNail);
                        }

                        @Override
                        public void onLoadingFailed(Exception e) {
                            if (TextUtils.equals((String) ivPicture.getTag(R.id.iv_picture), thumbNail)) {
                                //为了解决图片加载错乱问题
                                ivPicture.setImageResource(R.drawable.bg_blank);
                            }
                        }

                        @Override
                        public void onLoadingComplete(Bitmap bitmap) {
                            if (TextUtils.equals((String) ivPicture.getTag(R.id.iv_picture), thumbNail)) {
                                //为了解决图片加载错乱问题
                                ivPicture.setImageBitmap(bitmap);
                            }
                            //为了预览时可以直接加载内存中的bitmap图片
                            WeakDataHolder.getInstance().saveData(thumbNail, bitmap);
                        }
                    });
                } else {//否则加载原图
                    ImgLoader.getInstance().loadImage(imageItem.getValidPath(), R.drawable.bg_blank, R.drawable.bg_blank, ivPicture);
                }
                ivDelete.setVisibility(mCanEdit ? View.VISIBLE : View.GONE);
                ivDelete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //删除图片
                        mPictureList.remove(position);
                        notifyDataSetChanged();
                        if (mOnChoosePictureListener != null) {
                            mOnChoosePictureListener.onPictureChanged(mPictureList);
                        }
                        hideTextViewCopyAndSelectAllDialog();
                    }
                });
                itemView.setOnClickListener(v -> {
                    //跳转图片查看页面
                    List<String> imageItemList = new ArrayList<>();
                    for (ImageItem imageItem1 : mPictureList) {
                        imageItemList.add(imageItem1.getValidPath());
                    }
                    ImgPreviewActivity.start(mContext, position, imageItemList);
                });
            }
        }

        /**
         * 隐藏复制全选框
         */
        public void hideTextViewCopyAndSelectAllDialog() {
            //隐藏复选框
            SelectableTextHelper.hideShowingTextSelector();
        }

        /**
         * 添加图片ViewHolder
         */
        class AddPicHolder {
            final View itemView;
            final TextView tvCountInfo;

            AddPicHolder(View itemView) {
                this.itemView = itemView;
                this.tvCountInfo = itemView.findViewById(R.id.tv_count_info);
            }

            void bindData() {
                tvCountInfo.setText(getAddPicText());
                itemView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //弹出添加图片选项
                        if (mPictureList != null && mPictureList.size() >= mMaxChooseCount) {
                            ToastHelper.showToast(mContext.getString(R.string.max_picture_count_hint_str, mMaxChooseCount));
                            return;
                        }
//                        showChoosePictureDialog();
                        if (mOnChoosePictureListener != null) {
                            mOnChoosePictureListener.onShowPublishWay();
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取添加图片的文本提示
     *
     * @return 字符串
     */
    protected String getAddPicText() {
        return mContext.getString(R.string.add_picture_count_info_str, getCurrentCount(), mMaxChooseCount);
    }

    protected int returnItemPictureLayoutId() {
        return R.layout.item_choose_picture;
    }

    protected int returnAddPictureLayoutId() {
        return R.layout.widget_add_picture;
    }

    /**
     * 处理当前操作中的状态，供子类使用
     */
    protected void handleActioningStatus() {
    }

    /**
     * 获取当前图片数量
     *
     * @return
     */
    protected int getCurrentCount() {
        final int currentCount;
        if (mPictureList != null) {
            currentCount = mPictureList.size();
        } else {
            currentCount = 0;
        }
        return currentCount;
    }

    /**
     * 添加本地图片数组到当前组件更新
     *
     * @param imgList
     */
    private void appendLocalImgList(ArrayList<String> imgList) {
        if (CollectionUtil.isEmpty(imgList)) {
            return;
        }
        if (mPictureList == null) {
            mPictureList = new ArrayList<>();
        }
        List<ImageItem> imgItemList = new ArrayList<>();
        for (String img : imgList) {
            imgItemList.add(new ImageItem(img));
        }
        mPictureList.addAll(imgItemList);
        if (mPictureAdapter == null) {
            mPictureAdapter = new PictureAdapter();
            mGvChoosePicture.setAdapter(mPictureAdapter);
        } else {
            mPictureAdapter.notifyDataSetChanged();
        }
        if (mOnChoosePictureListener != null) {
            mOnChoosePictureListener.onPictureChanged(mPictureList);
        }
    }

    /**
     * 获取当前选择的图片数组
     *
     * @return
     */
    public List<ImageItem> getPictureList() {
        return mPictureList;
    }

    /**
     * 设置是否可以编辑
     *
     * @param canEdit
     */
    public void setCanEdit(boolean canEdit) {
        if (mCanEdit == canEdit) {
            return;
        }
        mCanEdit = canEdit;
        if (mPictureAdapter != null) {
            mPictureAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置最大选择数量
     *
     * @param maxChooseCount
     */
    public void setMaxChooseCount(int maxChooseCount) {
        if (maxChooseCount <= 0) {
            maxChooseCount = 1;
        }
        mMaxChooseCount = maxChooseCount;
    }

    /**
     * 显示图片信息
     *
     * @param pictureList 图片数组（可以是本地路径，也可以是url路径）
     */
    public void showPictureInfo(List<ImageItem> pictureList) {
        this.mPictureList = pictureList;
        if (mPictureAdapter == null) {
            mPictureAdapter = new PictureAdapter();
            mGvChoosePicture.setAdapter(mPictureAdapter);
        } else {
            mPictureAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置是否在Fragment中使用，将Fragment传给当前View
     *
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    /**
     * 显示底部选择图片弹窗
     */
    public void showChoosePictureDialog() {
        final int picCount = mMaxChooseCount - getCurrentCount();
        final FragmentManager fragmentManager;
        if (mFragment != null) {
            fragmentManager = mFragment.getChildFragmentManager();
        } else {
            fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        }
        new BottomDialogFragment.Builder()
                .addDialogItem(new BottomDialogFragment.DialogItem(mContext.getString(R.string.take_picture), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleActioningStatus();
                        if (mFragment != null) {

                        } else {

                        }
                    }
                }))
                .addDialogItem(new BottomDialogFragment.DialogItem(mContext.getString(R.string.choose_from_gallery), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleActioningStatus();
                    }
                }))
                .build()
                .show(fragmentManager, "ChoosePicture_BottomDialogFragment");
    }

    /**
     * 供使用方(Activity或者Fragment)调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = false;
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//从拍照界面回来
//                ArrayList<String> imgList = data.getStringArrayListExtra(KEY_PHOTO);
                ArrayList<String> selectPaths = SelectionManager.getInstance().getSelectPaths();
                appendLocalImgList(selectPaths);
//                appendLocalImgList(imgList);
                result = true;
            } else if (requestCode == REQUEST_CODE_CHOOSE_FROM_GALLERY) {//从相册选择回来
                ArrayList<String> imgList = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                appendLocalImgList(imgList);
                result = true;
            }
        }
        return result;
    }

    public OnChoosePictureListener mOnChoosePictureListener;

    public void setOnChoosePictureListener(OnChoosePictureListener onChoosePictureListener) {
        mOnChoosePictureListener = onChoosePictureListener;
    }

    /**
     * 接口
     */
    public interface OnChoosePictureListener {

        /**
         * 图片选择改变回调
         *
         * @param picList 图片数组
         */
        void onPictureChanged(List<ImageItem> picList);

        /**
         * 方式
         *
         * @param
         */
        void onShowPublishWay();
    }

    public static final class ImageItem {

        private int width;//宽
        private int height;//高
        private String path;//路径（本地或者网络路径）
        private String thumbNail;//缩略图路径(本地或者网络路径)
        private boolean isUrlPath;//是否是网络路径

        /**
         * 如果确认path是本地路径可以调用这个
         *
         * @param path
         */
        public ImageItem(String path) {
            Point point = BitmapUtil.getBitmapSize(path);
            this.width = point.x;
            this.height = point.y;
            this.path = path;
            this.isUrlPath = false;
        }

        public ImageItem(int width, int height, String path, String thumbNail, boolean isUrlPath) {
            this.width = width;
            this.height = height;
            this.path = path;
            this.thumbNail = thumbNail;
            this.isUrlPath = isUrlPath;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getPath() {
            return path;
        }

        public String getThumbNail() {
            return thumbNail;
        }

        public boolean isUrlPath() {
            return isUrlPath;
        }

        /**
         * 获取有效路径
         *
         * @return
         */
        public String getValidPath() {
            final String url;
            if (!isUrlPath) {
                url = path;
            } else {
                url = CommonUtil.getFsUrl(path);
            }
            return url;
        }

        /**
         * 获取有效缩略图路径
         *
         * @return
         */
        public String getValidThumbNail() {
            if (TextUtils.isEmpty(thumbNail)) {
                return thumbNail;
            }
            final String url;
            if (!isUrlPath) {
                url = thumbNail;
            } else {
                url = CommonUtil.getFsUrl(thumbNail);
            }
            return url;
        }
    }

    /**
     * 清楚之前选择过的图片集合    解决七彩作业评论的时候图片和评语库相互选择时候的数据异常的bug
     */
    public void removePicList() {
        if (!CollectionUtil.isEmpty(mPictureList)) {
            mPictureList.clear();
        }
    }
}
