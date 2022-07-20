package com.sun.img.i;


import com.sun.img.model.bean.MediaFolder;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 图片扫描数据回调接口
 */
public interface MediaLoadCallback {

    void loadMediaSuccess(List<MediaFolder> mediaFolderList);
}
