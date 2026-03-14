package com.pingboard.monitor.service;

import com.pingboard.monitor.domain.MonitorStatus;

public record PingResult(
        MonitorStatus status,
        Integer httpStatus,
        Long latencyMs,
        String errorMessage
) {
    public static PingResult success(int httpStatus, long latencyMs) {
        return new PingResult(MonitorStatus.UP, httpStatus, latencyMs, null);
    }

    public static PingResult failure(Integer httpStatus, long latencyMs, String errorMessage) {
        return new PingResult(MonitorStatus.DOWN, httpStatus, latencyMs, errorMessage);
    }
}
