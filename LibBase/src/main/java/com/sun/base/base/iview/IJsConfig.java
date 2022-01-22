package com.sun.base.base.iview;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.sun.base.net.state.NetworkUtil;
import com.sun.base.service.ServiceFactory;
import com.sun.common.toast.ToastHelper;
import com.sun.common.util.AppUtil;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: jssdk
 */
public class IJsConfig {

    private final Activity mActivity;

    public IJsConfig(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 获取客户端版本号
     *
     * @return
     */
    @JavascriptInterface
    public String getAppVersion() {
        return AppUtil.getVersionName();
    }

    /**
     * 设置标题
     *
     * @param title
     */
    @JavascriptInterface
    public void setTitle(String title) {
        //TODO
    }

    /**
     * 获取当前登录用户Id
     *
     * @return
     */
    @JavascriptInterface
    public String getUserId() {
        return ServiceFactory.getInstance().getAccountService().getAccountId();
    }

    /**
     * 获取当前token
     *
     * @return
     */
    @JavascriptInterface
    public String getToken() {
        return ServiceFactory.getInstance().getAccountService().getToken();
    }

    /**
     * 更新token
     */
    @JavascriptInterface
    public void updateToken() {
        //TODO
    }

    /**
     * 获取网络状态
     *
     * @return 当前网络状态 “wifi”， “wwan” 蜂窝， “none”没有网络
     */
    @JavascriptInterface
    public String getNetworkState() {
        return NetworkUtil.getNetworkType();
    }

    /**
     * 设置当前页面返回类型
     *
     * @param backType       0：close window, 1: history back, 2: callback
     * @param callbackMethod 回调的JS方法名
     */
    @JavascriptInterface
    public void setGoBackType(int backType, String callbackMethod) {
        //TODO
    }

    /**
     * 关闭当前窗口
     */
    @JavascriptInterface
    public void close() {
        mActivity.finish();
    }

    /**
     * 返回
     */
    @JavascriptInterface
    public void goBack() {
        //TODO
    }

    /**
     * 跳转app内部页面
     *
     * @param internalLink 符合banner的内部跳转参数格式
     */
    @JavascriptInterface
    public void productEntry(String internalLink) {
        //TODO
    }

    /**
     * 新窗口打开链接
     *
     * @param url 跳转链接
     */
    @JavascriptInterface
    public void openBrowseWithUrl(String url) {
        //TODO
    }

    /**
     * 设置导航栏显示状态
     *
     * @param state 0:GONE 1:VISIBLE 2:INVISIBLE
     */
    @JavascriptInterface
    public void setToolbarVisibility(int state) {
        //TODO
    }

    /**
     * 弹出Toast提示
     *
     * @param toast 提示信息
     */
    @JavascriptInterface
    public void showToast(String toast) {
        if (mActivity != null) {
            mActivity.runOnUiThread(() -> ToastHelper.showCommonToast(toast));
        }
    }

    /**
     * 第三方分享
     *
     * @param type      分享类型，传空则客户端弹出分享选择界面
     * @param title     分享标题
     * @param content   分享信息内容
     * @param targetUrl 分享链接
     * @param imgUrl    分享图片链接
     * @param callback  分享成功后回调的JS方法名
     */
    @JavascriptInterface
    public void share(String type, String title, String content,
                      String targetUrl, String imgUrl, String callback) {
        //TODO
    }

    /**
     * 保存图片到相册
     *
     * @param url       图片文件链接
     * @param imageName 图片文件名（iOS不保存文件名称）
     */
    @JavascriptInterface
    public void saveImageWithUrl(String url, String imageName) {
        //TODO
    }

}
