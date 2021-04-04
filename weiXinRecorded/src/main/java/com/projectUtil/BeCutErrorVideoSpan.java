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

    @Override
    public String toString() {
        return startTime + "-" + endTime + ":" + errorType.toString();
    }
}
