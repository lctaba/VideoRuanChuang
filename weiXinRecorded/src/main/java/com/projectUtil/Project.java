package com.projectUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Project implements Serializable {
    //项目中包含的素材路径
    public List<Video> videos;
    //项目中包含的视频片段
    public List<VideoClip> videoClips;
    //项目的名称
    public String name;

    public Project(Project project) {
        this.name = project.name;
        this.videos = new ArrayList<>(project.videos);
        this.videoClips = new ArrayList<>(project.videoClips);
    }

    public Project(String name) {
        this.name = name;
        videoClips = new ArrayList<>();
        videos = new ArrayList<>();
    }

    public Project() {
        videoClips = new ArrayList<>();
        videos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }
}
