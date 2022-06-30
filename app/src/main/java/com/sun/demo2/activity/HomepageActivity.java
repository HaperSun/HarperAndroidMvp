package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.net.exception.ApiException;
import com.sun.common.adapter.BaseAdapter;
import com.sun.common.adapter.BaseViewHolder;
import com.sun.demo2.R;
import com.sun.demo2.activity.bd.FaceHomepageActivity;
import com.sun.demo2.databinding.ActivityHomepageBinding;
import com.sun.demo2.event.UpgradeApkDownloadSuccessEvent;
import com.sun.demo2.update.UpdateService;
import com.sun.demo2.update.model.GetUpdateInfoResponse;
import com.sun.demo2.update.view.UpdateAppDialogFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/6
 * @note:
 */
public class HomepageActivity extends BaseMvpActivity {

    private Context mContext;
    private ActivityHomepageBinding mBinding;

    public static void start(Context context) {
        Intent intent = new Intent(context, HomepageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_homepage;
    }

    @Override
    public void initView() {
        mBinding = (ActivityHomepageBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        mContext = HomepageActivity.this;
        setAdapter();
        //App更新
        initUpdateService();
    }

    private void setAdapter() {
        List<String> beans = new ArrayList<>();
        beans.add("GreenDao在登录成功后，的一个使用实例");
        beans.add("Bar Charts 单柱状图");
        beans.add("Bar Charts 双柱状图");
        beans.add("Pie Charts 饼状图");
        beans.add("RecyclerView中的EditText");
        beans.add("图片倒影处理");
        beans.add("图片取色并融入背景色效果");
        beans.add("测试在列表中的图片加载");
        beans.add("webView的封装");
        beans.add("计时器");
        beans.add("在App内浏览PDF、docx文件");
        beans.add("Lifecycle的示范");
        beans.add("图片拼接");
        beans.add("Horizontal Bar Charts 横向单柱状图");
        beans.add("带边线的饼状图 Pie Charts");
        beans.add("腾讯地图");
        beans.add("腾讯地图  地图内置定位标及定位标点击");
        beans.add("LineChart  多条目折线图");
        beans.add("baidu  人脸识别");
        beans.add("RecyclerView中点播视频");
        beans.add("仿照百度地图的上层地址列表的上拉、下拉的拖动效果");
        beans.add("仿通讯录效果");
        beans.add("WebSocket使用和腾讯视频播放器");
        beans.add("可展开的TextView实例");
        beans.add("DrawerLayout+toolBar和音频播放");
        Adapter adapter = new Adapter(R.layout.adapter_main_recycler_view, beans);
        mBinding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> doClick(position));
    }

    private void doClick(int position) {
        switch (position) {
            case 0:
                LoginActivity.start(mContext);
                break;
            case 1:
                BarChartBasicActivity.start(mContext);
                break;
            case 2:
                BarChartMultiActivity.start(mContext);
                break;
            case 3:
                PieChartsBasicActivity.start(mContext);
                break;
            case 4:
                EditTextInRecyclerViewActivity.start(mContext);
                break;
            case 5:
                InvertedImageActivity.start(mContext);
                break;
            case 6:
                PickingPictureActivity.start(mContext);
                break;
            case 7:
                RecyclerViewImageActivity.start(mContext);
                break;
            case 8:
                WebViewActivity.start(mContext);
                break;
            case 9:
                TimerActivity.start(mContext);
                break;
            case 10:
                TbsReaderActivity.start(mContext);
                break;
            case 11:
                LifeStudyActivity.start(mContext);
                break;
            case 12:
                PictureSplicingActivity.start(mContext);
                break;
            case 13:
                HorizontalBarChartActivity.start(mContext);
                break;
            case 14:
                PiePolylineChartActivity.start(mContext);
                break;
            case 15:
                SearchBasicActivity.start(mContext);
                break;
            case 16:
                TenMapActivity.start(mContext);
                break;
            case 17:
                MultiLineChartActivity.start(mContext);
                break;
            case 18:
                FaceHomepageActivity.start(mContext);
                break;
            case 19:
                ClickVideoPlayActivity.start(mContext);
                break;
            case 20:
                CustomScrollLayoutActivity.start(mContext);
                break;
            case 21:
                AddressBookActivity.start(mContext);
                break;
            case 22:
                WebSocketActivity.start(mContext);
                break;
            case 23:
                ExpandableTextActivity.start(mContext);
                break;
            case 24:
                MusicListActivity.start(mContext);
                break;
            default:
                break;
        }
    }

    private void initUpdateService() {
        UpdateService.start(mContext, UpdateService.CMD_CHECK_UPDATE);
        UpdateService.addOnForceInstallListener((updateInfo, isDownloaded) -> {
            //强制安装的话，没有下载下来就不弹窗
            if (!isDownloaded) {
                return;
            }
            UpdateAppDialogFragment.newInstance(String.valueOf(updateInfo.getVersion()), updateInfo.getInfo(),
                    isDownloaded, updateInfo.isForceUpdate()).show(getSupportFragmentManager(), "UpdateAppDialogFragment");
        });
        UpdateService.addOnCheckUpdateListener(new UpdateService.OnCheckUpdateListener() {
            @Override
            public void onNewVersionFounded(GetUpdateInfoResponse.DataBean updateInfo, boolean isDownloaded) {
                UpdateAppDialogFragment updateAppDialogFragment = UpdateAppDialogFragment
                        .newInstance(String.valueOf(updateInfo.getVersion()), updateInfo.getInfo(), isDownloaded,
                                updateInfo.isForceUpdate());
                updateAppDialogFragment.show(getSupportFragmentManager(), "UpdateAppDialogFragment");
                updateAppDialogFragment.setUpdateHintDialogListener(new UpdateAppDialogFragment.UpdateAppDialogListener() {
                    @Override
                    public void onCancelDownloadClick(View view) {
                        //doNothing
                    }

                    @Override
                    public void onDownloadError(boolean isNeedFinish) {
                        //doNothing
                    }
                });
            }

            @Override
            public void onNoNewVersionFounded(ApiException e) {

            }
        });
    }

    class Adapter extends BaseAdapter<String, BaseViewHolder> {

        public Adapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            TextView textView = helper.getView(R.id.tv_title);
            textView.setText(item);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeApkDownloadSuccessEvent(UpgradeApkDownloadSuccessEvent event) {
        GetUpdateInfoResponse.DataBean updateInfo = event.getUpdateInfo();
        UpdateAppDialogFragment
                .newInstance(String.valueOf(updateInfo.getVersion()), updateInfo.getInfo(), true,
                        updateInfo.isForceUpdate())
                .show(getSupportFragmentManager(), "UpdateHintDialogFragment");
    }
}