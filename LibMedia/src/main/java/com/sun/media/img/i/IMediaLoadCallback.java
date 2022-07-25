package com.sun.media.img.i;


import com.sun.media.img.model.bean.MediaFolder;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 图片扫描数据回调接口
 */
public interface IMediaLoadCallback {

    void loadMediaSuccess(List<MediaFolder> mediaFolderList);
}
