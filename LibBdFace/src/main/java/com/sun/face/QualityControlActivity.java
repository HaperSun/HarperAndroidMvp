package com.sun.face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.face.databinding.ActivityQualityControlBinding;
import com.sun.face.manager.QualityConfigManager;
import com.sun.face.model.FaceConst;
import com.sun.face.model.QualityConfig;
import com.sun.face.util.FaceSpUtil;
/**
 * @author: Harper
 * @date: 2022/5/13
 * @note: 质量控制页面
 */
public class QualityControlActivity extends BaseMvpActivity<ActivityQualityControlBinding> implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private String mSelectQuality;
    private Context mContext;
    private FaceSpUtil mFaceSpUtil;
    private int mIntentCount = 0;

    public static void startActivityResult(Context context, int requestCode,String quality) {
        Intent intent = new Intent(context, QualityControlActivity.class);
        intent.putExtra(FaceConst.INTENT_QUALITY_LEVEL, quality);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_quality_control;
    }

    @Override
    public void initView() {
        mContext = this;
        vdb.butQualityReturn.setOnClickListener(this);
        vdb.relativeLow.setOnClickListener(this);
        vdb.relativeNormal.setOnClickListener(this);
        vdb.relativeHigh.setOnClickListener(this);
        vdb.relativeCustom.setOnClickListener(this);
        vdb.radioLow.setOnCheckedChangeListener(this);
        vdb.radioNormal.setOnCheckedChangeListener(this);
        vdb.radioHigh.setOnCheckedChangeListener(this);
        vdb.radioCustom.setOnCheckedChangeListener(this);
        vdb.textLowEnter.setOnClickListener(this);
        vdb.textNormalEnter.setOnClickListener(this);
        vdb.textHighEnter.setOnClickListener(this);
        vdb.textCustomEnter.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mFaceSpUtil = new FaceSpUtil(mContext);
        Intent intent = getIntent();
        if (intent != null) {
            String qualityLevel = intent.getStringExtra(FaceConst.INTENT_QUALITY_LEVEL);
            mSelectQuality = qualityLevel;
            // 正常
            if (getResources().getString(R.string.setting_quality_normal_txt).equals(qualityLevel)) {
                vdb.radioNormal.setChecked(true);
                vdb.textNormalEnter.setVisibility(View.VISIBLE);
                // 宽松
            } else if (getResources().getString(R.string.setting_quality_low_txt).equals(qualityLevel)) {
                vdb.radioLow.setChecked(true);
                vdb.textLowEnter.setVisibility(View.VISIBLE);
                // 严格
            } else if (getResources().getString(R.string.setting_quality_high_txt).equals(qualityLevel)) {
                vdb.radioHigh.setChecked(true);
                vdb.textHighEnter.setVisibility(View.VISIBLE);
                // 自定义
            } else if (getResources().getString(R.string.setting_quality_custom_txt).equals(qualityLevel)) {
                vdb.radioCustom.setChecked(true);
                vdb.textCustomEnter.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIntentCount = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.relative_low) {
            // 点击宽松
            disableOthers(R.id.radio_low);
            handleSelectStatus(FaceConst.QUALITY_LOW);
        } else if (id == R.id.relative_normal) {
            // 点击正常
            disableOthers(R.id.radio_normal);
            handleSelectStatus(FaceConst.QUALITY_NORMAL);
        } else if (id == R.id.relative_high) {
            // 点击严格
            disableOthers(R.id.radio_high);
            handleSelectStatus(FaceConst.QUALITY_HIGH);
        } else if (id == R.id.relative_custom) {
            // 点击自定义
            disableOthers(R.id.radio_custom);
            handleSelectStatus(FaceConst.QUALITY_CUSTOM);
            startIntent(R.string.setting_quality_custom_params_txt);
        } else if (id == R.id.but_quality_return) {
            // 点击返回
            Intent intent = getIntent();
            intent.putExtra(FaceConst.INTENT_QUALITY_LEVEL, mSelectQuality);
            setResult(FaceConst.RESULT_QUALITY_CONTROL, intent);
            finish();
        } else if (id == R.id.text_low_enter) {
            // 点击宽松跳转
            startIntent(R.string.setting_quality_low_params_txt);
        } else if (id == R.id.text_normal_enter) {
            // 点击正常跳转
            startIntent(R.string.setting_quality_normal_params_txt);
        } else if (id == R.id.text_high_enter) {
            // 点击严格跳转
            startIntent(R.string.setting_quality_high_params_txt);
        } else if (id == R.id.text_custom_enter) {
            // 点击自定义跳转
            startIntent(R.string.setting_quality_custom_params_txt);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.radio_low) {
                handleSelectStatus(FaceConst.QUALITY_LOW);
            }
            if (buttonView.getId() == R.id.radio_normal) {
                handleSelectStatus(FaceConst.QUALITY_NORMAL);
            }
            if (buttonView.getId() == R.id.radio_high) {
                handleSelectStatus(FaceConst.QUALITY_HIGH);
            }
            if (buttonView.getId() == R.id.radio_custom) {
                handleSelectStatus(FaceConst.QUALITY_CUSTOM);
            }
            disableOthers(buttonView.getId());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = getIntent();
            intent.putExtra(FaceConst.INTENT_QUALITY_LEVEL, mSelectQuality);
            setResult(FaceConst.RESULT_QUALITY_CONTROL, intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void handleSelectStatus(int qualityLevel) {
        if (qualityLevel == FaceConst.QUALITY_NORMAL) {
            vdb.radioNormal.setChecked(true);
            mSelectQuality = vdb.radioNormal.getText().toString();
            vdb.textNormalEnter.setVisibility(View.VISIBLE);
            setFaceConfig(FaceConst.QUALITY_NORMAL);
            mFaceSpUtil.put(FaceConst.KEY_QUALITY_LEVEL_SAVE, FaceConst.QUALITY_NORMAL);
        } else if (qualityLevel == FaceConst.QUALITY_LOW) {
            vdb.radioLow.setChecked(true);
            mSelectQuality = vdb.radioLow.getText().toString();
            vdb.textLowEnter.setVisibility(View.VISIBLE);
            setFaceConfig(FaceConst.QUALITY_LOW);
            mFaceSpUtil.put(FaceConst.KEY_QUALITY_LEVEL_SAVE, FaceConst.QUALITY_LOW);
        } else if (qualityLevel == FaceConst.QUALITY_HIGH) {
            vdb.radioHigh.setChecked(true);
            mSelectQuality = vdb.radioHigh.getText().toString();
            vdb.textHighEnter.setVisibility(View.VISIBLE);
            setFaceConfig(FaceConst.QUALITY_HIGH);
            mFaceSpUtil.put(FaceConst.KEY_QUALITY_LEVEL_SAVE, FaceConst.QUALITY_HIGH);
        } else if (qualityLevel == FaceConst.QUALITY_CUSTOM) {
            vdb.radioCustom.setChecked(true);
            mSelectQuality = vdb.radioCustom.getText().toString();
            vdb.textCustomEnter.setVisibility(View.VISIBLE);
            setFaceConfig(FaceConst.QUALITY_CUSTOM);
            mFaceSpUtil.put(FaceConst.KEY_QUALITY_LEVEL_SAVE, FaceConst.QUALITY_CUSTOM);
        }
    }


    /**
     * 单选时，其它状态置成false
     *
     * @param viewId
     */
    private void disableOthers(int viewId) {
        if (R.id.radio_low != viewId && vdb.radioLow.isChecked()) {
            vdb.radioLow.setChecked(false);
            vdb.textLowEnter.setVisibility(View.GONE);
        }
        if (R.id.radio_normal != viewId && vdb.radioNormal.isChecked()) {
            vdb.radioNormal.setChecked(false);
            vdb.textNormalEnter.setVisibility(View.GONE);
        }
        if (R.id.radio_high != viewId && vdb.radioHigh.isChecked()) {
            vdb.radioHigh.setChecked(false);
            vdb.textHighEnter.setVisibility(View.GONE);
        }
        if (R.id.radio_custom != viewId && vdb.radioCustom.isChecked()) {
            vdb.radioCustom.setChecked(false);
            vdb.textCustomEnter.setVisibility(View.GONE);
        }
    }

    /**
     * 跳转至参数配置页面
     */
    private void startIntent(int qualityId) {
        if (mIntentCount >= 1) {
            return;
        }
        mIntentCount++;
        QualityParamsActivity.startActivityResult(mContext,FaceConst.REQUEST_QUALITY_PARAMS,getResources().getString(qualityId));
    }

    /**
     * 参数配置方法
     */
    private void setFaceConfig(int qualityLevel) {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        config.setQualityLevel(qualityLevel);
        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        QualityConfigManager manager = QualityConfigManager.getInstance();
        manager.readQualityFile(mContext.getApplicationContext(), qualityLevel);
        QualityConfig qualityConfig = manager.getConfig();
        // 设置模糊度阈值
        config.setBlurnessValue(qualityConfig.getBlur());
        // 设置最小光照阈值（范围0-255）
        config.setBrightnessValue(qualityConfig.getMinIllum());
        // 设置最大光照阈值（范围0-255）
        config.setBrightnessMaxValue(qualityConfig.getMaxIllum());
        // 设置左眼遮挡阈值
        config.setOcclusionLeftEyeValue(qualityConfig.getLeftEyeOcclusion());
        // 设置右眼遮挡阈值
        config.setOcclusionRightEyeValue(qualityConfig.getRightEyeOcclusion());
        // 设置鼻子遮挡阈值
        config.setOcclusionNoseValue(qualityConfig.getNoseOcclusion());
        // 设置嘴巴遮挡阈值
        config.setOcclusionMouthValue(qualityConfig.getMouseOcclusion());
        // 设置左脸颊遮挡阈值
        config.setOcclusionLeftContourValue(qualityConfig.getLeftContourOcclusion());
        // 设置右脸颊遮挡阈值
        config.setOcclusionRightContourValue(qualityConfig.getRightContourOcclusion());
        // 设置下巴遮挡阈值
        config.setOcclusionChinValue(qualityConfig.getChinOcclusion());
        // 设置人脸姿态角阈值
        config.setHeadPitchValue(qualityConfig.getPitch());
        config.setHeadYawValue(qualityConfig.getYaw());
        config.setHeadRollValue(qualityConfig.getRoll());
        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FaceConst.REQUEST_QUALITY_PARAMS && resultCode == FaceConst.RESULT_QUALITY_PARAMS) {
            if (data != null) {
                int qualityLevel = data.getIntExtra(FaceConst.INTENT_QUALITY_LEVEL_PARAMS, FaceConst.QUALITY_NORMAL);
                if (qualityLevel == FaceConst.QUALITY_LOW) {
                    disableOthers(R.id.radio_low);
                } else if (qualityLevel == FaceConst.QUALITY_NORMAL) {
                    disableOthers(R.id.radio_normal);
                } else if (qualityLevel == FaceConst.QUALITY_HIGH) {
                    disableOthers(R.id.radio_high);
                } else if (qualityLevel == FaceConst.QUALITY_CUSTOM) {
                    disableOthers(R.id.radio_custom);
                }
                handleSelectStatus(qualityLevel);
            }
        }
    }
}