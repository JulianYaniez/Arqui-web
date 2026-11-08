import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.events.domain.enums.EventType;
import com.arquiweb.catalog.events.domain.exceptions.IllegalEventStateException;
import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.dto.request.EventCriteriaQuery;
import com.arquiweb.catalog.events.dto.response.EventResponse;
import com.arquiweb.catalog.events.dto.response.EventStateResponse;
import com.arquiweb.catalog.events.repository.EventCustomRepository;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.events.service.EventService;
import com.arquiweb.catalog.infrastructure.client.TicketingClient;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.catalog.venues.domain.Sector;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.service.VenueService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class EventServiceTest {

    private static final String VALID_EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";
    private static final String VALID_VENUE_ID = "64b1f2c3d4e5f6a7b8c9d0e2";
    private static final String NON_EXISTENT_ID = "64b1f2c3d4e5f6a7b8c9d000";

    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventCustomRepository eventCustomRepository;

    @Mock
    private TicketingClient ticketingClient;

    @Mock
    private VenueService venueService;

    private AutoCloseable closeable;

    @BeforeMethod
    void setup() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        this.eventService = new EventService(
                                eventRepository,
                                eventCustomRepository,
                                venueService,
                                ticketingClient
                            );
    }

    @AfterMethod
    void tearDown() throws Exception {
        if(closeable != null)
            closeable.close();
    }

    @Test
    public void testCreateEvent_Successful() {
        String sectorId = "sec-01";

        CreateEventSectorRequest sectorReq = mock(CreateEventSectorRequest.class);
        when(sectorReq.sectorId()).thenReturn(sectorId);
        when(sectorReq.price()).thenReturn(BigDecimal.valueOf(15000));

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(VALID_VENUE_ID);
        when(request.sectors()).thenReturn(List.of(sectorReq));

        SectorResponse sectorResp = mock(SectorResponse.class);
        when(sectorResp.id()).thenReturn(sectorId);

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorResp));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResp);

        Event mockSavedEvent = mock(Event.class);
        when(mockSavedEvent.getId()).thenReturn("event-gen-999");
        when(request.toDomain(eq(venueResp), anyList())).thenReturn(mockSavedEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(mockSavedEvent);

        String idGenerado = eventService.createEvent(request);

        Assert.assertNotNull(idGenerado);
        Assert.assertEquals(idGenerado, "event-gen-999");
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testCreateEvent_DuplicatedSector() {
        String sectorId = "sec-01";

        CreateEventSectorRequest sec_1 = mock(CreateEventSectorRequest.class);
        when(sec_1.sectorId()).thenReturn(sectorId);

        CreateEventSectorRequest sec_2 = mock(CreateEventSectorRequest.class);
        when(sec_2.sectorId()).thenReturn(sectorId);

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(VALID_VENUE_ID);
        when(request.sectors()).thenReturn(List.of(sec_1, sec_2));

        SectorResponse sectorResp = mock(SectorResponse.class);
        when(sectorResp.id()).thenReturn(sectorId);

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorResp));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResp);

        eventService.createEvent(request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testCreateEvent_SectorNoVenue() {
        CreateEventSectorRequest sectorInvalido = mock(CreateEventSectorRequest.class);
        when(sectorInvalido.sectorId()).thenReturn("sec-no-existe");

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(VALID_VENUE_ID);
        when(request.sectors()).thenReturn(List.of(sectorInvalido));

        SectorResponse sectorValido = mock(SectorResponse.class);
        when(sectorValido.id()).thenReturn("sec-valido");

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorValido));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResp);

        eventService.createEvent(request);
    }

    @Test
    void testAddSector_Successful() {
        // Arrange

        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(request.sectorId()).thenReturn(NON_EXISTENT_ID);
        when(request.price()).thenReturn(BigDecimal.valueOf(12000));

        Event mockEvent = mock(Event.class);
        when(mockEvent.getVenueId()).thenReturn(VALID_VENUE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        SectorResponse sectorResponse = mock(SectorResponse.class);
        when(sectorResponse.id()).thenReturn(NON_EXISTENT_ID);

        VenueResponse venueResponse = mock(VenueResponse.class);
        when(venueResponse.sectors()).thenReturn(List.of(sectorResponse));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResponse);

        // Act
        String returnedSectorId = eventService.addSector(VALID_EVENT_ID, request);

        // Assert
        Assert.assertNotNull(returnedSectorId);
        Assert.assertEquals(returnedSectorId, NON_EXISTENT_ID);

        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAddSector_SectorNoVenue() {
        // Arrange
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(request.sectorId()).thenReturn("sec-inexistente");

        Event mockEvent = mock(Event.class);
        when(mockEvent.getVenueId()).thenReturn(VALID_VENUE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));


        SectorResponse sectorValido = mock(SectorResponse.class);
        when(sectorValido.id()).thenReturn("sec-valido");

        VenueResponse venueResponse = mock(VenueResponse.class);
        when(venueResponse.sectors()).thenReturn(List.of(sectorValido));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResponse);

        //Act
        eventService.addSector(VALID_EVENT_ID, request);

        // Assert
        verify(eventRepository, never()).save(any());
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAddSector_NoExistsEvent() {
        // Arrange
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        // Act
        eventService.addSector(VALID_EVENT_ID, request);

        // Assert
        verify(venueService, never()).findVenueById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test(expectedExceptions = IllegalEventStateException.class)
    public void testAddSector_StateInvalid() {
        // Arrange

        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(request.sectorId()).thenReturn(NON_EXISTENT_ID);
        when(request.price()).thenReturn(BigDecimal.valueOf(10000));

        Event mockEvent = mock(Event.class);
        when(mockEvent.getVenueId()).thenReturn(VALID_VENUE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        SectorResponse sectorResponse = mock(SectorResponse.class);
        when(sectorResponse.id()).thenReturn(NON_EXISTENT_ID);

        VenueResponse venueResponse = mock(VenueResponse.class);
        when(venueResponse.sectors()).thenReturn(List.of(sectorResponse));

        Venue mockVenue = mock(Venue.class);
        when(mockVenue.getId()).thenReturn(VALID_VENUE_ID);

        Sector mockSector = mock(Sector.class);
        when(mockSector.getId()).thenReturn(NON_EXISTENT_ID);
        when(mockVenue.getSectors()).thenReturn(List.of(mockSector));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResponse);

        doThrow(new IllegalEventStateException("Event is already published"))
                .when(mockEvent).addSector(any());

        // Act
        eventService.addSector(VALID_EVENT_ID, request);

        // Assert
        verify(eventRepository, never()).save(any());
    }

    @Test
    public void testPublishEvent_WithCapacities() {
        // Arrange
        Event mockEvent = mock(Event.class);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        EventSector mockSector = mock(EventSector.class);
        when(mockEvent.getSectors()).thenReturn(List.of(mockSector));

        when(eventRepository.findById(anyString())).thenReturn(Optional.of(mockEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        // Act
        eventService.publishEvent(VALID_EVENT_ID);

        // Assert
        verify(mockEvent, times(1)).publish();
        verify(ticketingClient, times(1)).initializeSectors(any());
        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test
    public void testPublishEvent_NoCapacities() {
        // Arrange
        Event mockEvent = mock(Event.class);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        // Act
        eventService.publishEvent(VALID_EVENT_ID);

        // Assert
        verify(mockEvent, times(1)).publish();
        verify(ticketingClient, never()).initializeSectors(any());
        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test(expectedExceptions = IllegalEventStateException.class)
    public void testPublishEvent_StateInvalid() {
        // Arrange
        Event mockEvent = mock(Event.class);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        doThrow(new IllegalEventStateException("Event is already published"))
                .when(mockEvent).publish();

        // Act
        eventService.publishEvent(VALID_EVENT_ID);

        // Assert
        verify(ticketingClient, never()).initializeSectors(any());
        verify(eventRepository, never()).save(any());
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testPublishEvent_NoExistsEvent() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        // Act
        eventService.publishEvent(VALID_EVENT_ID);

        // Assert
        verify(ticketingClient, never()).initializeSectors(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    public void testGetAll_WithResult() {
        // Arrange
        EventCriteriaQuery criteria = mock(EventCriteriaQuery.class);
        Pageable pageable = PageRequest.of(0, 5);

        Event mockEvent = mock(Event.class);

        List<Event> eventList = List.of(mockEvent);
        Page<Event> mockPage = new PageImpl<>(eventList, pageable, 1);

        when(eventCustomRepository.findEventsWithDynamicFilters(criteria, pageable))
                .thenReturn(mockPage);

        // Act
        Page<EventResponse> result = eventService.getAll(criteria, pageable);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTotalElements(), 1);
        Assert.assertEquals(result.getContent().size(), 1);

        verify(eventCustomRepository, times(1))
                .findEventsWithDynamicFilters(criteria, pageable);
    }

    @Test
    public void testGetAll_NoResult() {
        // Arrange
        EventCriteriaQuery criteria = mock(EventCriteriaQuery.class);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(eventCustomRepository.findEventsWithDynamicFilters(criteria, pageable))
                .thenReturn(emptyPage);

        // Act
        Page<EventResponse> result = eventService.getAll(criteria, pageable);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Assert.assertEquals(result.getTotalElements(), 0);

        verify(eventCustomRepository, times(1))
                .findEventsWithDynamicFilters(criteria, pageable);
    }

    @Test
    public void testSearchEvents_WithFiltersAndPagination_Successful() {
        // 1. Arrange
        EventCriteriaQuery criteria = mock(EventCriteriaQuery.class);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").ascending());

        Event mockEvent = mock(Event.class);
        when(mockEvent.getId()).thenReturn(VALID_EVENT_ID);

        Page<Event> mockPage = new PageImpl<>(List.of(mockEvent));

        when(eventCustomRepository.findEventsWithDynamicFilters(eq(criteria), eq(pageable)))
                .thenReturn(mockPage);

        // 2. Act
        Page<?> result = eventService.getAll(criteria, pageable);

        // 3. Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTotalElements(), 1);
        verify(eventCustomRepository, times(1))
                .findEventsWithDynamicFilters(eq(criteria), eq(pageable));
    }

    @Test
    public void testSearchEvents_EmptyResults_ReturnsEmptyPage() {
        // 1. Arrange
        EventCriteriaQuery criteria = mock(EventCriteriaQuery.class);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> emptyPage = new PageImpl<>(Collections.emptyList());

        when(eventCustomRepository.findEventsWithDynamicFilters(eq(criteria), eq(pageable)))
                .thenReturn(emptyPage);

        // 2. Act
        Page<?> result = eventService.getAll(criteria, pageable);

        // 3. Assert
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        verify(eventCustomRepository, times(1))
                .findEventsWithDynamicFilters(eq(criteria), eq(pageable));
    }

    @Test
    public void testGetById_Successful() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getId()).thenReturn(VALID_EVENT_ID);

        EventType mockEventType = mock(EventType.class);
        when(mockEventType.toString()).thenReturn("CONCERT");
        when(mockEvent.getType()).thenReturn(mockEventType);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        EventStateResponse response = eventService.getById(VALID_EVENT_ID);

        Assert.assertNotNull(response);
        verify(eventRepository, times(1)).findById(VALID_EVENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetById_NotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        eventService.getById(VALID_EVENT_ID);
    }

    @Test
    public void testCancelEvent_Successful() {
        Event mockEvent = mock(Event.class);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        eventService.cancelEvent(VALID_EVENT_ID);

        verify(eventRepository, times(1)).findById(VALID_EVENT_ID);
        verify(mockEvent, times(1)).cancel();
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCancelEvent_NotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        eventService.cancelEvent(VALID_EVENT_ID);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testAddSector_NullEventId_ThrowsInvalidInputException() {
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);

        eventService.addSector(null, request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testAddSector_InvalidObjectIdFormat_ThrowsInvalidInputException() {
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);

        eventService.addSector("formato-no-valido-123", request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testPublishEvent_NullEventId_ThrowsInvalidInputException() {
        eventService.publishEvent(null);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testPublishEvent_InvalidObjectIdFormat_ThrowsInvalidInputException() {
        eventService.publishEvent("formato-no-valido-123");
    }
}