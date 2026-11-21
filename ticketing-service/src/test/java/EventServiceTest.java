import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.dto.requests.InitEventRequest;
import com.arquiweb.ticketing.event.repository.TicketingEventRepository;
import com.arquiweb.ticketing.event.service.EventService;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.ticketing.infrastructure.messaging.NotificationEventProducer;
import com.arquiweb.ticketing.inventory.dto.requests.SectorCapacityDto;
import com.arquiweb.ticketing.inventory.dto.requests.SectorSeatsDto;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.service.ReservationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class EventServiceTest {

    private static final String EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";

    @Mock
    private InventoryService inventoryService;

    @Mock
    private TicketingEventRepository ticketingEventRepository;

    @Mock
    private ReservationService reservationService;

    @Mock
    private NotificationEventProducer notificationEventProducer;

    private EventService eventService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        eventService = new EventService(
                inventoryService,
                ticketingEventRepository,
                reservationService,
                notificationEventProducer
        );
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testInitEvent_Successful_InitializesSectorsAndSavesEvent() {
        List<SectorCapacityDto> capacities = Collections.emptyList();
        List<SectorSeatsDto> seats = Collections.emptyList();
        InitEventRequest request = new InitEventRequest(EVENT_ID, true, capacities, seats);

        eventService.initEvent(request);

        verify(inventoryService, times(1)).initSector(EVENT_ID, capacities, seats);

        ArgumentCaptor<TicketingEvent> eventCaptor = ArgumentCaptor.forClass(TicketingEvent.class);
        verify(ticketingEventRepository, times(1)).save(eventCaptor.capture());

        TicketingEvent savedEvent = eventCaptor.getValue();
        Assert.assertEquals(savedEvent.getEventId(), EVENT_ID);
        Assert.assertTrue(savedEvent.isSalesEnabled());
        Assert.assertTrue(savedEvent.isHighDemand());
    }

    @Test
    public void testCancelEvent_Successful_DisablesSalesCancelsReservationsAndNotifies() {
        TicketingEvent event = new TicketingEvent(EVENT_ID, true, false);
        when(ticketingEventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        UUID client1 = UUID.randomUUID();
        UUID client2 = UUID.randomUUID();

        Reservation res1 = mock(Reservation.class);
        when(res1.getClientId()).thenReturn(client1);

        Reservation res2 = mock(Reservation.class);
        when(res2.getClientId()).thenReturn(client2);

        when(reservationService.cancelByEvent(EVENT_ID)).thenReturn(List.of(res1, res2));

        eventService.cancelEvent(EVENT_ID);

        Assert.assertFalse(event.isSalesEnabled());
        verify(reservationService, times(1)).cancelByEvent(EVENT_ID);
        verify(notificationEventProducer, times(1))
                .publishEventChanged(List.of(client1, client2), EVENT_ID, "CANCELLED");
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCancelEvent_EventNotFound_ThrowsResourceNotFoundException() {
        when(ticketingEventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        eventService.cancelEvent(EVENT_ID);

        verifyNoInteractions(reservationService, notificationEventProducer);
    }
}
