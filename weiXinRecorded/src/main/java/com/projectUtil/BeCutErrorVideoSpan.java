package com.projectUtil;


public class BeCutErrorVideoSpan {
    public Long startTime;
    public Long endTime;
    public ErrorType errorType;
    public boolean isChecked = false;

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
