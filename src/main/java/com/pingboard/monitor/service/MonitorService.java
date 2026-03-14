package com.pingboard.monitor.service;

import com.pingboard.monitor.api.dto.CreateMonitorRequest;
import com.pingboard.monitor.api.dto.DashboardSummaryResponse;
import com.pingboard.monitor.domain.CheckResult;
import com.pingboard.monitor.domain.Monitor;
import com.pingboard.monitor.domain.MonitorStatus;
import com.pingboard.monitor.repository.CheckResultRepository;
import com.pingboard.monitor.repository.MonitorRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final CheckResultRepository checkResultRepository;
    private final HttpPingClient httpPingClient;

    @Transactional
    public Monitor create(CreateMonitorRequest request) {
        Monitor monitor = new Monitor(request.name(), request.url(), request.intervalSeconds());
        return monitorRepository.save(monitor);
    }

    @Transactional(readOnly = true)
    public List<Monitor> findAll() {
        return monitorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        List<Monitor> monitors = monitorRepository.findAll();
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
        PingResult pingResult = httpPingClient.ping(monitor.getUrl());

        monitor.applyCheckResult(
                pingResult.status(),
                pingResult.httpStatus(),
                pingResult.latencyMs(),
                pingResult.errorMessage(),
                Instant.now()
        );

        CheckResult checkResult = new CheckResult(
                monitor,
                pingResult.status(),
                pingResult.httpStatus(),
                pingResult.latencyMs(),
                pingResult.errorMessage()
        );
        log.info("Monitor {} checked with status {}", monitor.getName(), pingResult.status());
        return checkResultRepository.save(checkResult);
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
        return monitor;
    }

    @Transactional
    public void runScheduledChecks() {
        monitorRepository.findAllByActiveTrueOrderByIdAsc()
                .forEach(monitor -> runCheck(monitor.getId()));
    }
}
