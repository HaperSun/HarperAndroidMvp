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
import com.sun.base.util.StringUtils;
import com.sun.common.toast.ToastHelper;
import com.sun.face.databinding.ActivityQualityParamsBinding;
import com.sun.face.model.FaceConst;
import com.sun.face.util.NumberUtil;
import com.sun.face.view.AmountView;
import com.sun.face.view.SelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class QualityParamsActivity extends BaseMvpActivity implements View.OnClickListener,
        SelectDialog.OnSelectDialogClickListener {

    private static final String TAG = QualityParamsActivity.class.getSimpleName();
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
    private ActivityQualityParamsBinding bind;

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
        bind = (ActivityQualityParamsBinding) mViewDataBinding;
        mContext = this;
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(FaceConst.INTENT_QUALITY_TITLE);
            bind.textParamsTitle.setText(title);
            if (getResources().getString(R.string.setting_quality_custom_params_txt).equals(title)) {
                bind.textDefault.setVisibility(View.GONE);
            } else {
                bind.textDefault.setVisibility(View.VISIBLE);
            }
        }
        bind.btnQualityParamReturn.setOnClickListener(this);
        bind.textDefault.setOnClickListener(this);
        bind.textSave.setOnClickListener(this);
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
        bind.amountMinIllum.setAmount(mConfig.getBrightnessValue());
        bind.amountMinIllum.setMinNum(20);
        bind.amountMinIllum.setMaxNum(60);
        bind.amountMinIllum.setInterval(1);
        bind.amountMinIllum.setQuality(AmountView.QUALITY_ILLUM);
        bind.amountMinIllum.setOnAmountChangeListener((view, amount) -> {
            mMinIllum = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // maxIllum
        bind.amountMaxIllum.setAmount(mConfig.getBrightnessMaxValue());
        bind.amountMaxIllum.setMinNum(200);
        bind.amountMaxIllum.setMaxNum(240);
        bind.amountMaxIllum.setInterval(1);
        bind.amountMaxIllum.setQuality(AmountView.QUALITY_ILLUM);
        bind.amountMaxIllum.setOnAmountChangeListener((view, amount) -> {
            mMaxIllum = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // blur
        bind.amountBlur.setAmount(mConfig.getBlurnessValue());
        bind.amountBlur.setMinNum(0.1f);
        bind.amountBlur.setMaxNum(0.9f);
        bind.amountBlur.setInterval(0.05f);
        bind.amountBlur.setQuality(AmountView.QUALITY_BLUR);
        bind.amountBlur.setOnAmountChangeListener((view, amount) -> {
            mBlur = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // left_eye
        bind.amountLeftEye.setAmount(mConfig.getOcclusionLeftEyeValue());
        bind.amountLeftEye.setMinNum(0.3f);
        bind.amountLeftEye.setMaxNum(1.0f);
        bind.amountLeftEye.setInterval(0.05f);
        bind.amountLeftEye.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountLeftEye.setOnAmountChangeListener((view, amount) -> {
            mLeftEye = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // right_eye
        bind.amountRightEye.setAmount(mConfig.getOcclusionRightEyeValue());
        bind.amountRightEye.setMinNum(0.3f);
        bind.amountRightEye.setMaxNum(1.0f);
        bind.amountRightEye.setInterval(0.05f);
        bind.amountRightEye.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountRightEye.setOnAmountChangeListener((view, amount) -> {
            mRightEye = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // nose
        bind.amountNose.setAmount(mConfig.getOcclusionNoseValue());
        bind.amountNose.setMinNum(0.3f);
        bind.amountNose.setMaxNum(1.0f);
        bind.amountNose.setInterval(0.05f);
        bind.amountNose.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountNose.setOnAmountChangeListener((view, amount) -> {
            mNose = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // mouth
        bind.amountMouth.setAmount(mConfig.getOcclusionMouthValue());
        bind.amountMouth.setMinNum(0.3f);
        bind.amountMouth.setMaxNum(1.0f);
        bind.amountMouth.setInterval(0.05f);
        bind.amountMouth.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountMouth.setOnAmountChangeListener((view, amount) -> {
            mMouth = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // left_cheek
        bind.amountLeftCheek.setAmount(mConfig.getOcclusionLeftContourValue());
        bind.amountLeftCheek.setMinNum(0.3f);
        bind.amountLeftCheek.setMaxNum(1.0f);
        bind.amountLeftCheek.setInterval(0.05f);
        bind.amountLeftCheek.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountLeftCheek.setOnAmountChangeListener((view, amount) -> {
            mLeftCheek = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // right_cheek
        bind.amountRightCheek.setAmount(mConfig.getOcclusionRightContourValue());
        bind.amountRightCheek.setMinNum(0.3f);
        bind.amountRightCheek.setMaxNum(1.0f);
        bind.amountRightCheek.setInterval(0.05f);
        bind.amountRightCheek.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountRightCheek.setOnAmountChangeListener((view, amount) -> {
            mRightCheek = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // chin
        bind.amountChin.setAmount(mConfig.getOcclusionChinValue());
        bind.amountChin.setMinNum(0.3f);
        bind.amountChin.setMaxNum(1.0f);
        bind.amountChin.setInterval(0.05f);
        bind.amountChin.setQuality(AmountView.QUALITY_OCCLU);
        bind.amountChin.setOnAmountChangeListener((view, amount) -> {
            mChin = StringUtils.parseFloat(amount);
            modifyViewColor();
        });
        // pitch
        bind.amountPitch.setAmount(mConfig.getHeadPitchValue());
        bind.amountPitch.setMinNum(10);
        bind.amountPitch.setMaxNum(50);
        bind.amountPitch.setInterval(1);
        bind.amountPitch.setQuality(AmountView.QUALITY_HEADPOSE);
        bind.amountPitch.setOnAmountChangeListener((view, amount) -> {
            mPitch = StringUtils.parseInt(amount);
            modifyViewColor();
        });
        // yaw
        bind.amountYaw.setAmount(mConfig.getHeadYawValue());
        bind.amountYaw.setMinNum(10);
        bind.amountYaw.setMaxNum(50);
        bind.amountYaw.setInterval(1);
        bind.amountYaw.setQuality(AmountView.QUALITY_HEADPOSE);
        bind.amountYaw.setOnAmountChangeListener((view, amount) -> {
            mYaw = StringUtils.parseInt(amount);
            modifyViewColor();
        });
        // roll
        bind.amountRoll.setAmount(mConfig.getHeadRollValue());
        bind.amountRoll.setMinNum(10);
        bind.amountRoll.setMaxNum(50);
        bind.amountRoll.setInterval(1);
        bind.amountRoll.setQuality(AmountView.QUALITY_HEADPOSE);
        bind.amountRoll.setOnAmountChangeListener((view, amount) -> {
            mRoll = StringUtils.parseInt(amount);
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
            if ("自定义".equals(bind.textParamsTitle.getText().toString())) {
                showMessageDialog(R.string.dialog_is_save_modify, R.string.dialog_tips3, R.string.dialog_button_exit,
                        R.string.dialog_button_save, FaceConst.DIALOG_SAVE_CUSTOM_MODIFY);
            } else {
                showMessageDialog(R.string.dialog_is_save_custom,
                        R.string.dialog_tips1, R.string.dialog_button_exit,
                        R.string.dialog_button_save_custom, FaceConst.DIALOG_SAVE_RETURN_BUTTON);
            }
        } else if (vId == R.id.text_save) {
            // 如果参数未改动
            if (judgeIsModified()) {
                showMessageDialog(R.string.dialog_is_save_custom,
                        R.string.dialog_tips1, R.string.cancel,
                        R.string.dialog_button_save_custom, FaceConst.DIALOG_SAVE_SAVE_BUTTON);
            }
        } else if (vId == R.id.text_default) {
            // 点击『恢复默认』
            // 如果参数未改动
            if (judgeIsModified()) {
                // 恢复默认配置
                String title = bind.textParamsTitle.getText().toString();
                int id = 0;
                if (title.contains("宽松")) {
                    id = R.string.dialog_is_low_default;
                } else if (title.contains("正常")) {
                    id = R.string.dialog_is_normal_default;
                } else if (title.contains("严格")) {
                    id = R.string.dialog_is_high_default;
                }
                showMessageDialog(id, R.string.dialog_tips2, R.string.cancel,
                        R.string.dialog_button_default, FaceConst.DIALOG_RESET_DEFAULT);
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
            bind.textSave.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            bind.textDefault.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
        } else {
            bind.textSave.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            bind.textDefault.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        }
    }

    /**
     * 恢复默认配置
     */
    private void resetDefaultParams() {
        // minIllum
        bind.amountMinIllum.setAmount(mConfig.getBrightnessValue());
        // maxIllum
        bind.amountMaxIllum.setAmount(mConfig.getBrightnessMaxValue());
        // blur
        bind.amountBlur.setAmount(mConfig.getBlurnessValue());
        // left_eye
        bind.amountLeftEye.setAmount(mConfig.getOcclusionLeftEyeValue());
        // right_eye
        bind.amountRightEye.setAmount(mConfig.getOcclusionRightEyeValue());
        // nose
        bind.amountNose.setAmount(mConfig.getOcclusionNoseValue());
        // mouth
        bind.amountMouth.setAmount(mConfig.getOcclusionMouthValue());
        // left_cheek
        bind.amountLeftCheek.setAmount(mConfig.getOcclusionLeftContourValue());
        // right_cheek
        bind.amountRightCheek.setAmount(mConfig.getOcclusionRightContourValue());
        // chin
        bind.amountChin.setAmount(mConfig.getOcclusionChinValue());
        // pitch
        bind.amountPitch.setAmount(mConfig.getHeadPitchValue());
        // yaw
        bind.amountYaw.setAmount(mConfig.getHeadYawValue());
        // roll
        bind.amountRoll.setAmount(mConfig.getHeadRollValue());
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
                bind.scrollView.scrollTo(0, 0);
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
        String title = bind.textParamsTitle.getText().toString();
        if (title.contains("宽松")) {
            ToastHelper.showCommonToast("已恢复为宽松默认参数");
        } else if (title.contains("正常")) {
            ToastHelper.showCommonToast("已恢复为正常默认参数");
        } else if (title.contains("严格")) {
            ToastHelper.showCommonToast("已恢复为严格默认参数");
        }
    }
}