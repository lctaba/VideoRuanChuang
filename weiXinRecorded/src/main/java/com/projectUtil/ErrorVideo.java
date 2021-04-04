package com.projectUtil;

import java.io.Serializable;
import java.security.acl.LastOwnerException;

/**
 * @Author cyh
 * @Date 2021/3/20 14:31
 */
public class ErrorVideo implements Serializable {
    public Long startTime;
    public Long endTime;
    public ErrorType errorType;

    public ErrorVideo() {
    }

    public ErrorVideo(Long startTime, Long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ErrorVideo(Long startTime, Long endTime, ErrorType errorType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorType = errorType;
    }
}
