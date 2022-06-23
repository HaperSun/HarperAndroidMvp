package com.sun.video.model.entity;

import com.sun.video.model.VideoModel;

import java.util.ArrayList;
import java.util.List;

public class VideoListModel {
    public List<VideoModel> videoModelList = new ArrayList<>();
    public String title;
    public String icon;

    public void addVideoModel(VideoModel videoModel) {
        videoModelList.add(videoModel);
    }
}
