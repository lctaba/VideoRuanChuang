package com.projectUtil;

import java.io.Serializable;


public class Subtitle implements Serializable {
    //字幕在对应的视频片段中对应的时间（相对时间）
    public Long startTime;
    public Long endTime;
    public String subtitle;

    public Subtitle() {
    }

    public Subtitle(Long startTime, Long endTime, String subtitle) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subtitle = subtitle;
    }

    public Subtitle(Long startTime, Long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
