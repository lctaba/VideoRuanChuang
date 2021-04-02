package com.projectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author cyh
 * @Date 2021/3/20 14:15
 */
public class VideoClip {
    //视频片段在项目中的开始时间与结束时间（绝对时间）
    public Integer startTime;
    public Integer endTime;
    //视频片段在素材中的开始时间和结束时间（相对时间）
    public Integer relativeStartTime;
    public Integer relativeEndTime;
    //字幕列表
    public List<Subtitle> subtitles;
    //可能出问题的视频片段列表
    public List<ErrorVideo> errorVideos;
    //属于的视频片段
    public String belongTo;

    public VideoClip() {
        subtitles = new ArrayList<>();
        errorVideos = new ArrayList<>();
    }

    public VideoClip(Integer startTime, Integer endTime, Integer relativeStartTime, Integer relativeEndTime, String belongTo) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.relativeStartTime = relativeStartTime;
        this.relativeEndTime = relativeEndTime;
        this.belongTo = belongTo;
        subtitles = new ArrayList<>();
        errorVideos = new ArrayList<>();
    }
}
