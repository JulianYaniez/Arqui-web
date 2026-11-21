package com.arquiweb.management.metrics.service;

import com.arquiweb.management.infrastructure.client.CatalogClient;
import com.arquiweb.management.infrastructure.client.TicketingClient;
import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.requests.SectorMetricsResponse;
import com.arquiweb.management.infrastructure.exception.ForbiddenException;
import com.arquiweb.management.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.management.infrastructure.exception.ServiceUnavailableException;
import com.arquiweb.management.metrics.domain.EventMetric;
import com.arquiweb.management.metrics.domain.jsonb.SectorMetrics;
import com.arquiweb.management.metrics.dto.responses.EventMetricsResponse;
import com.arquiweb.management.metrics.repository.EventMetricRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MetricsService {

    private final EventMetricRepository metricRepository;
    private final int minMetricAgeMinutes;
    private final CatalogClient catalogClient;
    private final TicketingClient ticketingClient;

    public MetricsService(
            EventMetricRepository metricRepository,
            @Value("${app.config.metrics.min-metric-age-minutes}") int minMetricAgeMinutes,
            CatalogClient catalogClient, TicketingClient ticketingClient) {
        this.metricRepository = metricRepository;
        this.minMetricAgeMinutes = minMetricAgeMinutes;
        this.catalogClient = catalogClient;
        this.ticketingClient = ticketingClient;
    }

    @Transactional
    public EventMetricsResponse getEventMetrics(String eventId, UUID organizerId) {
        EventMetric cached = metricRepository.findById(eventId).orElse(null);

        if (cached != null) {
            validateOwnership(cached.getOrganizerId(), organizerId);

            boolean isFresh = cached.getUpdatedAt()
                    .isAfter(
                            LocalDateTime.now().minus(
                                    Duration.ofMinutes(this.minMetricAgeMinutes)
                            )
                    );

            if (!isFresh) {
                List<SectorMetrics> newMetrics = fetchMetrics(cached.getEventId()).stream()
                        .map(SectorMetricsResponse::toDomain)
                        .toList();
                cached.updateMetrics(newMetrics);
                metricRepository.save(cached);
            }
            return EventMetricsResponse.fromDomain(cached);
        }

        EventStateResponse event = fetchEvent(eventId);
        validateOwnership(event.organizerId(), organizerId);

        List<SectorMetrics> metrics = fetchMetrics(eventId).stream()
                .map(SectorMetricsResponse::toDomain)
                .toList();

        EventMetric eventMetric = new EventMetric(eventId, event.name(), organizerId, metrics);
        metricRepository.save(eventMetric);

        return EventMetricsResponse.fromDomain(eventMetric);
    }

    private EventStateResponse fetchEvent(String eventId) {
        try {
            return catalogClient.getEventById(eventId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        } catch (FeignException ex) {
            throw new ServiceUnavailableException("Catalog service unavailable.");
        }
    }

    private List<SectorMetricsResponse> fetchMetrics(String eventId) {
        try {
            return ticketingClient.getSectorsMetrics(eventId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Ticketing data not found for event: " + eventId);
        } catch (FeignException ex) {
            throw new ServiceUnavailableException("Ticketing service unavailable.");
        }
    }

    private void validateOwnership(UUID organizerId, UUID inputId) {
        if (!organizerId.equals(inputId)) throw new ForbiddenException("Event does not belong to current user.");
    }
}
