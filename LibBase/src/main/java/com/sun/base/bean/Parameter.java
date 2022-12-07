package com.sun.base.bean;

/**
 * @author Harper
 * @date 2022/7/19
 * note:
 */
public interface Parameter {

    String TYPE = "type";
    String INDEX = "index";
    String FILE_PATH = "filePath";
    String BEAN = "bean";
    String ITEM = "item";
    String NEED_ANIMATION = "needAnimation";
    String STATE_POSITION = "statePosition";
    //requestCode
    int REQUEST_CODE_MEDIA = 0x001;
    int REQUEST_CODE_TAKE_PHOTO = 0x002;
    int REQUEST_CODE_PERMISSION_CAMERA = 0x003;
    //resultCode
    int RESULT_CODE_MEDIA = 0x001;
}
