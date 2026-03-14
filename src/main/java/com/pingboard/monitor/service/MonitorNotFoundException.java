package com.pingboard.monitor.service;

public class MonitorNotFoundException extends RuntimeException {

    public MonitorNotFoundException(Long monitorId) {
        super("Monitor not found: " + monitorId);
    }
}
