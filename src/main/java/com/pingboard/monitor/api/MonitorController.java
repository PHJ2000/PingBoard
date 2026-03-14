package com.pingboard.monitor.api;

import com.pingboard.monitor.api.dto.CheckResultResponse;
import com.pingboard.monitor.api.dto.CreateMonitorRequest;
import com.pingboard.monitor.api.dto.DashboardSummaryResponse;
import com.pingboard.monitor.api.dto.MonitorResponse;
import com.pingboard.monitor.service.MonitorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitors")
public class MonitorController {

    private final MonitorService monitorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonitorResponse create(@Valid @RequestBody CreateMonitorRequest request) {
        return MonitorResponse.from(monitorService.create(request));
    }

    @GetMapping
    public List<MonitorResponse> findAll(@RequestParam(required = false) String environment) {
        return monitorService.findAll(environment).stream()
                .map(MonitorResponse::from)
                .toList();
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(@RequestParam(required = false) String environment) {
        return monitorService.getSummary(environment);
    }

    @GetMapping("/{monitorId}")
    public MonitorResponse findById(@PathVariable Long monitorId) {
        return MonitorResponse.from(monitorService.findById(monitorId));
    }

    @GetMapping("/{monitorId}/checks")
    public List<CheckResultResponse> findRecentChecks(@PathVariable Long monitorId) {
        return monitorService.findRecentChecks(monitorId).stream()
                .map(CheckResultResponse::from)
                .toList();
    }

    @PostMapping("/{monitorId}/checks")
    public CheckResultResponse runCheck(@PathVariable Long monitorId) {
        return CheckResultResponse.from(monitorService.runCheck(monitorId));
    }

    @PostMapping("/{monitorId}/pause")
    public MonitorResponse pause(@PathVariable Long monitorId) {
        return MonitorResponse.from(monitorService.pause(monitorId));
    }

    @PostMapping("/{monitorId}/resume")
    public MonitorResponse resume(@PathVariable Long monitorId) {
        return MonitorResponse.from(monitorService.resume(monitorId));
    }
}
