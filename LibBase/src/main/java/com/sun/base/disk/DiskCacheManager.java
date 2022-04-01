package com.sun.base.disk;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.base.util.CipherUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.common.bean.Constant;

import java.io.File;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note: 数据磁盘缓存管理类(缓存统一放置到/sdcard/Android/data/{包名}/files/cache/文件夹下)
 */
public class DiskCacheManager {

    private static final String TAG = DiskCacheManager.class.getName();

    /**
     * 缓存根文件夹
     */
    private final File DIR_CACHE_ROOT;
    /**
     * 缓存的数据类型与文件映射关系
     */
    private static ArrayMap<Class, CacheFileRule> mCacheClassFileRuleMap = new ArrayMap<>();

    private static Context mContext;
    /**
     * 用于获取当前登录用户的userId，这个必要时会做为目录名区分用户
     */
    private static OnGetUserIdListener mOnGetUserIdListener;

    private static volatile DiskCacheManager sInstance = null;

    /**
     * 用于获取当前登录用户的userId回调接口
     */
    public interface OnGetUserIdListener {
        /**
         * 返回当前登录用户的userId
         *
         * @return
         */
        String getUserId();
    }

    private DiskCacheManager(Context context) {
        if (context == null) {
            throw new RuntimeException("Please call init method first before use DiskCacheManager!");
        }
        DIR_CACHE_ROOT = FileUtil.getExternalFileDir(context, Constant.DirName.CACHE);
    }

    /**
     * 初始化，请务必先调用此方法，一般在Application的onCreate方法中调用
     *
     * @param context
     * @param onGetUserIdListener
     */
    public static void init(@NonNull Context context, @NonNull OnGetUserIdListener onGetUserIdListener) {
        mContext = context.getApplicationContext();
        mOnGetUserIdListener = onGetUserIdListener;
    }

    public static DiskCacheManager getInstance() {
        if (sInstance == null) {
            synchronized (DiskCacheManager.class) {
                if (sInstance == null) {
                    sInstance = new DiskCacheManager(mContext);
                }
            }
        }
        return sInstance;
    }

    /**
     * 构建缓存头信息，用于写在缓存文件的头部标识用
     * <p>
     * 大概类似于下面这样
     * <p>
     * com.iflytek.icola.lib_utils.disk_cache.DiskCacheManager
     * 1
     * <p>
     * </p>
     *
     * @param cacheFileRule
     * @return
     */
    private String buildCacheHeaderStr(CacheFileRule cacheFileRule) {
        return DiskCacheManager.class.getName() + "\n"
                + cacheFileRule.getVersion() + "\n\n";
    }

    /**
     * 获取缓存完整路径
     *
     * @param cacheFileRule 缓存文件规则
     * @param filePrefix    文件名前缀
     * @return
     */
    private String getCacheFilePath(CacheFileRule cacheFileRule, String filePrefix) {
        String userId = mOnGetUserIdListener.getUserId();
        if (TextUtils.isEmpty(userId)) {
            // 当前用户ID为空时，默认存到 unknown 目录下
            userId = "unknown";
        }
        return cacheFileRule.getCacheFilePath(DIR_CACHE_ROOT, userId, filePrefix);
    }

    /**
     * 将要缓存的数据类型和需要缓存的文件信息注册(做一个映射)
     *
     * @param clazz         缓存的数据类型，一般是一个javaBean类型
     * @param cacheFileRule 缓存的文件规则
     */
    public static void registerClassCacheFileRule(Class clazz, CacheFileRule cacheFileRule) {
        mCacheClassFileRuleMap.put(clazz, cacheFileRule);
    }

    /**
     * 获取指定缓存数据类型的缓存
     *
     * @param returnType 要获取缓存的数据类型，一般是一个javaBean类型
     * @param <T>
     * @return 之前缓存的数据，以javaBean形式返回
     */
    public <T> T getCacheData(Class<T> returnType) {
        return getCacheData(returnType, null);
    }

    /**
     * 获取指定缓存数据类型的缓存
     *
     * @param returnType 要获取缓存的数据类型，一般是一个javaBean类型
     * @param filePrefix 要获取缓存的文件名前缀
     * @param <T>
     * @return 之前缓存的数据，以javaBean形式返回
     */
    public <T> T getCacheData(Class<T> returnType, String filePrefix) {
        CacheFileRule cacheFileRule = mCacheClassFileRuleMap.get(returnType);
        if (cacheFileRule == null) {
            throw new RuntimeException(returnType + "cacheFileRule is null!Please call registerClassCacheFile() first before use getCacheData!");
        }
        String cacheFilePath = getCacheFilePath(cacheFileRule, filePrefix);
        //需要对本地文件先进行解密
        String encryptSaveDataStr = FileUtil.readStrFromFile(cacheFilePath);
        if (TextUtils.isEmpty(encryptSaveDataStr)) {
            return null;
        }
        String saveDataStr = CipherUtil.desDecrypt(encryptSaveDataStr);
        if (TextUtils.isEmpty(saveDataStr)) {
            return null;
        }
        //获取头部信息，解析出缓存版本号
        String firstLine = DiskCacheManager.class.getName() + "\n";
        if (!saveDataStr.startsWith(firstLine)) {
            FileUtil.delete(cacheFilePath);
            return null;
        }
        int savedVersion = 0;
        try {
            savedVersion = Integer.parseInt(saveDataStr.substring(firstLine.length(), saveDataStr.indexOf("\n\n")));
        } catch (Exception e) {
            LogHelper.e(TAG, "get savedVersion failed " + cacheFileRule, e);
        }
        if (savedVersion != cacheFileRule.getVersion()) {
            //发现缓存版本号不一致，清除缓存，并返回空
            FileUtil.delete(cacheFilePath);
            return null;
        }
        String tDataStr = saveDataStr.substring((firstLine + savedVersion + "\n\n").length());
        if (!TextUtils.isEmpty(tDataStr)) {
            try {
                return new Gson().fromJson(tDataStr, returnType);
            } catch (JsonSyntaxException e) {
                LogHelper.e(TAG, "JsonSyntaxException ", e);
            }
        }
        return null;
    }

    /**
     * 缓存数据到磁盘
     *
     * @param TData 要缓存的数据，一般是一个javaBean
     * @param <T>
     * @return 是否缓存成功
     */
    public <T> boolean saveCacheData(T TData) {
        return saveCacheData(TData, null);
    }

    /**
     * 缓存数据到磁盘
     *
     * @param TData      要缓存的数据，一般是一个javaBean
     * @param filePrefix 要缓存的文件名前缀
     * @param <T>
     * @return 是否缓存成功
     */
    public <T> boolean saveCacheData(T TData, String filePrefix) {
        CacheFileRule cacheFileRule = mCacheClassFileRuleMap.get(TData.getClass());
        if (cacheFileRule == null) {
            throw new RuntimeException(TData.getClass() + "cacheFileRule is null!Please call registerClassCacheFile() first before use saveCacheData!");
        }
        String cacheFilePath = getCacheFilePath(cacheFileRule, filePrefix);
        String tDataStr = new Gson().toJson(TData);
        String cacheHeaderStr = buildCacheHeaderStr(cacheFileRule);
        String saveDataStr = cacheHeaderStr + tDataStr;
        //保存到本地时为了安全起见，做一层加密
        String encryptSaveDataStr = CipherUtil.desEncrypt(saveDataStr);
        return FileUtil.saveStrToFile(encryptSaveDataStr, cacheFilePath, false);
    }

    /**
     * 获取磁盘缓存文件大小
     *
     * @return 文件大小，单位字节
     */
    public long getDiskCacheSize() {
        return FileUtil.getDirSize(DIR_CACHE_ROOT);
    }

    /**
     * 清除所有磁盘缓存文件
     */
    public void clearDiskCache() {
        FileUtil.delete(DIR_CACHE_ROOT);
    }

    /**
     * 清除指定的磁盘缓存文件
     *
     * @param returnType 要清除缓存的数据类型，一般是一个javaBean类型
     * @param <T>
     */
    public <T> void clearDiskCache(Class<T> returnType) {
        clearDiskCache(returnType, null);
    }

    /**
     * 清除指定的磁盘缓存文件
     *
     * @param returnType 要清除缓存的数据类型，一般是一个javaBean类型
     * @param filePrefix 要清除的缓存的文件名前缀
     * @param <T>
     */
    public <T> void clearDiskCache(Class<T> returnType, String filePrefix) {
        CacheFileRule cacheFileRule = mCacheClassFileRuleMap.get(returnType);
        if (cacheFileRule == null) {
            throw new RuntimeException(returnType + "cacheFileRule is null!Please call registerClassCacheFile() first before use clearDiskCache!");
        }
        String cacheFilePath = getCacheFilePath(cacheFileRule, filePrefix);
        FileUtil.delete(cacheFilePath);
    }

}
