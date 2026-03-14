package com.pingboard.monitor.service;

import com.pingboard.alert.config.AlertProperties;
import com.pingboard.alert.service.MonitorAlertService;
import com.pingboard.monitor.api.dto.CreateMonitorRequest;
import com.pingboard.monitor.api.dto.DashboardSummaryResponse;
import com.pingboard.monitor.api.dto.UpdateMonitorRequest;
import com.pingboard.monitor.domain.CheckResult;
import com.pingboard.monitor.domain.Monitor;
import com.pingboard.monitor.domain.MonitorStatus;
import com.pingboard.monitor.repository.CheckResultRepository;
import com.pingboard.monitor.repository.MonitorRepository;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final CheckResultRepository checkResultRepository;
    private final HttpPingClient httpPingClient;
    private final MonitorAlertService monitorAlertService;
    private final AlertProperties alertProperties;

    @Transactional
    public Monitor create(CreateMonitorRequest request) {
        Set<String> tags = sanitizeTags(request.tags());
        Monitor monitor = new Monitor(
                request.name(),
                request.url(),
                request.intervalSeconds(),
                request.environment().trim().toLowerCase(Locale.ROOT),
                tags
        );
        return monitorRepository.save(monitor);
    }

    @Transactional(readOnly = true)
    public List<Monitor> findAll(String environment) {
        if (!StringUtils.hasText(environment)) {
            return monitorRepository.findAll();
        }
        return monitorRepository.findAllByEnvironmentIgnoreCaseOrderByIdAsc(environment.trim());
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(String environment) {
        List<Monitor> monitors = findAll(environment);
        long total = monitors.size();
        long active = monitors.stream().filter(Monitor::isActive).count();
        long down = monitors.stream().filter(monitor -> monitor.getStatus() == MonitorStatus.DOWN).count();
        long up = monitors.stream().filter(monitor -> monitor.getStatus() == MonitorStatus.UP).count();
        double averageLatency = monitors.stream()
                .map(Monitor::getLastLatencyMs)
                .filter(latency -> latency != null)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
        return new DashboardSummaryResponse(total, active, up, down, Math.round(averageLatency));
    }

    @Transactional(readOnly = true)
    public Monitor findById(Long monitorId) {
        return monitorRepository.findById(monitorId)
                .orElseThrow(() -> new MonitorNotFoundException(monitorId));
    }

    @Transactional(readOnly = true)
    public List<CheckResult> findRecentChecks(Long monitorId) {
        findById(monitorId);
        return checkResultRepository.findTop20ByMonitorIdOrderByCheckedAtDesc(monitorId);
    }

    @Transactional
    public CheckResult runCheck(Long monitorId) {
        Monitor monitor = findById(monitorId);
        MonitorStatus previousStatus = monitor.getStatus();
        PingResult pingResult = httpPingClient.ping(monitor.getUrl());
        Instant checkedAt = Instant.now();

        monitor.applyCheckResult(
                pingResult.status(),
                pingResult.httpStatus(),
                pingResult.latencyMs(),
                pingResult.errorMessage(),
                checkedAt
        );

        CheckResult checkResult = new CheckResult(
                monitor,
                pingResult.status(),
                pingResult.httpStatus(),
                pingResult.latencyMs(),
                pingResult.errorMessage()
        );

        if (monitor.shouldNotifyFailure(alertProperties.getFailureThreshold())) {
            monitorAlertService.notifyFailure(monitor);
            monitor.markAlertSent(MonitorStatus.DOWN, checkedAt);
        } else if (monitor.shouldNotifyRecovery(previousStatus)) {
            monitorAlertService.notifyRecovery(monitor);
            monitor.markAlertSent(MonitorStatus.UP, checkedAt);
        }

        log.info("Monitor {} checked with status {}", monitor.getName(), pingResult.status());
        return checkResultRepository.save(checkResult);
    }

    @Transactional
    public Monitor update(Long monitorId, UpdateMonitorRequest request) {
        Monitor monitor = findById(monitorId);
        monitor.updateDetails(
                request.name(),
                request.url(),
                request.intervalSeconds(),
                request.environment().trim().toLowerCase(Locale.ROOT),
                sanitizeTags(request.tags())
        );
        return monitor;
    }

    @Transactional
    public Monitor pause(Long monitorId) {
        Monitor monitor = findById(monitorId);
        monitor.pause();
        return monitor;
    }

    @Transactional
    public Monitor resume(Long monitorId) {
        Monitor monitor = findById(monitorId);
        monitor.resume();
        runCheck(monitorId);
        return monitor;
    }

    @Transactional
    public void runScheduledChecks() {
        monitorRepository.findAllByActiveTrueOrderByIdAsc()
                .forEach(monitor -> runCheck(monitor.getId()));
    }

    private Set<String> sanitizeTags(List<String> tags) {
        return tags == null ? Set.of() : tags.stream()
                .filter(StringUtils::hasText)
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
