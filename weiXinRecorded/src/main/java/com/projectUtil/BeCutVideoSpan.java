package com.projectUtil;

/**
 * @Author cyh
 * @Date 2021/4/4 15:35
 */
public class BeCutVideoSpan {
    public Long startTime;
    public Long endTime;
    public String subtitle;

    @Override
    public String toString() {
        return startTime + "-" + endTime + ":" + subtitle;
    }
}
