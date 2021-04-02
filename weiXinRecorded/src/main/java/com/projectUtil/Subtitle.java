package com.projectUtil;

/**
 * @Author cyh
 * @Date 2021/3/20 14:16
 */
public class Subtitle {
    //字幕在对应的视频片段中对应的时间（相对时间）
    public Integer startTime;
    public Integer endTime;
    public String subtitle;

    public Subtitle() {
    }

    public Subtitle(Integer startTime, Integer endTime, String subtitle) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subtitle = subtitle;
    }

    public Subtitle(Integer startTime, Integer endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
