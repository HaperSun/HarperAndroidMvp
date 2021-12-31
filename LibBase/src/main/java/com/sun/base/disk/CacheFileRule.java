package com.sun.base.disk;

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.sun.base.util.EncodeUtil;

import java.io.File;

/**
 * @author: Harper
 * @date: 2021/12/31
 * @note: 缓存文件规则类
 */
public class CacheFileRule {
    /**
     * 指定缓存的文件名(也有可能只是后缀)
     */
    private String fileNameOrSuffix;
    /**
     * 指定缓存的文件夹名，为空说明是data_cache根目录，否则为data_cache + fileDirName
     */
    private String fileDirName;
    /**
     * 是否要区分userid（某些文件跟用户相关，需要在缓存时标识出来），如果为true，缓存文件目录会加上一层userId目录
     */
    private boolean isDistinguishUser;
    /**
     * 缓存版本号（为了防止缓存数据结构改变而解析异常），版本号从1开始
     */
    private int version;

    public CacheFileRule(String fileNameOrSuffix) {
        this(fileNameOrSuffix, null);
    }

    public CacheFileRule(String fileNameOrSuffix, String fileDirName) {
        this(fileNameOrSuffix, fileDirName, false);
    }

    public CacheFileRule(String fileNameOrSuffix, String fileDirName, boolean isDistinguishUser) {
        this(fileNameOrSuffix, fileDirName, isDistinguishUser, 1);
    }

    public CacheFileRule(String fileNameOrSuffix, String fileDirName, boolean isDistinguishUser, int version) {
        if (TextUtils.isEmpty(fileNameOrSuffix)) {
            throw new RuntimeException("fileNameOrSuffix cannot be empty!Please specify one");
        }
        if (version <= 0) {//防止外界传version小于等于0的版本号
            throw new RuntimeException("version cannot be less than 1");
        }
        this.fileNameOrSuffix = fileNameOrSuffix;
        this.fileDirName = fileDirName;
        this.isDistinguishUser = isDistinguishUser;
        this.version = version;
    }

    int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "CacheFileRule{" +
                "fileNameOrSuffix='" + fileNameOrSuffix + '\'' +
                ", fileDirName='" + fileDirName + '\'' +
                ", isDistinguishUser=" + isDistinguishUser +
                ", version=" + version +
                '}';
    }

    /**
     * 获取缓存完整路径
     *
     * @param dirCacheRootFile 缓存的根文件
     * @param userId           用户id
     * @param filePrefix       文件名前缀
     * @return
     */
    String getCacheFilePath(File dirCacheRootFile, String userId, String filePrefix) {
        String rootDir = dirCacheRootFile.getAbsolutePath() + File.separator;
        if (!TextUtils.isEmpty(fileDirName)) {//有子文件夹
            rootDir = rootDir + fileDirName + File.separator;
        }
        if (isDistinguishUser) {//要区分用户
            if (TextUtils.isEmpty(userId)) {
                throw new RuntimeException("userId is null!");
            }
            rootDir = rootDir + userId + File.separator;
        }
        String fullFileName = fileNameOrSuffix;
        if (!TextUtils.isEmpty(filePrefix)) {//有前缀
            //如果发现文件前缀是网络链接，需要做encode处理
            if (URLUtil.isNetworkUrl(filePrefix)) {
                filePrefix = EncodeUtil.urlEncode(filePrefix);
            }
            fullFileName = filePrefix + fullFileName;
        }
        return new File(rootDir, fullFileName).getAbsolutePath();
    }
}
