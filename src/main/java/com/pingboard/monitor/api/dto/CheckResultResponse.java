package com.pingboard.monitor.api.dto;

import com.pingboard.monitor.domain.CheckResult;
import java.time.Instant;

public record CheckResultResponse(
        Long id,
        String status,
        Integer httpStatus,
        Long latencyMs,
        String errorMessage,
        Instant checkedAt
) {
    public static CheckResultResponse from(CheckResult checkResult) {
        return new CheckResultResponse(
                checkResult.getId(),
                checkResult.getStatus().name(),
                checkResult.getHttpStatus(),
                checkResult.getLatencyMs(),
                checkResult.getErrorMessage(),
                checkResult.getCheckedAt()
        );
    }
}
