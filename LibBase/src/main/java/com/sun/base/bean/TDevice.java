package com.sun.base.bean;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.sun.base.R;
import com.sun.base.util.LogHelper;
import com.sun.base.util.StringUtil;
import com.sun.base.util.ToastUtil;
import com.sun.common.toast.ToastHelper;
import com.sun.common.util.AppUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 设备相关工具类
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TDevice {

    private static final String TAG = "TDevice";
    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    private static Boolean _hasBigScreen = null;
    private static Boolean _hasCamera = null;
    private static Boolean _isTablet = null;
    private static Integer _loadFactor = null;
    private static float displayDensity = 0.0F;


    private TDevice() {
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = AppUtil.getCtx().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = AppUtil.getCtx().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getDefaultLoadFactor() {
        if (_loadFactor == null) {
            Integer integer = Integer.valueOf(0xf & AppUtil.getCtx().getResources().getConfiguration().screenLayout);
            _loadFactor = integer;
            _loadFactor = Integer.valueOf(Math.max(integer.intValue(), 1));
        }
        return _loadFactor.intValue();
    }

    public static float getDensity() {
        if (displayDensity == 0.0) {
            displayDensity = getDisplayMetrics().density;
        }
        return displayDensity;
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) AppUtil.getCtx().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int[] getRealScreenSize(Activity activity) {
        int[] size = new int[2];
        int screenWidth, screenHeight;
        WindowManager w = activity.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT < 17) {
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth")
                        .invoke(d);
                screenHeight = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
                LogHelper.e(TAG, "getRawWidth or getRawHeight exception", ignored);
            }
        }
        // includes window decorations (statusbar bar/menu bar)
        else if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d,
                        realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
                LogHelper.e(TAG, "getRealSize exception", ignored);
            }
        }
        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;
    }

    public static boolean hasBigScreen() {
        boolean flag = true;
        if (_hasBigScreen == null) {
            boolean flag1;
            flag1 = (0xf & AppUtil.getCtx().getResources()
                    .getConfiguration().screenLayout) >= 3;
            Boolean boolean1 = Boolean.valueOf(flag1);
            _hasBigScreen = boolean1;
            if (!boolean1.booleanValue()) {
                if (getDensity() <= 1.5F) {
                    flag = false;
                }
                _hasBigScreen = Boolean.valueOf(flag);
            }
        }
        return _hasBigScreen.booleanValue();
    }

    public static boolean hasCamera() {
        if (_hasCamera == null) {
            PackageManager pckMgr = AppUtil.getCtx()
                    .getPackageManager();
            boolean flag = pckMgr
                    .hasSystemFeature("android.hardware.camera.front");
            boolean flag1 = pckMgr.hasSystemFeature("android.hardware.camera");
            boolean flag2 = flag || flag1;
            _hasCamera = Boolean.valueOf(flag2);
        }
        return _hasCamera.booleanValue();
    }

    /**
     * 判断设备是否支持闪光灯
     *
     * @return boolean
     */
    public static boolean isSupportFlash() {
        PackageManager pm = AppUtil.getCtx().getPackageManager();
        FeatureInfo[] features = pm.getSystemAvailableFeatures();
        for (FeatureInfo f : features) {
            // 判断设备是否支持闪光灯
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasInternet() {
        return ((ConnectivityManager) AppUtil.getCtx().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static boolean gotoGoogleMarket(Activity activity, String pck) {
        try {
            Intent intent = new Intent();
            intent.setPackage("com.android.vending");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + pck));
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPackageExist(String pckName) {
        try {
            PackageInfo pckInfo = AppUtil.getCtx().getPackageManager()
                    .getPackageInfo(pckName, 0);
            if (pckInfo != null) {
                return true;
            }
        } catch (NameNotFoundException e) {
            LogHelper.e(e.getMessage());
        }
        return false;
    }

    public static void hideSoftKeyboard(View view) {
        if (view == null) {
            return;
        }
        ((InputMethodManager) AppUtil.getCtx().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    public static boolean isLandscape() {
        return AppUtil.getCtx().getResources().getConfiguration().orientation == 2;
    }

    public static boolean isPortrait() {
        boolean flag = true;
        if (AppUtil.getCtx().getResources().getConfiguration().orientation != 1) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @return
     */
    public static boolean isTablet() {
        if (_isTablet == null) {
            boolean flag = (AppUtil.getCtx().getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
            _isTablet = Boolean.valueOf(flag);
        }
        return _isTablet.booleanValue();
    }

    public static float pixelsToDp(float f) {
        return f / (getDisplayMetrics().densityDpi / 160F);
    }

    public static void showSoftKeyboard(Dialog dialog) {
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(4);
            }
        }
    }

    public static void showSoftKeyboard(View view) {
        ((InputMethodManager) AppUtil.getCtx().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void toogleSoftKeyboard(View view) {
        ((InputMethodManager) AppUtil.getCtx().getSystemService(
                Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 判断是否有外部存储设备sdcard <br/>
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    public static String percent(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        str = nf.format(p3);
        return str;
    }

    public static String percent2(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str = nf.format(p3);
        return str;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static void gotoMarket(Context context, String pck) {
        if (!isHaveMarket(context)) {
            ToastHelper.showCommonToast("你手机中没有安装应用市场");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static void gotoMarket2(Context context, String pck) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            ToastHelper.showCommonToast("你手机中没有安装应用市场");
        }
    }

    public static boolean isHaveMarket(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    public static void openAppInMarket(Context context) {
        if (context != null) {
            String pckName = context.getPackageName();
            try {
                gotoMarket(context, pckName);
            } catch (Exception ex) {
                try {
                    String otherMarketUri = "http://market.android.com/details?id="
                            + pckName;
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(otherMarketUri));
                    context.startActivity(intent);
                } catch (Exception e) {
                    LogHelper.e(TAG, "openAppInMarket exception", e);
                }
            }
        }
    }

    public static void setFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void cancelFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static PackageInfo getPackageInfo(String pckName) {
        try {
            return AppUtil.getCtx().getPackageManager()
                    .getPackageInfo(pckName, 0);
        } catch (NameNotFoundException e) {
            LogHelper.e(e.getMessage());
        }
        return null;
    }

    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode() {
        int versionCode;
        try {
            versionCode = AppUtil.getCtx()
                    .getPackageManager()
                    .getPackageInfo(AppUtil.getPackageName(),
                            0).versionCode;
        } catch (NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    public static int getVersionCode(String packageName) {
        int versionCode;
        try {
            versionCode = AppUtil.getCtx().getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    public static String getVersionName() {
        String name;
        try {
            name = AppUtil.getCtx()
                    .getPackageManager()
                    .getPackageInfo(AppUtil.getPackageName(),
                            0).versionName;
        } catch (NameNotFoundException ex) {
            name = "";
        }
        return name;
    }

    /**
     * 安装apk
     *
     * @param context
     * @param filePath
     */
    public static void installAPK(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        installAPK(context, new File(filePath));
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void installAPK(Context context, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            // 安装
            Intent intent = new Intent();
            // 下载完成打开安装界面
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;

            //兼容7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".fileprovider",
                        file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                uri = Uri.fromFile(file);
                //由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            /*
             * 经过实验，发现无论是否成功安装，该Intent都返回result为0 具体结果如下： type requestCode
             * resultCode data 取消安装 10086 0 null 覆盖安装 10086 0 null 全新安装 10086 0
             * null
             */
            context.startActivity(intent);
        } catch (Exception e) {
            LogHelper.e(TAG, "installAPK Exception", e);
            //兼容8.0的逻辑放在这里面，因为context.getPackageManager().canRequestPackageInstalls()这个判断在很多国产ROM里面始终返回为false即使允许了。。。
            //比如华为或小米或Oppo的部分设备
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    ToastUtil.showShort(context, "请设置本应用未知来源应用权限为允许");
                    startInstallPermissionSettingActivity(context);
                    return;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        //注意这个是8.0新API
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Intent getInstallApkIntent(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
    }

    public static void openDial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(it);
    }

    public static void openSMS(Context context, String smsBody, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsBody);
        context.startActivity(it);
    }

    public static void openDail(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openSendMsg(Context context) {
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("WrongConstant")
    public static void openCamera(Context context) {
        Intent intent = new Intent(); // 调用照相机
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        intent.setFlags(0x34c40000);
        context.startActivity(intent);
    }

    /**
     * 获取IMEI号
     * 仅仅只对Android手机有效(一些如平板电脑的设置没有通话功能是没有imei号的) 需要在AndroidManifest.xml中加入一个许可：android.permission.READ_PHONE_STATE
     *
     * @return
     */
    public static String getIMEI() {
        TelephonyManager tel = (TelephonyManager) AppUtil.getCtx().getSystemService(Context.TELEPHONY_SERVICE);
        if (tel == null) {
            return null;
        }
        return tel.getDeviceId();
    }

    /**
     * 获取mac地址
     * 你需要为你的工程加入android.permission.ACCESS_WIFI_STATE 权限，否则这个地址会为null
     *
     * @return
     */
    public static String getWlanMacAddress() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) AppUtil.getCtx().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return null;
        }
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if (wifiInfo == null) {
            return null;
        }
        String mac = wifiInfo.getMacAddress();
        if (mac != null) {
            mac = mac.trim();
        }
        if ("02:00:00:00:00:00".equalsIgnoreCase(mac)) {//Android 7.0及以上或者部分设备通过WifiInfo的getMacAddress方法获取到的mac地址始终为02:00:00:00:00:00
            String localMacAddressFromIp = getLocalMacAddressFromIp();
            if (localMacAddressFromIp != null) {
                localMacAddressFromIp = localMacAddressFromIp.trim();
            }
            if (!TextUtils.isEmpty(localMacAddressFromIp)) {
                return localMacAddressFromIp;
            }
            String macWithNetworkInterface = getMacWithNetworkInterface();
            if (macWithNetworkInterface != null) {
                macWithNetworkInterface = macWithNetworkInterface.trim();
            }
            return macWithNetworkInterface;
        } else {
            return mac;
        }
    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    private static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            LogHelper.e(TAG, "getLocalMacAddressFromIp exception", e);
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        break;
                    } else {
                        ip = null;
                    }
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            LogHelper.e(TAG, "getLocalInetAddress exception", e);
        }
        return ip;
    }

    /**
     * 通过网络接口获取mac地址
     *
     * @return
     */
    private static String getMacWithNetworkInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            LogHelper.e(TAG, "getMacWithNetworkInterface exception", ex);
        }
        return null;
    }

    /**
     * 通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来计算出唯一的id
     *
     * @return
     */
    public static String getPesudoUniqueID() {
        String pesudoUniqueID = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return pesudoUniqueID;
    }

    private static String sUniqueDeviceID;//唯一的设备ID

    /**
     * 获取唯一的设备ID
     * 优先级是mac地址>imei>pesudoUniqueID
     *
     * @return
     */
    public static String getUniqueDeviceID(Context context) {
        if (!TextUtils.isEmpty(sUniqueDeviceID)) {
            return sUniqueDeviceID;
        }
        try {
            sUniqueDeviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!TextUtils.isEmpty(sUniqueDeviceID)) {
            return sUniqueDeviceID;
        }
        try {
            sUniqueDeviceID = getWlanMacAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        sUniqueDeviceID = getIMEI();
        if (!TextUtils.isEmpty(sUniqueDeviceID)) {
            return sUniqueDeviceID;
        }
        sUniqueDeviceID = getPesudoUniqueID();
        return sUniqueDeviceID;
    }

    public static String getPhoneType() {
        return Build.MODEL;
    }

    public static void openApp(Context context, String packageName) {
        Intent mainIntent = AppUtil.getCtx().getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (mainIntent == null) {
            mainIntent = new Intent(packageName);
        } else {
            LogHelper.i("Action:" + mainIntent.getAction());
        }
        context.startActivity(mainIntent);
    }

    public static boolean openAppActivity(Context context, String packageName,
                                          String activityName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, activityName);
        intent.setComponent(cn);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void uninstallApk(Context context, String packageName) {
        if (isPackageExist(packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                    packageURI);
            context.startActivity(uninstallIntent);
        }
    }

    @SuppressWarnings("deprecation")
    public static void copyTextToBoard(Context context, String string) {
        if (TextUtils.isEmpty(string)) {
            return;
        }
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(string);
        ToastHelper.showCommonToast(AppUtil.getCtx().getResources().getString(R.string.copied_to_pasteboard));
    }

    /**
     * 发送邮件
     *
     * @param context
     * @param subject 主题
     * @param content 内容
     * @param emails  邮件地址
     */
    public static void sendEmail(Context context, String subject,
                                 String content, String... emails) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            // 模拟器
            // intent.setType("text/plain");
            intent.setType("message/rfc822"); // 真机
            intent.putExtra(Intent.EXTRA_EMAIL, emails);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, content);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        return (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    /**
     * 调用系统安装了的应用分享
     *
     * @param context
     * @param title
     * @param url
     */
    public static void showSystemShareOption(Activity context,
                                             final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppUtil.getCtx()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtil.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        Resources resources = AppUtil.getCtx().getResources();
        int resId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = resources.getDimensionPixelOffset(resId);
        }
        return result;
    }

    /**
     * 获取view在当前窗口内的绝对坐标
     *
     * @param view
     * @return location [0]--->x坐标,location [1]--->y坐标
     */
    public static int[] getLocationInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    /**
     * 获取view在整个屏幕内的绝对坐标(包括了通知栏的高度)
     *
     * @param view
     * @return location [0]--->x坐标,location [1]--->y坐标
     */
    public static int[] getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    /**
     * 获取当前系统版本号 <br/>
     */
    public static String getSDK() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    /**
     * 判断安装的版本是不是debug版本 <br/>
     */
    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = AppUtil.getCtx().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商 eg:Huawei
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号 eg:HUAWEI MT7-TL00
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号 eg:6.0
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取 AndroidManifest 中 meta-data 对应的字符串值
     *
     * @param key meta-data name
     */
    public static String getMetaString(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return info.metaData.getString(key);
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取 AndroidManifest 中 meta-data 对应的布尔值
     *
     * @param key meta-data name
     */
    public static boolean getMetaBoolean(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return info.metaData.getBoolean(key);
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取 AndroidManifest 中 meta-data 对应的字符串值
     *
     * @param keys meta-data name
     */
    public static SparseArray<String> getMetaString(Context context, Collection<String> keys) {
        SparseArray<String> result = new SparseArray<>();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            int index = 0;
            for (String key : keys) {
                result.put(index++, info.metaData.getString(key));
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            return result;
        }
    }

    /**
     * 当前app是否开启通知权限
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {
        /*String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        *//* Context.APP_OPS_MANAGER *//*
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            MyLogUtil.e(TAG, "AppOpsManager exception", e);
        }
        return true;*/
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 跳转通知设置界面
     *
     * @param context
     */
    public static void go2NotificationSetting(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断设备是否有前置摄像头
     *
     * @param context
     * @return
     */
    public static boolean hasFrontCamera(Context context) {
        boolean hasCamera = false;
        PackageManager pm = context.getPackageManager();
        hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD || Camera.getNumberOfCameras() > 0;
        return hasCamera;
    }

    /**
     * 是否有悬浮窗权限
     *
     * @param context
     * @return
     */
    public static boolean hasOverlayPermission(Context context) {
        //当AndroidSDK>=23及Android版本6.0及以上时，需要获取OVERLAY_PERMISSION.
        //使用canDrawOverlays用于检查，下面为其源码。其中也提醒了需要在manifest文件中添加权限.
        /**
         * Checks if the specified context can draw on top of other apps. As of API
         * level 23, an app cannot draw on top of other apps unless it declares the
         * {@link android.Manifest.permission#SYSTEM_ALERT_WINDOW} permission in its
         * manifest, <em>and</em> the user specifically grants the app this
         * capability. To prompt the user to grant this approval, the app must send an
         * intent with the action
         * {@link Settings#ACTION_MANAGE_OVERLAY_PERMISSION}, which
         * causes the system to display a permission management screen.
         *
         */
        //SDK在23以下，不用管.直接有权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //23及23以上要做额外判断
        boolean hasPermission = false;
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (manager != null) {
            try {
                int result = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                hasPermission = result == AppOpsManager.MODE_ALLOWED;
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        return hasPermission && Settings.canDrawOverlays(context);
    }
}
