package com.arquiweb.management.metrics.repository;

import com.arquiweb.management.metrics.domain.EventMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMetricRepository extends JpaRepository<EventMetric, String> {
}