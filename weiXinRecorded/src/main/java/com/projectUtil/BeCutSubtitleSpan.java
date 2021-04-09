package com.projectUtil;

/**
 * @Author cyh
 * @Date 2021/4/4 15:35
 */
public class BeCutSubtitleSpan {
    public Long startTime;
    public Long endTime;
    public String subtitle;

    private String convertToTime(Long l){
        return l/3600000 + ":" + (l/60000)%60 + ":" + (l/1000)%60;
    }

    @Override
    public String toString() {
        return convertToTime(startTime) + "-" + convertToTime(endTime) + ":" + subtitle;
    }
}
