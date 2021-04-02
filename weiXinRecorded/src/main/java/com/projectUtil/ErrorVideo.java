package com.projectUtil;

/**
 * @Author cyh
 * @Date 2021/3/20 14:31
 */
public class ErrorVideo {
    public Integer startTime;
    public Integer endTime;
    public ErrorType errorType;

    public ErrorVideo() {
    }

    public ErrorVideo(Integer startTime, Integer endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ErrorVideo(Integer startTime, Integer endTime, ErrorType errorType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorType = errorType;
    }
}
