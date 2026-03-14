package com.pingboard.alert.service;

import com.pingboard.alert.config.AlertProperties;
import com.pingboard.alert.config.AlertProvider;
import com.pingboard.monitor.domain.Monitor;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookMonitorAlertService implements MonitorAlertService {

    private final AlertProperties alertProperties;

    @Override
    public void notifyFailure(Monitor monitor) {
        send(formatFailureMessage(monitor));
    }

    @Override
    public void notifyRecovery(Monitor monitor) {
        send(formatRecoveryMessage(monitor));
    }

    private void send(String message) {
        if (!alertProperties.isEnabled() || !StringUtils.hasText(alertProperties.getWebhookUrl())) {
            return;
        }

        try {
            RestClient.create()
                    .post()
                    .uri(alertProperties.getWebhookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload(message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("Failed to send webhook alert: {}", ex.getMessage());
        }
    }

    private Map<String, Object> payload(String message) {
        if (alertProperties.getProvider() == AlertProvider.SLACK) {
            return Map.of("text", message);
        }
        return Map.of("content", message);
    }

    private String formatFailureMessage(Monitor monitor) {
        return """
                PingBoard alert: %s is DOWN
                URL: %s
                Consecutive failures: %d
                Last status code: %s
                Last error: %s
                Dashboard: %s
                """.formatted(
                monitor.getName(),
                monitor.getUrl(),
                monitor.getConsecutiveFailures(),
                monitor.getLastHttpStatus() == null ? "-" : monitor.getLastHttpStatus(),
                monitor.getLastError() == null ? "-" : monitor.getLastError(),
                alertProperties.getAppBaseUrl()
        ).trim();
    }

    private String formatRecoveryMessage(Monitor monitor) {
        return """
                PingBoard recovery: %s is UP again
                URL: %s
                Latest latency: %sms
                Last checked: %s
                Dashboard: %s
                """.formatted(
                monitor.getName(),
                monitor.getUrl(),
                monitor.getLastLatencyMs() == null ? "-" : monitor.getLastLatencyMs(),
                monitor.getLastCheckedAt(),
                alertProperties.getAppBaseUrl()
        ).trim();
    }
}
