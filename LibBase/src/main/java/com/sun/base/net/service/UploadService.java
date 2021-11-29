package com.sun.base.net.service;



import com.sun.base.net.response.UploadFileResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note:
 */

public interface UploadService {

    @POST(value = "http://test.static.upload.xingzhijishu.com/api?c=file&m=upFile&dataType=json")
    @Multipart
    Observable<Result<UploadFileResponse>> commonUpload(@Part MultipartBody.Part file);

    @POST
    @Multipart
    Observable<Result<UploadFileResponse>> uploadMultiFile(@Part MultipartBody file, @Url String url, @QueryMap Map<String, String> params);

    @POST(value = "material/upload-file"/*TODO 这里是相对路径*/)
    @Multipart
    Observable<Result<UploadFileResponse>> uploadMaterial(@Query("userid") int userId, @Part MultipartBody.Part file);
}
