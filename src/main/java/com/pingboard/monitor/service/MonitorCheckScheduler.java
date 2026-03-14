package com.pingboard.monitor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorCheckScheduler {

    private final MonitorService monitorService;

    @Scheduled(fixedDelayString = "${pingboard.checker.scheduler-delay:PT30S}")
    public void execute() {
        log.debug("Running scheduled monitor checks");
        monitorService.runScheduledChecks();
    }
}
