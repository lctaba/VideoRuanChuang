package com.projectUtil;

/**
 * @Author cyh
 * @Date 2021/4/4 19:57
 */
public class BeCutErrorVideoSpan {
    public Long startTime;
    public Long endTime;
    public ErrorType errorType;

    public BeCutErrorVideoSpan() {
    }

    public BeCutErrorVideoSpan(Long startTime, Long endTime, ErrorType errorType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorType = errorType;
    }

    private String convertToTime(Long l){
        return l/3600000 + ":" + (l/60000)%60 + ":" + (l/1000)%60;
    }

    @Override
    public String toString() {
        return convertToTime(startTime) + "-" + convertToTime(endTime) + ":" + errorType.toString();
    }
}
