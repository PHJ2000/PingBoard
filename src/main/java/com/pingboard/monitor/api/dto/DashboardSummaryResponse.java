package com.pingboard.monitor.api.dto;

public record DashboardSummaryResponse(
        long totalMonitors,
        long activeMonitors,
        long upMonitors,
        long downMonitors,
        long averageLatencyMs
) {
}
