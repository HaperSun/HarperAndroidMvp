package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.dialog.CommonAlertDialog;
import com.sun.base.net.exception.ApiException;
import com.sun.base.adapter.BaseAdapter;
import com.sun.base.adapter.BaseViewHolder;
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
public class HomepageActivity extends BaseMvpActivity<ActivityHomepageBinding> {

    private Context mContext;

    public static void start(Context context) {
        Intent intent = new Intent(context, HomepageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean enableEventBus() {
        return true;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mTitleColor = R.color.white;
        return false;
    }

    @Override
    public int layoutId() {
        return R.layout.activity_homepage;
    }

    @Override
    public void initView() {
        baseBind.title.setTitle("首页哈哈哈哈哈哈哈哈哈哈或或或或或或哈哈哈哈哈哈哈哈哈哈或或或或或或");
        baseBind.title.setOnTitleClickListener(view -> onBackPressed());
        LinearLayout layout = baseBind.title.getTitleLeftContainer();
        if (layout != null){
            View view = LayoutInflater.from(this).inflate(R.layout.view_homepage_left_title,null);
            view.findViewById(R.id.ll_menu).setOnClickListener(v -> onBackPressed());
            layout.addView(view);
        }
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
        beans.add("DrawerLayout+toolBar");
        beans.add("图片视频的选择和展示");
        Adapter adapter = new Adapter(R.layout.adapter_main_recycler_view, beans);
        bind.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> doClick(position));
    }

    private void doClick(int position) {
        switch (position) {
            case 0:
                BarChartBasicActivity.start(mContext);
                break;
            case 1:
                BarChartMultiActivity.start(mContext);
                break;
            case 2:
                PieChartsBasicActivity.start(mContext);
                break;
            case 3:
                EditTextInRecyclerViewActivity.start(mContext);
                break;
            case 4:
                InvertedImageActivity.start(mContext);
                break;
            case 5:
                PickingPictureActivity.start(mContext);
                break;
            case 6:
                RecyclerViewImageActivity.start(mContext);
                break;
            case 7:
                WebViewActivity.start(mContext);
                break;
            case 8:
                TimerActivity.start(mContext);
                break;
            case 9:
                TbsReaderActivity.start(mContext);
                break;
            case 10:
                LifeStudyActivity.start(mContext);
                break;
            case 11:
                PictureSplicingActivity.start(mContext);
                break;
            case 12:
                HorizontalBarChartActivity.start(mContext);
                break;
            case 13:
                PiePolylineChartActivity.start(mContext);
                break;
            case 14:
                SearchBasicActivity.start(mContext);
                break;
            case 15:
                TenMapActivity.start(mContext);
                break;
            case 16:
                MultiLineChartActivity.start(mContext);
                break;
            case 17:
                FaceHomepageActivity.start(mContext);
                break;
            case 18:
                ClickVideoPlayActivity.start(mContext);
                break;
            case 19:
                CustomScrollLayoutActivity.start(mContext);
                break;
            case 20:
                AddressBookActivity.start(mContext);
                break;
            case 21:
                WebSocketActivity.start(mContext);
                break;
            case 22:
                ExpandableTextActivity.start(mContext);
                break;
            case 23:
                ImageSelectActivity.start(mContext);
                break;
            case 24:
                ChoosePhotoVideoActivity.start(mContext);
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
                    isDownloaded, updateInfo.isForceUpdate()).show(getSupportFragmentManager(), TAG);
        });
        UpdateService.addOnCheckUpdateListener(new UpdateService.OnCheckUpdateListener() {
            @Override
            public void onNewVersionFounded(GetUpdateInfoResponse.DataBean updateInfo, boolean isDownloaded) {
                UpdateAppDialogFragment updateAppDialogFragment = UpdateAppDialogFragment
                        .newInstance(String.valueOf(updateInfo.getVersion()), updateInfo.getInfo(), isDownloaded,
                                updateInfo.isForceUpdate());
                updateAppDialogFragment.show(getSupportFragmentManager(), TAG);
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

    static class Adapter extends BaseAdapter<String, BaseViewHolder> {

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
        UpdateAppDialogFragment.newInstance(String.valueOf(updateInfo.getVersion()), updateInfo.getInfo(),
                true, updateInfo.isForceUpdate()).show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onBackPressed() {
        new CommonAlertDialog.Builder(this)
                .setTitle(R.string.reminder)
                .setMessage(R.string.exit_application)
                .setNegativeText(R.string.cancel)
                .setPositiveText(R.string.confirm, view -> close())
                .build().show();
    }
}