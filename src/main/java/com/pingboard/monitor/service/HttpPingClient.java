package com.pingboard.monitor.service;

public interface HttpPingClient {

    PingResult ping(String url);
}
