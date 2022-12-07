package com.sun.face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.utils.FileUtils;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.StringUtil;
import com.sun.face.databinding.ActivityQualityParamsBinding;
import com.sun.face.model.FaceConst;
import com.sun.face.util.NumberUtil;
import com.sun.face.view.AmountView;
import com.sun.face.view.SelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author: Harper
 * @date: 2022/5/13
 * @note: 质量参数页面
 */
public class QualityParamsActivity extends BaseMvpActivity<ActivityQualityParamsBinding> implements View.OnClickListener,
        SelectDialog.OnSelectDialogClickListener {

    private FaceConfig mConfig;
    private float mMinIllum;
    private float mMaxIllum;
    private float mBlur;
    private float mLeftEye;
    private float mRightEye;
    private float mNose;
    private float mMouth;
    private float mLeftCheek;
    private float mRightCheek;
    private float mChin;
    private int mPitch;
    private int mYaw;
    private int mRoll;
    private SelectDialog mSelectDialog;
    private View mViewBg;
    private Context mContext;

    public static void startActivityResult(Context context, int requestCode,String quality) {
        Intent intent = new Intent(context, QualityParamsActivity.class);
        intent.putExtra(FaceConst.INTENT_QUALITY_TITLE, quality);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_quality_params;
    }

    @Override
    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(FaceConst.INTENT_QUALITY_TITLE);
            vdb.textParamsTitle.setText(title);
            if (getResources().getString(R.string.setting_quality_custom_params_txt).equals(title)) {
                vdb.textDefault.setVisibility(View.GONE);
            } else {
                vdb.textDefault.setVisibility(View.VISIBLE);
            }
        }
        vdb.btnQualityParamReturn.setOnClickListener(this);
        vdb.textDefault.setOnClickListener(this);
        vdb.textSave.setOnClickListener(this);
        mSelectDialog = new SelectDialog(mContext);
        mSelectDialog.setDialogListener(this);
        mSelectDialog.setCanceledOnTouchOutside(false);
        mSelectDialog.setCancelable(false);
        mViewBg = findViewById(R.id.view_bg);
    }

    @Override
    public void initData() {
        mConfig = FaceSDKManager.getInstance().getFaceConfig();
        // minIllum
        vdb.amountMinIllum.setAmount(mConfig.getBrightnessValue());
        vdb.amountMinIllum.setMinNum(20);
        vdb.amountMinIllum.setMaxNum(60);
        vdb.amountMinIllum.setInterval(1);
        vdb.amountMinIllum.setQuality(AmountView.QUALITY_ILLUM);
        vdb.amountMinIllum.setOnAmountChangeListener((view, amount) -> {
            mMinIllum = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // maxIllum
        vdb.amountMaxIllum.setAmount(mConfig.getBrightnessMaxValue());
        vdb.amountMaxIllum.setMinNum(200);
        vdb.amountMaxIllum.setMaxNum(240);
        vdb.amountMaxIllum.setInterval(1);
        vdb.amountMaxIllum.setQuality(AmountView.QUALITY_ILLUM);
        vdb.amountMaxIllum.setOnAmountChangeListener((view, amount) -> {
            mMaxIllum = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // blur
        vdb.amountBlur.setAmount(mConfig.getBlurnessValue());
        vdb.amountBlur.setMinNum(0.1f);
        vdb.amountBlur.setMaxNum(0.9f);
        vdb.amountBlur.setInterval(0.05f);
        vdb.amountBlur.setQuality(AmountView.QUALITY_BLUR);
        vdb.amountBlur.setOnAmountChangeListener((view, amount) -> {
            mBlur = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // left_eye
        vdb.amountLeftEye.setAmount(mConfig.getOcclusionLeftEyeValue());
        vdb.amountLeftEye.setMinNum(0.3f);
        vdb.amountLeftEye.setMaxNum(1.0f);
        vdb.amountLeftEye.setInterval(0.05f);
        vdb.amountLeftEye.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountLeftEye.setOnAmountChangeListener((view, amount) -> {
            mLeftEye = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // right_eye
        vdb.amountRightEye.setAmount(mConfig.getOcclusionRightEyeValue());
        vdb.amountRightEye.setMinNum(0.3f);
        vdb.amountRightEye.setMaxNum(1.0f);
        vdb.amountRightEye.setInterval(0.05f);
        vdb.amountRightEye.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountRightEye.setOnAmountChangeListener((view, amount) -> {
            mRightEye = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // nose
        vdb.amountNose.setAmount(mConfig.getOcclusionNoseValue());
        vdb.amountNose.setMinNum(0.3f);
        vdb.amountNose.setMaxNum(1.0f);
        vdb.amountNose.setInterval(0.05f);
        vdb.amountNose.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountNose.setOnAmountChangeListener((view, amount) -> {
            mNose = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // mouth
        vdb.amountMouth.setAmount(mConfig.getOcclusionMouthValue());
        vdb.amountMouth.setMinNum(0.3f);
        vdb.amountMouth.setMaxNum(1.0f);
        vdb.amountMouth.setInterval(0.05f);
        vdb.amountMouth.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountMouth.setOnAmountChangeListener((view, amount) -> {
            mMouth = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // left_cheek
        vdb.amountLeftCheek.setAmount(mConfig.getOcclusionLeftContourValue());
        vdb.amountLeftCheek.setMinNum(0.3f);
        vdb.amountLeftCheek.setMaxNum(1.0f);
        vdb.amountLeftCheek.setInterval(0.05f);
        vdb.amountLeftCheek.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountLeftCheek.setOnAmountChangeListener((view, amount) -> {
            mLeftCheek = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // right_cheek
        vdb.amountRightCheek.setAmount(mConfig.getOcclusionRightContourValue());
        vdb.amountRightCheek.setMinNum(0.3f);
        vdb.amountRightCheek.setMaxNum(1.0f);
        vdb.amountRightCheek.setInterval(0.05f);
        vdb.amountRightCheek.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountRightCheek.setOnAmountChangeListener((view, amount) -> {
            mRightCheek = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // chin
        vdb.amountChin.setAmount(mConfig.getOcclusionChinValue());
        vdb.amountChin.setMinNum(0.3f);
        vdb.amountChin.setMaxNum(1.0f);
        vdb.amountChin.setInterval(0.05f);
        vdb.amountChin.setQuality(AmountView.QUALITY_OCCLU);
        vdb.amountChin.setOnAmountChangeListener((view, amount) -> {
            mChin = StringUtil.parseFloat(amount);
            modifyViewColor();
        });
        // pitch
        vdb.amountPitch.setAmount(mConfig.getHeadPitchValue());
        vdb.amountPitch.setMinNum(10);
        vdb.amountPitch.setMaxNum(50);
        vdb.amountPitch.setInterval(1);
        vdb.amountPitch.setQuality(AmountView.QUALITY_HEADPOSE);
        vdb.amountPitch.setOnAmountChangeListener((view, amount) -> {
            mPitch = StringUtil.parseInt(amount);
            modifyViewColor();
        });
        // yaw
        vdb.amountYaw.setAmount(mConfig.getHeadYawValue());
        vdb.amountYaw.setMinNum(10);
        vdb.amountYaw.setMaxNum(50);
        vdb.amountYaw.setInterval(1);
        vdb.amountYaw.setQuality(AmountView.QUALITY_HEADPOSE);
        vdb.amountYaw.setOnAmountChangeListener((view, amount) -> {
            mYaw = StringUtil.parseInt(amount);
            modifyViewColor();
        });
        // roll
        vdb.amountRoll.setAmount(mConfig.getHeadRollValue());
        vdb.amountRoll.setMinNum(10);
        vdb.amountRoll.setMaxNum(50);
        vdb.amountRoll.setInterval(1);
        vdb.amountRoll.setQuality(AmountView.QUALITY_HEADPOSE);
        vdb.amountRoll.setOnAmountChangeListener((view, amount) -> {
            mRoll = StringUtil.parseInt(amount);
            modifyViewColor();
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_quality_param_return) {
            // 如果参数未改动
            if (!judgeIsModified()) {
                finish();
                return;
            }
            if ("自定义".equals(vdb.textParamsTitle.getText().toString())) {
                showMessageDialog(R.string.dialog_is_save_modify, R.string.dialog_tips3, R.string.dialog_button_exit,
                        R.string.dialog_button_save, FaceConst.DIALOG_SAVE_CUSTOM_MODIFY);
            } else {
                showMessageDialog(R.string.dialog_is_save_custom, R.string.dialog_tips1, R.string.dialog_button_exit,
                        R.string.dialog_button_save_custom, FaceConst.DIALOG_SAVE_RETURN_BUTTON);
            }
        } else if (vId == R.id.text_save) {
            // 如果参数未改动
            if (judgeIsModified()) {
                showMessageDialog(R.string.dialog_is_save_custom, R.string.dialog_tips1, R.string.cancel,
                        R.string.dialog_button_save_custom, FaceConst.DIALOG_SAVE_SAVE_BUTTON);
            }
        } else if (vId == R.id.text_default) {
            // 点击『恢复默认』
            // 如果参数未改动
            if (judgeIsModified()) {
                // 恢复默认配置
                String title = vdb.textParamsTitle.getText().toString();
                int id = 0;
                if (title.contains("宽松")) {
                    id = R.string.dialog_is_low_default;
                } else if (title.contains("正常")) {
                    id = R.string.dialog_is_normal_default;
                } else if (title.contains("严格")) {
                    id = R.string.dialog_is_high_default;
                }
                showMessageDialog(id, R.string.dialog_tips2, R.string.cancel, R.string.dialog_button_default, FaceConst.DIALOG_RESET_DEFAULT);
            }
        }
    }

    /**
     * 判断是否修改参数值
     *
     * @return
     */
    private boolean judgeIsModified() {
        return mMinIllum != mConfig.getBrightnessValue() || mMaxIllum != mConfig.getBrightnessMaxValue()
                || mBlur != mConfig.getBlurnessValue() || mLeftEye != mConfig.getOcclusionLeftEyeValue()
                || mRightEye != mConfig.getOcclusionRightEyeValue() || mNose != mConfig.getOcclusionNoseValue()
                || mMouth != mConfig.getOcclusionMouthValue() || mLeftCheek != mConfig.getOcclusionLeftContourValue()
                || mRightCheek != mConfig.getOcclusionRightContourValue() || mChin != mConfig.getOcclusionChinValue()
                || mPitch != mConfig.getHeadPitchValue() || mYaw != mConfig.getHeadYawValue()
                || mRoll != mConfig.getHeadRollValue();
    }

    /**
     * 创建自定义配置的json数据
     */
    private void createJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("minIllum", mMinIllum);
            jsonObject.put("maxIllum", mMaxIllum);
            jsonObject.put("leftEyeOcclusion", NumberUtil.floatToDouble(mLeftEye));
            jsonObject.put("rightEyeOcclusion", NumberUtil.floatToDouble(mRightEye));
            jsonObject.put("noseOcclusion", NumberUtil.floatToDouble(mNose));
            jsonObject.put("mouseOcclusion", NumberUtil.floatToDouble(mMouth));
            jsonObject.put("leftContourOcclusion", NumberUtil.floatToDouble(mLeftCheek));
            jsonObject.put("rightContourOcclusion", NumberUtil.floatToDouble(mRightCheek));
            jsonObject.put("chinOcclusion", NumberUtil.floatToDouble(mChin));
            jsonObject.put("pitch", mPitch);
            jsonObject.put("yaw", mYaw);
            jsonObject.put("roll", mRoll);
            jsonObject.put("blur", NumberUtil.floatToDouble(mBlur));
            // 保存文件
            FileUtils.writeToFile(new File(mContext.getFilesDir() + "/" + "custom_quality.txt"),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改控件颜色
     */
    private void modifyViewColor() {
        if (!judgeIsModified()) {
            vdb.textSave.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            vdb.textDefault.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
        } else {
            vdb.textSave.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            vdb.textDefault.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        }
    }

    /**
     * 恢复默认配置
     */
    private void resetDefaultParams() {
        // minIllum
        vdb.amountMinIllum.setAmount(mConfig.getBrightnessValue());
        // maxIllum
        vdb.amountMaxIllum.setAmount(mConfig.getBrightnessMaxValue());
        // blur
        vdb.amountBlur.setAmount(mConfig.getBlurnessValue());
        // left_eye
        vdb.amountLeftEye.setAmount(mConfig.getOcclusionLeftEyeValue());
        // right_eye
        vdb.amountRightEye.setAmount(mConfig.getOcclusionRightEyeValue());
        // nose
        vdb.amountNose.setAmount(mConfig.getOcclusionNoseValue());
        // mouth
        vdb.amountMouth.setAmount(mConfig.getOcclusionMouthValue());
        // left_cheek
        vdb.amountLeftCheek.setAmount(mConfig.getOcclusionLeftContourValue());
        // right_cheek
        vdb.amountRightCheek.setAmount(mConfig.getOcclusionRightContourValue());
        // chin
        vdb.amountChin.setAmount(mConfig.getOcclusionChinValue());
        // pitch
        vdb.amountPitch.setAmount(mConfig.getHeadPitchValue());
        // yaw
        vdb.amountYaw.setAmount(mConfig.getHeadYawValue());
        // roll
        vdb.amountRoll.setAmount(mConfig.getHeadRollValue());
    }

    private void showMessageDialog(int titleId, int tipsId, int cancelId, int confirmId, int dialogType) {
        if (mViewBg != null) {
            mViewBg.setVisibility(View.VISIBLE);
        }
        mSelectDialog.show();
        mSelectDialog.setDialogTitle(titleId);
        mSelectDialog.setDialogTips(tipsId);
        mSelectDialog.setDialogBtnCancel(cancelId);
        mSelectDialog.setDialogBtnConfirm(confirmId);
        mSelectDialog.setDialogType(dialogType);
    }

    public void dismissDialog() {
        if (mSelectDialog != null) {
            mSelectDialog.dismiss();
        }
        if (mViewBg != null) {
            mViewBg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfirm(int dialogType) {
        switch (dialogType) {
            case FaceConst.DIALOG_SAVE_SAVE_BUTTON:
            case FaceConst.DIALOG_SAVE_RETURN_BUTTON:
            case FaceConst.DIALOG_SAVE_CUSTOM_MODIFY:
                createJson();
                Intent intent = getIntent();
                intent.putExtra(FaceConst.INTENT_QUALITY_LEVEL_PARAMS, FaceConst.QUALITY_CUSTOM);
                setResult(FaceConst.RESULT_QUALITY_PARAMS, intent);
                finish();
                break;
            case FaceConst.DIALOG_RESET_DEFAULT:
                dismissDialog();
                resetDefaultParams();
                vdb.scrollView.scrollTo(0, 0);
                showToast();
                break;
            default:
                break;
        }
    }

    @Override
    public void onReturn(int dialogType) {
        switch (dialogType) {
            case FaceConst.DIALOG_SAVE_SAVE_BUTTON:
            case FaceConst.DIALOG_RESET_DEFAULT:
                dismissDialog();
                break;
            case FaceConst.DIALOG_SAVE_RETURN_BUTTON:
            case FaceConst.DIALOG_SAVE_CUSTOM_MODIFY:
                finish();
                break;
            default:
                break;
        }
    }

    public void showToast() {
        String title = vdb.textParamsTitle.getText().toString();
        if (title.contains("宽松")) {
            showToast("已恢复为宽松默认参数");
        } else if (title.contains("正常")) {
            showToast("已恢复为正常默认参数");
        } else if (title.contains("严格")) {
            showToast("已恢复为严格默认参数");
        }
    }
}