package com.pingboard.monitor.metrics;

import com.pingboard.monitor.domain.MonitorStatus;
import com.pingboard.monitor.repository.MonitorRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorMetricsConfiguration {

    public MonitorMetricsConfiguration(MeterRegistry meterRegistry, MonitorRepository monitorRepository) {
        Gauge.builder("pingboard.monitors.total", monitorRepository, repo -> repo.count())
                .description("Total number of configured monitors")
                .register(meterRegistry);

        Gauge.builder("pingboard.monitors.active", monitorRepository, repo -> repo.findAllByActiveTrueOrderByIdAsc().size())
                .description("Number of active monitors")
                .register(meterRegistry);

        Gauge.builder("pingboard.monitors.up", monitorRepository, repo ->
                        repo.findAll().stream().filter(monitor -> monitor.getStatus() == MonitorStatus.UP).count())
                .description("Number of monitors currently UP")
                .register(meterRegistry);

        Gauge.builder("pingboard.monitors.down", monitorRepository, repo ->
                        repo.findAll().stream().filter(monitor -> monitor.getStatus() == MonitorStatus.DOWN).count())
                .description("Number of monitors currently DOWN")
                .register(meterRegistry);
    }
}
