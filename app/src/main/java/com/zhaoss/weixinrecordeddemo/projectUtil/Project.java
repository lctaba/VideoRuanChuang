package com.zhaoss.weixinrecordeddemo.projectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author cyh
 * @Date 2021/3/20 14:15
 */

public class Project {
    //项目中包含的素材名称
    public List<String> videos;
    //项目中包含的视频片段
    public List<VideoClip> videoClips;
    //项目的名称
    public String name;

    public Project(String name) {
        this.name = name;
        videoClips = new ArrayList<>();
        videos = new ArrayList<>();
    }

    public Project() {
        videoClips = new ArrayList<>();
        videos = new ArrayList<>();
    }
}
