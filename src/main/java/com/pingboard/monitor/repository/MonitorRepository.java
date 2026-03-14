package com.pingboard.monitor.repository;

import com.pingboard.monitor.domain.Monitor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    List<Monitor> findAllByActiveTrueOrderByIdAsc();
}
