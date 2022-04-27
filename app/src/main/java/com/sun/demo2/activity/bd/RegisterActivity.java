package com.sun.demo2.activity.bd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.common.toast.ToastHelper;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityRegisterBinding;
import com.sun.face.exception.FaceError;
import com.sun.face.model.RegResult;
import com.sun.face.other.FaceSDKManager;
import com.sun.face.ui.service.APIService;
import com.sun.face.util.ImageSaveUtil;
import com.sun.face.util.Md5;
import com.sun.face.util.OnResultListener;

import java.io.File;
/**
 * @author: Harper
 * @date: 2022/4/26
 * @note:
 */
public class RegisterActivity extends BaseMvpActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_DETECT_FACE = 1000;
    private ActivityRegisterBinding bind;
    private String facePath;
    private Bitmap mHeadBmp;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        bind = (ActivityRegisterBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        init();
        bind.detectBtn.setOnClickListener(this);
        bind.submitBtn.setOnClickListener(this);
    }

    private void init() {
        // 如果图片中的人脸小于200*200个像素，将不能检测出人脸，可以根据需求在100-400间调节大小
        FaceSDKManager.getInstance().getFaceTracker(this).set_min_face_size(200);
        FaceSDKManager.getInstance().getFaceTracker(this).set_isCheckQuality(true);
        // 该角度为商学，左右，偏头的角度的阀值，大于将无法检测出人脸，为了在1：n的时候分数高，注册尽量使用比较正的人脸，可自行条件角度
        FaceSDKManager.getInstance().getFaceTracker(this).set_eulur_angle_thr(45, 45, 45);
        FaceSDKManager.getInstance().getFaceTracker(this).set_isVerifyLive(true);
    }

    @Override
    public void onClick(View v) {
        if (v == bind.detectBtn) {
            Intent it = new Intent(this, FaceDetectActivity.class);
            startActivityForResult(it, REQUEST_CODE_DETECT_FACE);
        } else if (v == bind.submitBtn) {
            // 人脸注册和1：n属于在线接口，需要通过ak，sk获得token后进行调用，此方法为获取token，为了防止你得ak，sk泄露，
            // 建议把此调用放在您的服务端

            if (TextUtils.isEmpty(facePath)) {
                ToastHelper.showCommonToast("请先进行人脸采集");
            } else {
                // 您可以使用在线活体检测后在进行注册，这样安全性更高，也可以直接注册。（在线活体请在官网控制台提交申请工单）
                reg(facePath);
                // onlineLiveness(facePath);
            }
        }
    }

    private void reg(String filePath) {
        String username = bind.usernameEt.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastHelper.showCommonToast("姓名不能为空");
            return;
        }
        final File file = new File(filePath);
        if (!file.exists()) {
            ToastHelper.showCommonToast("文件不存在");
            return;
        }
        // TODO 人脸注册说明 https://aip.baidubce.com/rest/2.0/face/v2/faceset/user/add
        // 模拟注册，先提交信息注册获取uid，再使用人脸+uid到百度人脸库注册，
        // TODO 实际使用中，建议注册放到您的服务端进行（这样可以有效防止ak，sk泄露） 把注册信息包括人脸一次性提交到您的服务端，
        // TODO 注册获得uid，然后uid+人脸调用百度人脸注册接口，进行注册。
        // 每个开发者账号只能创建一个人脸库；
        // 每个人脸库下，用户组（group）数量没有限制；
        // 每个用户组（group）下，可添加最多300000张人脸，如每个uid注册一张人脸，则最多300000个用户uid；
        // 每个用户（uid）所能注册的最大人脸数量没有限制；
        // 说明：人脸注册完毕后，生效时间最长为35s，之后便可以进行识别或认证操作。
        // 说明：注册的人脸，建议为用户正面人脸。
        // 说明：uid在库中已经存在时，对此uid重复注册时，新注册的图片默认会追加到该uid下，如果手动选择action_type:replace，
        // 则会用新图替换库中该uid下所有图片。
        // uid          是	string	用户id（由数字、字母、下划线组成），长度限制128B
        // user_info    是	string	用户资料，长度限制256B
        // group_id	    是	string	用户组id，标识一组用户（由数字、字母、下划线组成），长度限制128B。
        // 如果需要将一个uid注册到多个group下，group_id,需要用多个逗号分隔，每个group_id长度限制为48个英文字符
        // image	    是	string	图像base64编码，每次仅支持单张图片，图片编码后大小不超过10M
        // action_type	否	string	参数包含append、replace。如果为“replace”，则每次注册时进行替换replace（新增或更新）操作，
        // 默认为append操作
        new Handler().postDelayed(() -> faceReg(file), 1000);
    }

    private void faceReg(File file) {
        // 用户id（由数字、字母、下划线组成），长度限制128B
        // uid为用户的id,百度对uid不做限制和处理，应该与您的帐号系统中的用户id对应。
        //   String uid = UUID.randomUUID().toString().substring(0, 8) + "_123";
        // String uid = 修改为自己用户系统中用户的id;
        // 模拟使用username替代
        String username = bind.usernameEt.getText().toString().trim();
        String uid = Md5.MD5(username, "utf-8");
        APIService.getInstance().reg(new OnResultListener<RegResult>() {
            @Override
            public void onResult(RegResult result) {
                Log.i("wtf", "orientation->" + result.getJsonRes());
                ToastHelper.showCommonToast("注册成功！");
                finish();
            }

            @Override
            public void onError(FaceError error) {
                ToastHelper.showCommonToast("注册失败" + error.getErrorMessage());
            }
        }, file, uid, username);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETECT_FACE && resultCode == Activity.RESULT_OK) {

            facePath = ImageSaveUtil.loadCameraBitmapPath(this, "head_tmp.jpg");
            if (mHeadBmp != null) {
                mHeadBmp.recycle();
            }
            mHeadBmp = ImageSaveUtil.loadBitmapFromPath(this, facePath);
            if (mHeadBmp != null) {
                bind.avatarIv.setImageBitmap(mHeadBmp);
            }
        }
    }
}