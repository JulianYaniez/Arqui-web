package com.arquiweb.management.metrics.service;

import com.arquiweb.management.metrics.repository.EventMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final EventMetricRepository metricRepository;
    private final int minMetricAgeMinutes;

    public MetricsService(
            EventMetricRepository metricRepository,
            @Value("${app.config.metrics.min-metric-age-minutes}") int minMetricAgeMinutes
    ) {
        this.metricRepository = metricRepository;
        this.minMetricAgeMinutes = minMetricAgeMinutes;
    }


}
