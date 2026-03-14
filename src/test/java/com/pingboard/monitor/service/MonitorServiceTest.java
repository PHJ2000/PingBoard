package com.pingboard.monitor.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.pingboard.monitor.api.dto.CreateMonitorRequest;
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

    @Test
    void createsMonitorAndPersistsManualCheck() {
        Monitor monitor = monitorService.create(new CreateMonitorRequest("OpenAI", "https://example.com", 60));

        CheckResult checkResult = monitorService.runCheck(monitor.getId());

        List<CheckResult> recentChecks = monitorService.findRecentChecks(monitor.getId());
        Monitor persistedMonitor = monitorRepository.findById(monitor.getId()).orElseThrow();

        assertThat(checkResult.getStatus()).isEqualTo(MonitorStatus.UP);
        assertThat(recentChecks).hasSize(1);
        assertThat(checkResultRepository.count()).isEqualTo(1);
        assertThat(persistedMonitor.getStatus()).isEqualTo(MonitorStatus.UP);
        assertThat(persistedMonitor.getLastHttpStatus()).isEqualTo(200);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        HttpPingClient httpPingClient() {
            return url -> PingResult.success(200, 42L);
        }
    }
}
