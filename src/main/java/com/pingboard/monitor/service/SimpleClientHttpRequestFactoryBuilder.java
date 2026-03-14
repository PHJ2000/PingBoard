package com.pingboard.monitor.service;

import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class SimpleClientHttpRequestFactoryBuilder {

    private final Duration connectTimeout;
    private final Duration readTimeout;

    public SimpleClientHttpRequestFactoryBuilder(Duration connectTimeout, Duration readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public SimpleClientHttpRequestFactory build() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
