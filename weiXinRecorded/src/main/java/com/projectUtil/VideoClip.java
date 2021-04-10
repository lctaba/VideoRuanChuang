package com.projectUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class VideoClip implements Serializable {
    //视频片段在项目中的开始时间与结束时间（绝对时间）
    public Long startTime;
    public Long endTime;
    //视频片段在素材中的开始时间和结束时间（相对时间）
    public Long relativeStartTime;
    public Long relativeEndTime;
    //字幕列表
    public List<Subtitle> subtitles;
    //可能出问题的视频片段列表
    public List<ErrorVideo> errorVideos;
    //属于的视频片段
    public Video belongTo;

    public VideoClip() {
        subtitles = new ArrayList<>();
        errorVideos = new ArrayList<>();
    }

    public VideoClip(Long startTime, Long endTime, Long relativeStartTime, Long relativeEndTime, Video belongTo) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.relativeStartTime = relativeStartTime;
        this.relativeEndTime = relativeEndTime;
        this.belongTo = belongTo;
        subtitles = new ArrayList<>();
        errorVideos = new ArrayList<>();
    }
}
