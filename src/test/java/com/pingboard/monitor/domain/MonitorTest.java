package com.pingboard.monitor.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class MonitorTest {

    @Test
    void resetsConsecutiveFailuresAfterSuccess() {
        Monitor monitor = new Monitor("Example", "https://example.com", 60);
        Instant now = Instant.parse("2026-03-14T01:00:00Z");

        monitor.applyCheckResult(MonitorStatus.DOWN, 500, 120L, "boom", now);
        monitor.applyCheckResult(MonitorStatus.DOWN, 500, 130L, "boom again", now.plusSeconds(60));
        monitor.applyCheckResult(MonitorStatus.UP, 200, 50L, null, now.plusSeconds(120));

        assertThat(monitor.getConsecutiveFailures()).isZero();
        assertThat(monitor.getLastSuccessCheckedAt()).isEqualTo(now.plusSeconds(120));
        assertThat(monitor.getStatus()).isEqualTo(MonitorStatus.UP);
    }
}
