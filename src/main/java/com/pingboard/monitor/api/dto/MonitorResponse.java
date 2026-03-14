package com.pingboard.monitor.api.dto;

import com.pingboard.monitor.domain.Monitor;
import java.time.Instant;
import java.util.Set;

public record MonitorResponse(
        Long id,
        String name,
        String url,
        Integer intervalSeconds,
        String environment,
        Set<String> tags,
        boolean active,
        String status,
        Integer lastHttpStatus,
        Long lastLatencyMs,
        String lastError,
        Instant lastCheckedAt,
        Instant lastSuccessCheckedAt,
        int consecutiveFailures,
        Instant createdAt
) {
    public static MonitorResponse from(Monitor monitor) {
        return new MonitorResponse(
                monitor.getId(),
                monitor.getName(),
                monitor.getUrl(),
                monitor.getIntervalSeconds(),
                monitor.getEnvironment(),
                monitor.getTags(),
                monitor.isActive(),
                monitor.getStatus().name(),
                monitor.getLastHttpStatus(),
                monitor.getLastLatencyMs(),
                monitor.getLastError(),
                monitor.getLastCheckedAt(),
                monitor.getLastSuccessCheckedAt(),
                monitor.getConsecutiveFailures(),
                monitor.getCreatedAt()
        );
    }
}
