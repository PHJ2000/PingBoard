package com.pingboard.alert.service;

import com.pingboard.monitor.domain.Monitor;

public interface MonitorAlertService {

    void notifyFailure(Monitor monitor);

    void notifyRecovery(Monitor monitor);
}
