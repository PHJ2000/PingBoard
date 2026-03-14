package com.pingboard.monitor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "check_results")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitor_id")
    private Monitor monitor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MonitorStatus status;

    private Integer httpStatus;

    private Long latencyMs;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private Instant checkedAt;

    public CheckResult(Monitor monitor, MonitorStatus status, Integer httpStatus, Long latencyMs, String errorMessage) {
        this.monitor = monitor;
        this.status = status;
        this.httpStatus = httpStatus;
        this.latencyMs = latencyMs;
        this.errorMessage = errorMessage;
    }

    @PrePersist
    void onCreate() {
        this.checkedAt = Instant.now();
    }
}
