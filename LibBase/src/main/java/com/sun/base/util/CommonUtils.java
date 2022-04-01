package com.sun.base.util;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.collection.ArrayMap;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;


import com.google.gson.Gson;
import com.sun.base.bean.TDevice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getName();

    private CommonUtils() {
        throw new UnsupportedOperationException("CommonUtils不能被构造");
    }

    /**
     * 上次点击时间
     */
    private static long lastClickTime = 0;

    /**
     * 判断是否快速点击，避免重复点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        //两次间隔时间太短就认为是快速点击
        if (timeD >= 0 && timeD <= 200) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    /**
     * 对象深拷贝
     *
     * @param input
     * @param <T>
     * @return
     */
    public static <T> T copy(Parcelable input) {
        Parcel parcel = null;

        try {
            parcel = Parcel.obtain();
            parcel.writeParcelable(input, 0);

            parcel.setDataPosition(0);
            return parcel.readParcelable(input.getClass().getClassLoader());
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    /**
     * Deep clone.
     *
     * @param data The data.
     * @param type The type.
     * @param <T>  The value type.
     * @return The object of cloned.
     */
    public static <T> T deepClone(final T data, final Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson(data), type);
        } catch (Exception e) {
            LogHelper.e(TAG, "Exception", e);
            return null;
        }
    }

    /**
     * 获取小数点后maxcount位，并且四舍五入(如果已经是maxcount位则不处理)
     *
     * @param number   数字
     * @param maxcount 小数点后maxcount位数
     * @return
     */
    public static float getNumber(float number, int maxcount) {
        float resultnumber = number;

        try {
            BigDecimal b = BigDecimal.valueOf(number);
            int count = b.scale();
            if (count <= maxcount) {
                return resultnumber;
            }

            b = b.setScale(maxcount, BigDecimal.ROUND_HALF_UP);
            resultnumber = b.floatValue();

        } catch (Exception ex) {
            LogHelper.e(TAG, "Exception", ex);
        }

        return resultnumber;
    }

    /**
     * 获取小数点后maxcount位，并且四舍五入(如果已经是maxcount位则不处理)
     *
     * @param number   数字
     * @param maxcount 小数点后maxcount位数
     * @return
     */
    public static double getNumber(double number, int maxcount) {
        double resultnumber = number;

        try {
            BigDecimal b = BigDecimal.valueOf(number);
            int count = b.scale();
            if (count <= maxcount) {
                return resultnumber;
            }

            b = b.setScale(maxcount, BigDecimal.ROUND_HALF_UP);
            resultnumber = b.doubleValue();

        } catch (Exception ex) {
            LogHelper.e(TAG, "Exception", ex);
        }

        return resultnumber;
    }


    /**
     * 获取小数点后maxcount位，并且四舍五入(如果已经是maxcount位则不处理)
     *
     * @param number   数字
     * @param maxcount 小数点后maxcount位数
     * @return
     */
    public static long getNumber(long number, int maxcount) {
        long resultnumber = number;

        try {
            BigDecimal b = BigDecimal.valueOf(number);
            int count = b.scale();
            if (count <= maxcount) {
                return resultnumber;
            }

            b = b.setScale(maxcount, BigDecimal.ROUND_HALF_UP);
            resultnumber = b.longValue();

        } catch (Exception ex) {
            LogHelper.e(TAG, "Exception", ex);
        }

        return resultnumber;
    }


    private static final String OLD_FS_URL_PREFIX = "http://fs.yixuexiao.cn";//旧的阿里云文件前缀
    private static final String NEW_FS_URL_PREFIX = "http://zhixue-ugc.oss-cn-hangzhou.aliyuncs.com";//新的阿里云文件前缀

    /**
     * 获取url完整路径
     *
     * @param url 服务端返回的url，有可能不完整
     * @return
     */
    public static String getFsUrl(String url) {
        return getFsUrl(url, false);
    }

    /**
     * 获取url完整路径
     *
     * @param url   服务端返回的url，有可能不完整
     * @param isNew 是否用新的阿里云文件前缀
     * @return
     */
    public static String getFsUrl(String url, boolean isNew) {
        if (URLUtil.isNetworkUrl(url)) {//如果已经是完整的url了，就直接返回
            return url;
        }
        final String prefix = isNew ? NEW_FS_URL_PREFIX : OLD_FS_URL_PREFIX;
        if (url.startsWith("/")) {
            url = prefix + url;
        } else {
            url = prefix + "/" + url;
        }
        return url;
    }

    /**
     * 设置View的背景，设置背景时会清空padding值，所以要额外处理下，保证显示正常
     *
     * @param view  要设置背景的View
     * @param resId 背景资源id
     */
    public static void setViewBackground(View view, @DrawableRes int resId) {
        setViewBackground(view, resId, false);
    }

    /**
     * 设置View的背景，设置背景时会清空padding值，所以要额外处理下，保证显示正常
     *
     * @param view         要设置背景的View
     * @param resId        背景资源id
     * @param needCatchOOM 是否需要捕获OOM异常错误 一般设置大图的时候 需要将此值设置为true
     */
    public static void setViewBackground(View view, @DrawableRes int resId, boolean needCatchOOM) {
        //先将padding值保存下来
        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingRight = view.getPaddingRight();
        int paddingBottom = view.getPaddingBottom();
        //设置背景
        if (!needCatchOOM) {
            view.setBackgroundResource(resId);
        } else {
            int inSampleSize = 1;//缩放比例
            boolean isSuccess = false;//设置背景是否成功
            while (!isSuccess) {
                try {
                    if (inSampleSize == 1) {//代表不缩放，直接设置背景
                        view.setBackgroundResource(resId);
                    } else {//代表缩放
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;
                        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), resId, options);
                        view.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }
                    isSuccess = true;
                } catch (OutOfMemoryError outOfMemoryError) {
                    LogHelper.e(TAG, "setViewBackground OutOfMemoryError", outOfMemoryError);
                    inSampleSize *= 2;//发现OOM了，就将缩放比例乘以2
                }
            }
        }
        //恢复padding值
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    /**
     * 给ImageView的图片设置tint
     *
     * @param imageView 要设置的ImageView控件
     * @param resId     图片资源id
     * @param tintColor tint颜色值
     */
    public static void setImageViewResourceWithTint(ImageView imageView, @DrawableRes int resId, @ColorInt int tintColor) {
        //1:通过图片资源文件生成Drawable实例
        Drawable drawable = imageView.getResources().getDrawable(resId).mutate();
        //2:先调用DrawableCompat的wrap方法
        drawable = DrawableCompat.wrap(drawable);
        //3:再调用DrawableCompat的setTint方法，为Drawable实例进行着色
        DrawableCompat.setTint(drawable, tintColor);
        //4:最后将着色的Drawable设置给ImageView
        imageView.setImageDrawable(drawable);
    }

    /**
     * 使用Map按key进行排序
     *
     * @param map 待排序的map
     * @return
     */
    public static <T> Map<Integer, T> sortMapByKey(Map<Integer, T> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, T> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }


    /**
     * 比较器类
     */
    private static class MapKeyComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer left, Integer right) {
            return left.compareTo(right);
        }
    }

    /**
     * 设置可不可以同时点击多个（递归设置）
     *
     * @param viewGroup 要设置的viewGroup
     * @param enabled   true表示可以，false表示不可以
     */
    public static void setMotionEventSplittingEnabled(ViewGroup viewGroup, boolean enabled) {
        if (viewGroup == null) {
            return;
        }
        viewGroup.setMotionEventSplittingEnabled(enabled);
        //ListView要额外处理headerView和FooterView
        if (viewGroup instanceof ListView) {
            setListViewMotionEventSplittingEnabled(enabled, (ListView) viewGroup);
        }
        int childCount = viewGroup.getChildCount();
        if (childCount <= 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                setMotionEventSplittingEnabled((ViewGroup) child, enabled);
            }
        }
    }

    /**
     * ListView要额外处理headerView和FooterView
     *
     * @param enabled
     * @param listView
     */
    private static void setListViewMotionEventSplittingEnabled(boolean enabled, ListView listView) {
        int headerViewsCount = listView.getHeaderViewsCount();
        if (headerViewsCount > 0) {
            ArrayList<ListView.FixedViewInfo> mHeaderViewInfos = ReflectUtils.reflect(listView).field("mHeaderViewInfos").get();
            if (!CollectionUtil.isEmpty(mHeaderViewInfos)) {
                for (ListView.FixedViewInfo fixedViewInfo : mHeaderViewInfos) {
                    View fixedViewInfoView = fixedViewInfo.view;
                    if (fixedViewInfoView instanceof ViewGroup) {
                        setMotionEventSplittingEnabled((ViewGroup) fixedViewInfoView, enabled);
                    }
                }
            }
        }
        int footerViewsCount = listView.getFooterViewsCount();
        if (footerViewsCount > 0) {
            ArrayList<ListView.FixedViewInfo> mFooterViewInfos = ReflectUtils.reflect(listView).field("mFooterViewInfos").get();
            if (!CollectionUtil.isEmpty(mFooterViewInfos)) {
                for (ListView.FixedViewInfo fixedViewInfo : mFooterViewInfos) {
                    View fixedViewInfoView = fixedViewInfo.view;
                    if (fixedViewInfoView instanceof ViewGroup) {
                        setMotionEventSplittingEnabled((ViewGroup) fixedViewInfoView, enabled);
                    }
                }
            }
        }
    }

    private static final Map<Integer, String> INDEX_TO_CHINESE_MAP;

    static {
        INDEX_TO_CHINESE_MAP = new ArrayMap<>();
        INDEX_TO_CHINESE_MAP.put(1, "一");
        INDEX_TO_CHINESE_MAP.put(2, "二");
        INDEX_TO_CHINESE_MAP.put(3, "三");
        INDEX_TO_CHINESE_MAP.put(4, "四");
        INDEX_TO_CHINESE_MAP.put(5, "五");
        INDEX_TO_CHINESE_MAP.put(6, "六");
        INDEX_TO_CHINESE_MAP.put(7, "七");
        INDEX_TO_CHINESE_MAP.put(8, "八");
        INDEX_TO_CHINESE_MAP.put(9, "九");
        INDEX_TO_CHINESE_MAP.put(10, "十");
        INDEX_TO_CHINESE_MAP.put(11, "十一");
        INDEX_TO_CHINESE_MAP.put(12, "十二");
        INDEX_TO_CHINESE_MAP.put(13, "十三");
        INDEX_TO_CHINESE_MAP.put(14, "十四");
        INDEX_TO_CHINESE_MAP.put(15, "十五");
        INDEX_TO_CHINESE_MAP.put(16, "十六");
        INDEX_TO_CHINESE_MAP.put(17, "十七");
        INDEX_TO_CHINESE_MAP.put(18, "十八");
        INDEX_TO_CHINESE_MAP.put(19, "十九");
        INDEX_TO_CHINESE_MAP.put(20, "二十");
        INDEX_TO_CHINESE_MAP.put(21, "二十一");
        INDEX_TO_CHINESE_MAP.put(22, "二十二");
        INDEX_TO_CHINESE_MAP.put(23, "二十三");
        INDEX_TO_CHINESE_MAP.put(24, "二十四");
        INDEX_TO_CHINESE_MAP.put(25, "二十五");
        INDEX_TO_CHINESE_MAP.put(26, "二十六");
        INDEX_TO_CHINESE_MAP.put(27, "二十七");
        INDEX_TO_CHINESE_MAP.put(28, "二十八");
        INDEX_TO_CHINESE_MAP.put(29, "二十九");
        INDEX_TO_CHINESE_MAP.put(30, "三十");
        INDEX_TO_CHINESE_MAP.put(31, "三十一");
        INDEX_TO_CHINESE_MAP.put(32, "三十二");
        INDEX_TO_CHINESE_MAP.put(33, "三十三");
        INDEX_TO_CHINESE_MAP.put(34, "三十四");
        INDEX_TO_CHINESE_MAP.put(35, "三十五");
        INDEX_TO_CHINESE_MAP.put(36, "三十六");
        INDEX_TO_CHINESE_MAP.put(37, "三十七");
        INDEX_TO_CHINESE_MAP.put(38, "三十八");
        INDEX_TO_CHINESE_MAP.put(39, "三十九");
        INDEX_TO_CHINESE_MAP.put(40, "四十");
        INDEX_TO_CHINESE_MAP.put(41, "四十一");
        INDEX_TO_CHINESE_MAP.put(42, "四十二");
        INDEX_TO_CHINESE_MAP.put(43, "四十三");
        INDEX_TO_CHINESE_MAP.put(44, "四十四");
        INDEX_TO_CHINESE_MAP.put(45, "四十五");
        INDEX_TO_CHINESE_MAP.put(46, "四十六");
        INDEX_TO_CHINESE_MAP.put(47, "四十七");
        INDEX_TO_CHINESE_MAP.put(48, "四十八");
        INDEX_TO_CHINESE_MAP.put(49, "四十九");
        INDEX_TO_CHINESE_MAP.put(50, "五十");
        INDEX_TO_CHINESE_MAP.put(51, "五十一");
        INDEX_TO_CHINESE_MAP.put(52, "五十二");
        INDEX_TO_CHINESE_MAP.put(53, "五十三");
        INDEX_TO_CHINESE_MAP.put(54, "五十四");
        INDEX_TO_CHINESE_MAP.put(55, "五十五");
        INDEX_TO_CHINESE_MAP.put(56, "五十六");
        INDEX_TO_CHINESE_MAP.put(57, "五十七");
        INDEX_TO_CHINESE_MAP.put(58, "五十八");
        INDEX_TO_CHINESE_MAP.put(59, "五十九");
        INDEX_TO_CHINESE_MAP.put(60, "六十");
        INDEX_TO_CHINESE_MAP.put(61, "六十一");
        INDEX_TO_CHINESE_MAP.put(62, "六十二");
        INDEX_TO_CHINESE_MAP.put(63, "六十三");
        INDEX_TO_CHINESE_MAP.put(64, "六十四");
        INDEX_TO_CHINESE_MAP.put(65, "六十五");
        INDEX_TO_CHINESE_MAP.put(66, "六十六");
        INDEX_TO_CHINESE_MAP.put(67, "六十七");
        INDEX_TO_CHINESE_MAP.put(68, "六十八");
        INDEX_TO_CHINESE_MAP.put(69, "六十九");
        INDEX_TO_CHINESE_MAP.put(70, "七十");
        INDEX_TO_CHINESE_MAP.put(71, "七十一");
        INDEX_TO_CHINESE_MAP.put(72, "七十二");
        INDEX_TO_CHINESE_MAP.put(73, "七十三");
        INDEX_TO_CHINESE_MAP.put(74, "七十四");
        INDEX_TO_CHINESE_MAP.put(75, "七十五");
        INDEX_TO_CHINESE_MAP.put(76, "七十六");
        INDEX_TO_CHINESE_MAP.put(77, "七十七");
        INDEX_TO_CHINESE_MAP.put(78, "七十八");
        INDEX_TO_CHINESE_MAP.put(79, "七十九");
        INDEX_TO_CHINESE_MAP.put(80, "八十");
        INDEX_TO_CHINESE_MAP.put(81, "八十一");
        INDEX_TO_CHINESE_MAP.put(82, "八十二");
        INDEX_TO_CHINESE_MAP.put(83, "八十三");
        INDEX_TO_CHINESE_MAP.put(84, "八十四");
        INDEX_TO_CHINESE_MAP.put(85, "八十五");
        INDEX_TO_CHINESE_MAP.put(86, "八十六");
        INDEX_TO_CHINESE_MAP.put(87, "八十七");
        INDEX_TO_CHINESE_MAP.put(88, "八十八");
        INDEX_TO_CHINESE_MAP.put(89, "八十九");
        INDEX_TO_CHINESE_MAP.put(90, "九十");
        INDEX_TO_CHINESE_MAP.put(91, "九十一");
        INDEX_TO_CHINESE_MAP.put(92, "九十二");
        INDEX_TO_CHINESE_MAP.put(93, "九十三");
        INDEX_TO_CHINESE_MAP.put(94, "九十四");
        INDEX_TO_CHINESE_MAP.put(95, "九十五");
        INDEX_TO_CHINESE_MAP.put(96, "九十六");
        INDEX_TO_CHINESE_MAP.put(97, "九十七");
        INDEX_TO_CHINESE_MAP.put(98, "九十八");
        INDEX_TO_CHINESE_MAP.put(99, "九十九");
        INDEX_TO_CHINESE_MAP.put(100, "一百");
    }

    /**
     * 根据下标索引返回对应的中文字符串 eg:"1"返回"一"
     *
     * @param index 从1开始
     * @return
     */
    public static String getChineseIndexStr(int index) {
        return INDEX_TO_CHINESE_MAP.get(index);
    }

    /**
     * 获取当前应用渠道号(application中用meta-data标记的友盟渠道号)
     *
     * @param context
     * @return
     */
    public static String getVendor(Context context) {
        String vendor = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            vendor = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            LogHelper.e(TAG, "getVendor exception", e);
        }
        return vendor;
    }

    /**
     * 从assets中获取文件输入流
     *
     * @param context
     * @param filePath 文件路径（相对assets）
     * @return
     */
    public static InputStream getInputStreamFromAssets(Context context, String filePath) throws IOException {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return context.getResources().getAssets().open(filePath);
    }

    /**
     * 获取剪贴板里面的内容
     *
     * @param context 上下文对象
     */
    public static String getClipBoardData(Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData data = cm.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                return data.getItemAt(0).coerceToText(context).toString();
            }
        }
        return null;
    }

    /**
     * 判断Fragment是否脱离界面了
     *
     * @param fragment
     * @return
     */
    public static boolean isFragmentDetached(Fragment fragment) {
        return fragment.isDetached()
                || fragment.getActivity() == null
                || !fragment.isAdded();
    }

    /**
     * 判断横竖屏
     *
     * @return
     */
    public static boolean isScreenOrientationVertical(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            return true;
        } else {
            //横屏
            return false;
        }
    }

    /**
     * 判断华为C5
     */
    private static final String PHONE_BRAND = "HUAWEI";//设备品牌
    private static final String PHONE_TYPE_01 = "BZT-W09";//型号
    private static final String PHONE_TYPE_02 = "BZT-AL10";//型号
    private static final String PHONE_TYPE_03 = "TYE100";//型号
    private static final String PHONE_TYPE_04 = "98 4G";//型号
    private static final String PHONE_TYPE_05 = "iXue PAD106";//型号
    private static final String PHONE_TYPE_06 = "AGS2-W09HN";//型号
    private static final String PHONE_TYPE_07 = "AGS2-W09";//型号
    private static final String PHONE_TYPE_08 = "HUAWEI M2-A01W";//型号
    private static final String PHONE_TYPE_09 = "SCM-W09";//型号
    public static final String PHONE_TYPE_10 = "FDR-A01w";//型号

    public static boolean isHuaWeiC5() {
        String deviceBrand = TDevice.getDeviceBrand();
        String phoneType = TDevice.getPhoneType();
        if ((TextUtils.equals(deviceBrand, PHONE_BRAND) && (TextUtils.equals(phoneType, PHONE_TYPE_01)
                || TextUtils.equals(phoneType, PHONE_TYPE_02))) || TextUtils.equals(phoneType, PHONE_TYPE_03)
                || TextUtils.equals(phoneType, PHONE_TYPE_04)
                || TextUtils.equals(phoneType, PHONE_TYPE_05)
                || TextUtils.equals(phoneType, PHONE_TYPE_06)
                || TextUtils.equals(phoneType, PHONE_TYPE_07)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTeacherHuaWeiC5() {
        String phoneType = TDevice.getPhoneType();
        return TextUtils.equals(phoneType, PHONE_TYPE_01)
                || TextUtils.equals(phoneType, PHONE_TYPE_02)
                || TextUtils.equals(phoneType, PHONE_TYPE_08)
                || TextUtils.equals(phoneType, PHONE_TYPE_09);
    }

    /**
     * Dialog的销毁操作
     *
     * @param dialog 对话框
     */
    public static void dismissWithTryCatch(Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
            LogHelper.d(TAG, "Dialog对话框崩溃了，exception->" + e);
        } finally {
            dialog = null;
        }
    }
}