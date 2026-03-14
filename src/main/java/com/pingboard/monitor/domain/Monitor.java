package com.pingboard.monitor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "monitors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Integer intervalSeconds;

    @Column(nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MonitorStatus status;

    private Integer lastHttpStatus;

    private Long lastLatencyMs;

    @Column(length = 500)
    private String lastError;

    private Instant lastCheckedAt;

    private Instant lastSuccessCheckedAt;

    @Column(nullable = false)
    private int consecutiveFailures;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MonitorStatus lastNotifiedStatus;

    private Instant lastNotifiedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Monitor(String name, String url, Integer intervalSeconds) {
        this.name = name;
        this.url = url;
        this.intervalSeconds = intervalSeconds;
        this.active = true;
        this.status = MonitorStatus.UNKNOWN;
    }

    public void applyCheckResult(MonitorStatus status, Integer httpStatus, Long latencyMs, String error, Instant checkedAt) {
        this.status = status;
        this.lastHttpStatus = httpStatus;
        this.lastLatencyMs = latencyMs;
        this.lastError = error;
        this.lastCheckedAt = checkedAt;
        if (status == MonitorStatus.UP) {
            this.lastSuccessCheckedAt = checkedAt;
            this.consecutiveFailures = 0;
            return;
        }
        this.consecutiveFailures += 1;
    }

    public void pause() {
        this.active = false;
    }

    public void resume() {
        this.active = true;
    }

    public boolean shouldNotifyFailure(int failureThreshold) {
        return status == MonitorStatus.DOWN
                && consecutiveFailures >= failureThreshold
                && lastNotifiedStatus != MonitorStatus.DOWN;
    }

    public boolean shouldNotifyRecovery(MonitorStatus previousStatus) {
        return status == MonitorStatus.UP
                && previousStatus == MonitorStatus.DOWN
                && lastNotifiedStatus == MonitorStatus.DOWN;
    }

    public void markAlertSent(MonitorStatus notifiedStatus, Instant notifiedAt) {
        this.lastNotifiedStatus = notifiedStatus;
        this.lastNotifiedAt = notifiedAt;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
