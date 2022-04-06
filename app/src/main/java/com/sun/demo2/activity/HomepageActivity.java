package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.net.exception.ApiException;
import com.sun.demo2.R;
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
    private List<String> mTitles;
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
        mTitles = getTitles();
        Adapter adapter = new Adapter();
        mBinding.recyclerView.setAdapter(adapter);
        initUpdateService();
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

    private List<String> getTitles() {
        List<String> titles = new ArrayList<>();
        titles.add("GreenDao在登录成功后，的一个使用实例");
        titles.add("Bar Charts 单柱状图");
        titles.add("Bar Charts 双柱状图");
        titles.add("Pie Charts 饼状图");
        titles.add("RecyclerView中的EditText");
        titles.add("图片倒影处理");
        titles.add("图片取色并融入背景色效果");
        titles.add("测试在列表中的图片加载");
        titles.add("webView的封装");
        titles.add("计时器");
        titles.add("在App内浏览PDF、docx文件");
        titles.add("Lifecycle的示范");
        titles.add("图片拼接");
        return titles;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeApkDownloadSuccessEvent(UpgradeApkDownloadSuccessEvent event) {
        GetUpdateInfoResponse.DataBean updateInfo = event.getUpdateInfo();
        UpdateAppDialogFragment
                .newInstance(String.valueOf(updateInfo.getVersion()), updateInfo.getInfo(), true,
                        updateInfo.isForceUpdate())
                .show(getSupportFragmentManager(), "UpdateHintDialogFragment");
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main_recycler_view,
                    parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.mTitleTextView.setText(mTitles.get(position));
            holder.itemView.setOnClickListener(v -> {
                doClick(position);
            });
        }

        @Override
        public int getItemCount() {
            return mTitles.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView mTitleTextView;

            public Holder(View itemView) {
                super(itemView);
                mTitleTextView = itemView.findViewById(R.id.adapter_title_text);
            }
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
                default:
                    break;
            }
        }
    }
}