package com.pingboard.alert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pingboard.alerts")
public class AlertProperties {

    private boolean enabled = false;
    private AlertProvider provider = AlertProvider.DISCORD;
    private String webhookUrl;
    private int failureThreshold = 3;
    private String appBaseUrl = "http://localhost:8080";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AlertProvider getProvider() {
        return provider;
    }

    public void setProvider(AlertProvider provider) {
        this.provider = provider;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public String getAppBaseUrl() {
        return appBaseUrl;
    }

    public void setAppBaseUrl(String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }
}
