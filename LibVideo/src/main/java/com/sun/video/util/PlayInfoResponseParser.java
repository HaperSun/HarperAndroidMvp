package com.sun.video.util;


import com.sun.video.model.entity.PlayInfoStream;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note:
 */
public class PlayInfoResponseParser {

    protected JSONObject response;

    public PlayInfoResponseParser(JSONObject response) {
        this.response = response;
    }

    /**
     * 获取封面图片
     *
     * @return 图片url
     */
    public String coverURL() {
        try {
            JSONObject coverInfo = response.getJSONObject("coverInfo");
            if (coverInfo != null) {
                return coverInfo.getString("coverUrl");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayInfoStream getSource() {
        try {
            JSONObject sourceVideo = response.getJSONObject("videoInfo").getJSONObject("sourceVideo");
            PlayInfoStream stream = new PlayInfoStream();
            stream.url = sourceVideo.getString("url");
            stream.duration = sourceVideo.getInt("duration");
            stream.width = sourceVideo.getInt("width");
            stream.height = sourceVideo.getInt("height");
            stream.size = sourceVideo.getInt("size");
            stream.bitrate = sourceVideo.getInt("bitrate");
            return stream;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取视频名称
     *
     * @return
     */
    public String name() {
        try {
            JSONObject basicInfo = response.getJSONObject("videoInfo").getJSONObject("basicInfo");
            if (basicInfo != null) {
                return basicInfo.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取视频描述
     *
     * @return
     */
    public String description() {
        try {
            JSONObject basicInfo = response.getJSONObject("videoInfo").getJSONObject("basicInfo");
            if (basicInfo != null) {
                return basicInfo.getString("description");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
