package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.sun.base.adapter.BaseAdapter;
import com.sun.base.adapter.BaseViewHolder;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityChoosePhotoVideoBinding;
import com.sun.demo2.model.ImageDisplayBean;
import com.sun.media.img.MediaSelector;
import com.sun.media.img.manager.MediaConfig;
import com.sun.media.img.ui.view.MediaDisplayWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/28
 * @note: 图片或视频的选择和展示
 */
public class ChoosePhotoVideoActivity extends BaseMvpActivity<ActivityChoosePhotoVideoBinding> {

    private final List<ImageDisplayBean> mBeans = new ArrayList<>();
    private final String[] mUrls = new String[]{"http://d.hiphotos.baidu.com/image/h%3D200/sign=201258cbcd80653864eaa313a7dca115/ca1349540923dd54e54f7aedd609b3de9c824873.jpg",
            "http://img3.fengniao.com/forum/attachpics/537/165/21472986.jpg",
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=ea218b2c5566d01661199928a729d498/a08b87d6277f9e2fd4f215e91830e924b999f308.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3445377427,2645691367&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2644422079,4250545639&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1444023808,3753293381&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=882039601,2636712663&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4119861953,350096499&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2437456944,1135705439&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3251359643,4211266111&fm=21&gp=0.jpg",
            "http://img4.duitang.com/uploads/item/201506/11/20150611000809_yFe5Z.jpeg",
            "http://img5.imgtn.bdimg.com/it/u=1717647885,4193212272&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2024625579,507531332&fm=21&gp=0.jpg"};

    public static void start(Context context) {
        Intent intent = new Intent(context, ChoosePhotoVideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_choose_photo_video;
    }

    @Override
    public void initView() {
        ArrayList<MediaFile> models = new ArrayList<>();
        bind.msw.initWidgetData(models, () -> MediaSelector.builder(this)
                //从相册中选择
                .operationType(MediaConfig.FROM_ALBUM)
                //文件类型：图片或视频
                .mediaFileType(MediaConfig.BOTH)
                //最大选择数
                .maxCount(9)
                //最大选择视频数
                .maxVideoCount(2)
                .build()
                .show());
    }

    @Override
    public void initData() {
        mBeans.add(new ImageDisplayBean(getDataByCount(1)));
        mBeans.add(new ImageDisplayBean(getDataByCount(4)));
        mBeans.add(new ImageDisplayBean(getDataByCount(mUrls.length)));
        mBeans.add(new ImageDisplayBean(getDataByCount(9)));
        mBeans.add(new ImageDisplayBean(getDataByCount(7)));
        mBeans.add(new ImageDisplayBean(getDataByCount(2)));

        Adapter adapter = new Adapter(R.layout.adapter_image_display, mBeans);
        bind.recyclerView.setAdapter(adapter);
    }

    private List<MediaFile> getDataByCount(int count){
        List<MediaFile> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setUrl(mUrls[i]);
            mediaFile.setItemType(MediaFile.PHOTO);
            files.add(mediaFile);
        }
        return files;
    }

    static class Adapter extends BaseAdapter<ImageDisplayBean, BaseViewHolder> {

        public Adapter(int layoutResId, List<ImageDisplayBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ImageDisplayBean item) {
            MediaDisplayWidget idw = helper.getView(R.id.idw);
            idw.setWidgetData(item.list);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Parameter.REQUEST_CODE_MEDIA && resultCode == Parameter.RESULT_CODE_MEDIA) {
                ArrayList<MediaFile> models = data.getParcelableArrayListExtra(Parameter.FILE_PATH);
                bind.msw.refreshWidgetData(models);
            }
        }
    }
}