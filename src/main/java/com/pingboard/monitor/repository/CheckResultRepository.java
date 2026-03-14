package com.pingboard.monitor.repository;

import com.pingboard.monitor.domain.CheckResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    List<CheckResult> findTop20ByMonitorIdOrderByCheckedAtDesc(Long monitorId);
}
