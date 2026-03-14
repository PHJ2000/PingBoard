package com.pingboard.monitor.service;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class RestClientHttpPingClient implements HttpPingClient {

    private final MeterRegistry meterRegistry;

    @Value("${pingboard.checker.connect-timeout:PT3S}")
    private Duration connectTimeout;

    @Value("${pingboard.checker.read-timeout:PT5S}")
    private Duration readTimeout;

    @Override
    public PingResult ping(String url) {
        Instant startedAt = Instant.now();
        try {
            RestClient restClient = RestClient.builder()
                    .requestFactory(new SimpleClientHttpRequestFactoryBuilder(connectTimeout, readTimeout).build())
                    .build();
            HttpStatusCode statusCode = restClient.method(HttpMethod.GET)
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();

            long latencyMs = Duration.between(startedAt, Instant.now()).toMillis();
            meterRegistry.counter("pingboard.monitor.checks", "result", "success").increment();
            return statusCode.is2xxSuccessful()
                    ? PingResult.success(statusCode.value(), latencyMs)
                    : PingResult.failure(statusCode.value(), latencyMs, "Received non-success status");
        } catch (RestClientException ex) {
            long latencyMs = Duration.between(startedAt, Instant.now()).toMillis();
            meterRegistry.counter("pingboard.monitor.checks", "result", "failure").increment();
            return PingResult.failure(null, latencyMs, ex.getMessage());
        }
    }
}
