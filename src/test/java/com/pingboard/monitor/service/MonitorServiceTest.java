package com.pingboard.monitor.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.pingboard.alert.config.AlertProperties;
import com.pingboard.alert.service.MonitorAlertService;
import com.pingboard.monitor.api.dto.CreateMonitorRequest;
import com.pingboard.monitor.api.dto.UpdateMonitorRequest;
import com.pingboard.monitor.domain.CheckResult;
import com.pingboard.monitor.domain.Monitor;
import com.pingboard.monitor.domain.MonitorStatus;
import com.pingboard.monitor.repository.CheckResultRepository;
import com.pingboard.monitor.repository.MonitorRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class MonitorServiceTest {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private CheckResultRepository checkResultRepository;

    @Autowired
    private RecordingAlertService recordingAlertService;

    @Autowired
    private StubHttpPingClient stubHttpPingClient;

    @Test
    void createsMonitorAndPersistsManualCheck() {
        Monitor monitor = monitorService.create(new CreateMonitorRequest("OpenAI", "https://example.com", 60, "prod", List.of("public", "critical")));

        CheckResult checkResult = monitorService.runCheck(monitor.getId());

        List<CheckResult> recentChecks = monitorService.findRecentChecks(monitor.getId());
        Monitor persistedMonitor = monitorRepository.findById(monitor.getId()).orElseThrow();

        assertThat(checkResult.getStatus()).isEqualTo(MonitorStatus.UP);
        assertThat(recentChecks).hasSize(1);
        assertThat(checkResultRepository.count()).isEqualTo(1);
        assertThat(persistedMonitor.getStatus()).isEqualTo(MonitorStatus.UP);
        assertThat(persistedMonitor.getLastHttpStatus()).isEqualTo(200);
        assertThat(persistedMonitor.getEnvironment()).isEqualTo("prod");
        assertThat(persistedMonitor.getTags()).containsExactlyInAnyOrder("public", "critical");
    }

    @Test
    void pausesAndResumesMonitor() {
        Monitor monitor = monitorService.create(new CreateMonitorRequest("PingBoard", "https://example.com", 60, "staging", List.of("ops")));

        Monitor paused = monitorService.pause(monitor.getId());
        Monitor resumed = monitorService.resume(monitor.getId());

        assertThat(paused.isActive()).isFalse();
        assertThat(resumed.isActive()).isTrue();
        assertThat(resumed.getLastCheckedAt()).isNotNull();
    }

    @Test
    void sendsFailureAndRecoveryAlertOncePerIncident() {
        Monitor monitor = monitorService.create(new CreateMonitorRequest("Alerted", "https://example.com", 60, "prod", List.of("critical")));

        stubHttpPingClient.nextResult = PingResult.failure(500, 80L, "upstream down");
        monitorService.runCheck(monitor.getId());
        monitorService.runCheck(monitor.getId());
        monitorService.runCheck(monitor.getId());

        stubHttpPingClient.nextResult = PingResult.success(200, 40L);
        monitorService.runCheck(monitor.getId());

        assertThat(recordingAlertService.failureCount).isEqualTo(1);
        assertThat(recordingAlertService.recoveryCount).isEqualTo(1);
    }

    @Test
    void filtersMonitorsByEnvironment() {
        monitorService.create(new CreateMonitorRequest("Prod API", "https://example.com/prod", 60, "prod", List.of("api")));
        monitorService.create(new CreateMonitorRequest("Dev API", "https://example.com/dev", 60, "dev", List.of("api")));

        List<Monitor> prodMonitors = monitorService.findAll("prod");

        assertThat(prodMonitors).isNotEmpty();
        assertThat(prodMonitors).allMatch(monitor -> monitor.getEnvironment().equals("prod"));
        assertThat(prodMonitors.stream().map(Monitor::getName)).contains("Prod API");
    }

    @Test
    void updatesMonitorDetailsWithoutRecreatingIt() {
        Monitor monitor = monitorService.create(new CreateMonitorRequest("Old API", "https://example.com/old", 60, "dev", List.of("legacy")));

        Monitor updated = monitorService.update(
                monitor.getId(),
                new UpdateMonitorRequest("New API", "https://example.com/new", 120, "prod", List.of("critical", "public"))
        );

        assertThat(updated.getId()).isEqualTo(monitor.getId());
        assertThat(updated.getName()).isEqualTo("New API");
        assertThat(updated.getUrl()).isEqualTo("https://example.com/new");
        assertThat(updated.getIntervalSeconds()).isEqualTo(120);
        assertThat(updated.getEnvironment()).isEqualTo("prod");
        assertThat(updated.getTags()).containsExactlyInAnyOrder("critical", "public");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        StubHttpPingClient httpPingClient() {
            return new StubHttpPingClient();
        }

        @Bean
        AlertProperties alertProperties() {
            AlertProperties properties = new AlertProperties();
            properties.setEnabled(true);
            properties.setFailureThreshold(3);
            properties.setAppBaseUrl("http://localhost:8080");
            return properties;
        }

        @Bean
        RecordingAlertService monitorAlertService() {
            return new RecordingAlertService();
        }
    }

    static class StubHttpPingClient implements HttpPingClient {
        private PingResult nextResult = PingResult.success(200, 42L);

        @Override
        public PingResult ping(String url) {
            return nextResult;
        }
    }

    static class RecordingAlertService implements MonitorAlertService {
        private int failureCount;
        private int recoveryCount;

        @Override
        public void notifyFailure(Monitor monitor) {
            failureCount++;
        }

        @Override
        public void notifyRecovery(Monitor monitor) {
            recoveryCount++;
        }
    }
}
