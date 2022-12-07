package com.sun.face;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.face.databinding.ActivityFaceDetectSettingBinding;
import com.sun.face.model.FaceConst;
import com.sun.face.model.FaceInitBean;
import com.sun.face.util.FaceSpUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * @author: Harper
 * @date: 2022/5/13
 * @note: 人脸识别的设置页面
 */
public class FaceDetectSettingActivity extends BaseMvpActivity<ActivityFaceDetectSettingBinding> {

    // 设置最少动作活体数量
    private static final int VALUE_MIN_ACTIVE_NUM = 1;
    private final List<LivenessTypeEnum> mLiveNessList = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, FaceDetectSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_face_detect_setting;
    }

    @Override
    public void initView() {
        vdb.butSettingReturn.setOnClickListener(v -> onBackPressed());
        vdb.actionliveBlinkCheckbox.setTag(LivenessTypeEnum.Eye);
        vdb.actionliveLeftTurnCheckbox.setTag(LivenessTypeEnum.HeadLeft);
        vdb.actionliveRightTurnCheckbox.setTag(LivenessTypeEnum.HeadRight);
        vdb.actionliveNodCheckbox.setTag(LivenessTypeEnum.HeadDown);
        vdb.actionliveLookUpCheckbox.setTag(LivenessTypeEnum.HeadUp);
        vdb.actionliveOpenMouthCheckbox.setTag(LivenessTypeEnum.Mouth);
        settingChecked();
    }

    @Override
    public void initData() {
        FaceSpUtil mSharedPreferencesUtil = new FaceSpUtil(this);
        int qualityLevel = (int) mSharedPreferencesUtil.getSharedPreference(FaceConst.KEY_QUALITY_LEVEL_SAVE, -1);
        if (qualityLevel == -1) {
            // 默认正常
            vdb.textEnterQuality.setText(getResources().getString(R.string.setting_quality_normal_txt));
        } else {
            if (qualityLevel == FaceConst.QUALITY_NORMAL) {
                vdb.textEnterQuality.setText(getResources().getString(R.string.setting_quality_normal_txt));
            } else if (qualityLevel == FaceConst.QUALITY_LOW) {
                vdb.textEnterQuality.setText(getResources().getString(R.string.setting_quality_low_txt));
            } else if (qualityLevel == FaceConst.QUALITY_HIGH) {
                vdb.textEnterQuality.setText(getResources().getString(R.string.setting_quality_high_txt));
            } else if (qualityLevel == FaceConst.QUALITY_CUSTOM) {
                vdb.textEnterQuality.setText(getResources().getString(R.string.setting_quality_custom_txt));
            }
        }
        initListener();
    }

    private void settingChecked() {
        // 语音播报开关
        vdb.announcementsSwitch.setChecked(FaceInitBean.isOpenSound);
        // 活体检测开关
        vdb.liveDetectSwitch.setChecked(FaceInitBean.isActionLive);
        // 动作活体随机开关
        vdb.actionliveSwitch.setChecked(FaceInitBean.isLivenessRandom);
        if (!vdb.liveDetectSwitch.isChecked()) {
            vdb.actionliveLayout.setVisibility(View.GONE);
            vdb.layoutActiveType.setVisibility(View.GONE);
        } else {
            vdb.actionliveLayout.setVisibility(View.VISIBLE);
            vdb.layoutActiveType.setVisibility(View.VISIBLE);
        }
        List<LivenessTypeEnum> list = FaceInitBean.livenessList;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == LivenessTypeEnum.Eye) {
                    vdb.actionliveBlinkCheckbox.setChecked(true);
                    if (!mLiveNessList.contains(LivenessTypeEnum.Eye)) {
                        mLiveNessList.add(LivenessTypeEnum.Eye);
                    }
                }
                if (list.get(i) == LivenessTypeEnum.Mouth) {
                    vdb.actionliveOpenMouthCheckbox.setChecked(true);
                    if (!mLiveNessList.contains(LivenessTypeEnum.Mouth)) {
                        mLiveNessList.add(LivenessTypeEnum.Mouth);
                    }
                }
                if (list.get(i) == LivenessTypeEnum.HeadRight) {
                    vdb.actionliveRightTurnCheckbox.setChecked(true);
                    if (!mLiveNessList.contains(LivenessTypeEnum.HeadRight)) {
                        mLiveNessList.add(LivenessTypeEnum.HeadRight);
                    }
                }
                if (list.get(i) == LivenessTypeEnum.HeadLeft) {
                    vdb.actionliveLeftTurnCheckbox.setChecked(true);
                    if (!mLiveNessList.contains(LivenessTypeEnum.HeadLeft)) {
                        mLiveNessList.add(LivenessTypeEnum.HeadLeft);
                    }
                }
                if (list.get(i) == LivenessTypeEnum.HeadUp) {
                    vdb.actionliveLookUpCheckbox.setChecked(true);
                    if (!mLiveNessList.contains(LivenessTypeEnum.HeadUp)) {
                        mLiveNessList.add(LivenessTypeEnum.HeadUp);
                    }
                }
                if (list.get(i) == LivenessTypeEnum.HeadDown) {
                    vdb.actionliveNodCheckbox.setChecked(true);
                    if (!mLiveNessList.contains(LivenessTypeEnum.HeadDown)) {
                        mLiveNessList.add(LivenessTypeEnum.HeadDown);
                    }
                }
            }
        }
    }

    private void initListener() {
        vdb.liveDetectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                vdb.actionliveLayout.setVisibility(View.GONE);
                vdb.layoutActiveType.setVisibility(View.GONE);
            } else {
                vdb.actionliveLayout.setVisibility(View.VISIBLE);
                vdb.layoutActiveType.setVisibility(View.VISIBLE);
            }
        });
        vdb.blinkLayout.setOnClickListener(v -> {
            if (!vdb.actionliveBlinkCheckbox.isChecked()) {
                vdb.actionliveBlinkCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.Eye)) {
                    mLiveNessList.add(LivenessTypeEnum.Eye);
                }
            } else {
                vdb.actionliveBlinkCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.Eye);
            }
        });
        vdb.actionliveBlinkCheckbox.setOnClickListener(v -> {
            if (vdb.actionliveBlinkCheckbox.isChecked()) {
                vdb.actionliveBlinkCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.Eye)) {
                    mLiveNessList.add(LivenessTypeEnum.Eye);
                }
            } else {
                vdb.actionliveBlinkCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.Eye);
            }
        });
        vdb.leftTurnLayout.setOnClickListener(v -> {
            if (!vdb.actionliveLeftTurnCheckbox.isChecked()) {
                vdb.actionliveLeftTurnCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadLeft)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadLeft);
                }
            } else {
                vdb.actionliveLeftTurnCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadLeft);
            }
        });
        vdb.actionliveLeftTurnCheckbox.setOnClickListener(v -> {
            if (vdb.actionliveLeftTurnCheckbox.isChecked()) {
                vdb.actionliveLeftTurnCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadLeft)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadLeft);
                }
            } else {
                vdb.actionliveLeftTurnCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadLeft);
            }
        });
        vdb.rightTurnLayout.setOnClickListener(v -> {
            if (!vdb.actionliveRightTurnCheckbox.isChecked()) {
                vdb.actionliveRightTurnCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadRight)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadRight);
                }
            } else {
                vdb.actionliveRightTurnCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadRight);
            }
        });
        vdb.actionliveRightTurnCheckbox.setOnClickListener(v -> {
            if (vdb.actionliveRightTurnCheckbox.isChecked()) {
                vdb.actionliveRightTurnCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadRight)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadRight);
                }
            } else {
                vdb.actionliveRightTurnCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadRight);
            }
        });
        vdb.nodLayout.setOnClickListener(v -> {
            if (!vdb.actionliveNodCheckbox.isChecked()) {
                vdb.actionliveNodCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadDown)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadDown);
                }
            } else {
                vdb.actionliveNodCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadDown);
            }
        });
        vdb.actionliveNodCheckbox.setOnClickListener(v -> {
            if (vdb.actionliveNodCheckbox.isChecked()) {
                vdb.actionliveNodCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadDown)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadDown);
                }
            } else {
                vdb.actionliveNodCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadDown);
            }
        });
        vdb.lookUpLayout.setOnClickListener(v -> {
            if (!vdb.actionliveLookUpCheckbox.isChecked()) {
                vdb.actionliveLookUpCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadUp)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadUp);
                }
            } else {
                vdb.actionliveLookUpCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadUp);
            }
        });
        vdb.actionliveLookUpCheckbox.setOnClickListener(v -> {
            if (vdb.actionliveLookUpCheckbox.isChecked()) {
                vdb.actionliveLookUpCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.HeadUp)) {
                    mLiveNessList.add(LivenessTypeEnum.HeadUp);
                }
            } else {
                vdb.actionliveLookUpCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.HeadUp);
            }
        });
        vdb.openMouthLayout.setOnClickListener(v -> {
            if (!vdb.actionliveOpenMouthCheckbox.isChecked()) {
                vdb.actionliveOpenMouthCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.Mouth)) {
                    mLiveNessList.add(LivenessTypeEnum.Mouth);
                }
            } else {
                vdb.actionliveOpenMouthCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.Mouth);
            }
        });
        vdb.actionliveOpenMouthCheckbox.setOnClickListener(v -> {
            if (vdb.actionliveOpenMouthCheckbox.isChecked()) {
                vdb.actionliveOpenMouthCheckbox.setChecked(true);
                if (!mLiveNessList.contains(LivenessTypeEnum.Mouth)) {
                    mLiveNessList.add(LivenessTypeEnum.Mouth);
                }
            } else {
                vdb.actionliveOpenMouthCheckbox.setChecked(false);
                mLiveNessList.remove(LivenessTypeEnum.Mouth);
            }
        });
        vdb.qualityLayout.setOnClickListener(v -> QualityControlActivity.startActivityResult(this,
                FaceConst.REQUEST_QUALITY_CONTROL, vdb.textEnterQuality.getText().toString().trim()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FaceInitBean.livenessList.clear();
        Collections.sort(this.mLiveNessList, new ComparatorValues());
        FaceInitBean.livenessList = this.mLiveNessList;
        FaceInitBean.isLivenessRandom = vdb.actionliveSwitch.isChecked();
        FaceInitBean.isOpenSound = vdb.announcementsSwitch.isChecked();
        FaceInitBean.isActionLive = vdb.liveDetectSwitch.isChecked();
        setFaceConfig();
    }

    @Override
    public void onBackPressed() {
        if (mLiveNessList.size() < VALUE_MIN_ACTIVE_NUM && vdb.liveDetectSwitch.isChecked()) {
            showToast("至少需要选择一项活体动作");
            return;
        }
        super.onBackPressed();
    }

    /**
     * 参数配置方法
     */
    private void setFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.setLivenessTypeList(FaceInitBean.livenessList);
        // 设置动作活体是否随机
        config.setLivenessRandom(FaceInitBean.isLivenessRandom);
        // 设置开启提示音
        config.setSound(FaceInitBean.isOpenSound);
        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    public static class ComparatorValues implements Comparator<LivenessTypeEnum> {

        @Override
        public int compare(LivenessTypeEnum object1, LivenessTypeEnum object2) {
            int m1 = object1.ordinal();
            int m2 = object2.ordinal();
            int result = 0;
            if (m1 > m2) {
                result = 1;
            } else if (m1 < m2) {
                result = -1;
            } else {
                result = 0;
            }
            return result;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FaceConst.REQUEST_QUALITY_CONTROL
                && resultCode == FaceConst.RESULT_QUALITY_CONTROL) {
            if (data != null) {
                vdb.textEnterQuality.setText(data.getStringExtra(FaceConst.INTENT_QUALITY_LEVEL));
            }
        }
    }
}