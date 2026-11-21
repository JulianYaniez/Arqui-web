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
import com.arquiweb.management.metrics.service.MetricsService;
import feign.FeignException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class MetricsServiceTest {

    private static final int MIN_AGE_MINUTES = 15;
    private static final String EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";
    private static final UUID ORGANIZER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_ORGANIZER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private EventMetricRepository metricRepository;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private TicketingClient ticketingClient;

    private MetricsService metricsService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(metricRepository, catalogClient, ticketingClient);
        metricsService = new MetricsService(
                metricRepository,
                MIN_AGE_MINUTES,
                catalogClient,
                ticketingClient
        );
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testGetEventMetrics_CacheHitFresh_ReturnsCachedWithoutFetching() {
        EventMetric cached = mock(EventMetric.class);
        when(cached.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(cached.getUpdatedAt()).thenReturn(LocalDateTime.now().minusMinutes(5)); // < 15 min -> fresh
        when(cached.getEventId()).thenReturn(EVENT_ID);
        when(cached.getEventName()).thenReturn("Recital Rock");
        when(cached.getSectorMetrics()).thenReturn(Collections.emptyList());

        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.of(cached));

        EventMetricsResponse response = metricsService.getEventMetrics(EVENT_ID, ORGANIZER_ID);

        Assert.assertNotNull(response);
        verify(cached, never()).updateMetrics(anyList());
        verify(metricRepository, never()).save(any());
        verifyNoInteractions(catalogClient, ticketingClient);
    }

    @Test
    public void testGetEventMetrics_CacheHitStale_RefreshesSavesAndReturns() {
        EventMetric cached = mock(EventMetric.class);
        when(cached.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(cached.getUpdatedAt()).thenReturn(LocalDateTime.now().minusMinutes(30)); // > 15 min -> stale
        when(cached.getEventId()).thenReturn(EVENT_ID);
        when(cached.getEventName()).thenReturn("Recital Rock");
        when(cached.getSectorMetrics()).thenReturn(Collections.emptyList());

        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.of(cached));

        SectorMetricsResponse sectorDto = mock(SectorMetricsResponse.class);
        SectorMetrics domainSector = mock(SectorMetrics.class);
        when(sectorDto.toDomain()).thenReturn(domainSector);
        when(ticketingClient.getSectorsMetrics(EVENT_ID)).thenReturn(List.of(sectorDto));

        EventMetricsResponse response = metricsService.getEventMetrics(EVENT_ID, ORGANIZER_ID);

        Assert.assertNotNull(response);
        verify(ticketingClient, times(1)).getSectorsMetrics(EVENT_ID);
        verify(cached, times(1)).updateMetrics(List.of(domainSector));
        verify(metricRepository, times(1)).save(cached);
        verifyNoInteractions(catalogClient);
    }

    @Test(expectedExceptions = ForbiddenException.class)
    public void testGetEventMetrics_CacheHitWrongOrganizer_ThrowsForbiddenException() {
        EventMetric cached = mock(EventMetric.class);
        when(cached.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.of(cached));

        metricsService.getEventMetrics(EVENT_ID, OTHER_ORGANIZER_ID);
    }

    @Test(expectedExceptions = ForbiddenException.class)
    public void testGetEventMetrics_CacheMissWrongOrganizer_ThrowsForbiddenException() {
        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        EventStateResponse event = mock(EventStateResponse.class);
        when(event.organizerId()).thenReturn(ORGANIZER_ID);
        when(catalogClient.getEventById(EVENT_ID)).thenReturn(event);

        metricsService.getEventMetrics(EVENT_ID, OTHER_ORGANIZER_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetEventMetrics_CatalogNotFound_ThrowsResourceNotFoundException() {
        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.empty());
        when(catalogClient.getEventById(EVENT_ID)).thenThrow(mock(FeignException.NotFound.class));

        metricsService.getEventMetrics(EVENT_ID, ORGANIZER_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testGetEventMetrics_CatalogFeignError_ThrowsServiceUnavailableException() {
        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.empty());
        when(catalogClient.getEventById(EVENT_ID)).thenThrow(mock(FeignException.class));

        metricsService.getEventMetrics(EVENT_ID, ORGANIZER_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetEventMetrics_TicketingNotFound_ThrowsResourceNotFoundException() {
        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        EventStateResponse event = mock(EventStateResponse.class);
        when(event.organizerId()).thenReturn(ORGANIZER_ID);
        when(catalogClient.getEventById(EVENT_ID)).thenReturn(event);

        when(ticketingClient.getSectorsMetrics(EVENT_ID)).thenThrow(mock(FeignException.NotFound.class));

        metricsService.getEventMetrics(EVENT_ID, ORGANIZER_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testGetEventMetrics_TicketingFeignError_ThrowsServiceUnavailableException() {
        when(metricRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        EventStateResponse event = mock(EventStateResponse.class);
        when(event.organizerId()).thenReturn(ORGANIZER_ID);
        when(catalogClient.getEventById(EVENT_ID)).thenReturn(event);

        when(ticketingClient.getSectorsMetrics(EVENT_ID)).thenThrow(mock(FeignException.class));

        metricsService.getEventMetrics(EVENT_ID, ORGANIZER_ID);
    }
}
