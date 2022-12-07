package com.sun.demo2.activity.bd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.listener.IInitCallback;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.DensityUtils;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityFaceHomepageBinding;
import com.sun.face.FaceDetectSettingActivity;
import com.sun.face.manager.QualityConfigManager;
import com.sun.face.model.FaceConst;
import com.sun.face.model.FaceInitBean;
import com.sun.face.model.QualityConfig;
import com.sun.face.util.FaceBitmapSaveUtils;
import com.sun.face.util.FaceSpUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * @author: Harper
 * @date: 2022/7/6
 * @note: 人脸识别入口页
 */
public class FaceHomepageActivity extends BaseMvpActivity<ActivityFaceHomepageBinding> implements View.OnClickListener {

    private Context mContext;
    private boolean mFaceInitSuccess;

    public static void start(Context context) {
        Intent intent = new Intent(context, FaceHomepageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_face_homepage;
    }

    @Override
    public void initView() {
        mContext = this;
        // 根据需求添加活体动作
        FaceInitBean.livenessList.clear();
        FaceInitBean.livenessList.add(LivenessTypeEnum.Eye);
        FaceInitBean.livenessList.add(LivenessTypeEnum.Mouth);
        FaceInitBean.livenessList.add(LivenessTypeEnum.HeadRight);
        vdb.butStartSetting.setOnClickListener(this);
        vdb.butStartDetect.setOnClickListener(this);
    }

    @Override
    public void initData() {
        requestPermission();
        boolean success = setFaceConfig();
        if (!success) {
            showToast("初始化失败 = json配置文件解析出错");
            return;
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(mContext, mContext.getString(R.string.bd_license_id),
                mContext.getString(R.string.bd_license_file_name), new IInitCallback() {
                    @Override
                    public void initSuccess() {
                        runOnUiThread(() -> {
                            Log.e(TAG, "初始化成功");
                            showToast("初始化成功");
                            mFaceInitSuccess = true;
                        });
                    }

                    @Override
                    public void initFailure(final int errCode, final String errMsg) {
                        runOnUiThread(() -> {
                            Log.e(TAG, "初始化失败 = " + errCode + " " + errMsg);
                            showToast("初始化失败 = " + errCode + ", " + errMsg);
                            mFaceInitSuccess = false;
                        });
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        new RxPermissions(this).request(Manifest.permission.CAMERA).subscribe(aBoolean -> {
            if (!aBoolean) {
                showToast("请在设置中手动打开相机权限！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 有多处人脸识别时，不要随便释放，否则会出现莫名其妙的bug
        FaceSDKManager.getInstance().release();
        FaceBitmapSaveUtils.getInstance().release();
    }

    @Override
    public void onClick(View v) {
        if (!mFaceInitSuccess) {
            showToast("未初始化成功！");
            return;
        }
        switch (v.getId()) {
            case R.id.but_start_setting:
                FaceDetectSettingActivity.start(mContext);
                break;
            case R.id.but_start_detect:
                if (FaceInitBean.isActionLive) {
                    FaceLiveExpendActivity.startActivityResult(mContext, 101);
                } else {
                    FaceDetectExpendActivity.startActivityResult(mContext, 100);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 参数配置方法
     */
    private boolean setFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
        // 获取保存的质量等级
        FaceSpUtil util = new FaceSpUtil(mContext);
        int qualityLevel = (int) util.getSharedPreference(FaceConst.KEY_QUALITY_LEVEL_SAVE, -1);
        if (qualityLevel == -1) {
            qualityLevel = FaceInitBean.qualityLevel;
        }
        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        QualityConfigManager manager = QualityConfigManager.getInstance();
        manager.readQualityFile(mContext.getApplicationContext(), qualityLevel);
        QualityConfig qualityConfig = manager.getConfig();
        if (qualityConfig == null) {
            return false;
        }
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
        // 设置可检测的最小人脸阈值
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // 设置可检测到人脸的阈值
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 设置闭眼阈值
        config.setEyeClosedValue(FaceEnvironment.VALUE_CLOSE_EYES);
        // 设置图片缓存数量
        config.setCacheImageNum(FaceEnvironment.VALUE_CACHE_IMAGE_NUM);
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.setLivenessTypeList(FaceInitBean.livenessList);
        // 设置动作活体是否随机
        config.setLivenessRandom(FaceInitBean.isLivenessRandom);
        // 设置开启提示音
        config.setSound(FaceInitBean.isOpenSound);
        // 原图缩放系数
        config.setScale(FaceEnvironment.VALUE_SCALE);
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.setCropHeight(FaceEnvironment.VALUE_CROP_HEIGHT);
        config.setCropWidth(FaceEnvironment.VALUE_CROP_WIDTH);
        // 抠图人脸框与背景比例
        config.setEnlargeRatio(FaceEnvironment.VALUE_CROP_ENLARGERATIO);
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.setSecType(FaceEnvironment.VALUE_SEC_TYPE);
        // 检测超时设置
        config.setTimeDetectModule(FaceEnvironment.TIME_DETECT_MODULE);
        // 检测框远近比率
        config.setFaceFarRatio(FaceEnvironment.VALUE_FAR_RATIO);
        config.setFaceClosedRatio(FaceEnvironment.VALUE_CLOSED_RATIO);
        FaceSDKManager.getInstance().setFaceConfig(config);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String bmpStr = FaceBitmapSaveUtils.getInstance().getBitmap();
        if (TextUtils.isEmpty(bmpStr)) {
            return;
        }
        Bitmap bmp = base64ToBitmap(bmpStr);
        bmp = FaceSDKManager.getInstance().scaleImage(bmp, DensityUtils.dip2px(getApplicationContext(), 97),
                DensityUtils.dip2px(getApplicationContext(), 97));
        vdb.circleHead.setImageBitmap(bmp);
    }

    private Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}