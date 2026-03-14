package com.pingboard.monitor.api.dto;

import com.pingboard.monitor.domain.Monitor;
import java.time.Instant;

public record MonitorResponse(
        Long id,
        String name,
        String url,
        Integer intervalSeconds,
        boolean active,
        String status,
        Integer lastHttpStatus,
        Long lastLatencyMs,
        String lastError,
        Instant lastCheckedAt,
        Instant createdAt
) {
    public static MonitorResponse from(Monitor monitor) {
        return new MonitorResponse(
                monitor.getId(),
                monitor.getName(),
                monitor.getUrl(),
                monitor.getIntervalSeconds(),
                monitor.isActive(),
                monitor.getStatus().name(),
                monitor.getLastHttpStatus(),
                monitor.getLastLatencyMs(),
                monitor.getLastError(),
                monitor.getLastCheckedAt(),
                monitor.getCreatedAt()
        );
    }
}
