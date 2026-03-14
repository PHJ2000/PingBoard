package com.pingboard.monitor.metrics;

import com.pingboard.monitor.domain.MonitorStatus;
import com.pingboard.monitor.repository.MonitorRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorMetricsConfiguration {

    @Bean
    MeterBinder monitorMetricsBinder(MonitorRepository monitorRepository) {
        return new MeterBinder() {
            @Override
            public void bindTo(MeterRegistry registry) {
                Gauge.builder("pingboard.monitors.total", monitorRepository, MonitorRepository::count)
                        .description("Total number of configured monitors")
                        .register(registry);

                Gauge.builder("pingboard.monitors.active", monitorRepository, repo -> repo.findAllByActiveTrueOrderByIdAsc().size())
                        .description("Number of active monitors")
                        .register(registry);

                Gauge.builder("pingboard.monitors.up", monitorRepository, repo ->
                                repo.findAll().stream().filter(monitor -> monitor.getStatus() == MonitorStatus.UP).count())
                        .description("Number of monitors currently UP")
                        .register(registry);

                Gauge.builder("pingboard.monitors.down", monitorRepository, repo ->
                                repo.findAll().stream().filter(monitor -> monitor.getStatus() == MonitorStatus.DOWN).count())
                        .description("Number of monitors currently DOWN")
                        .register(registry);
            }
        };
    }
}
