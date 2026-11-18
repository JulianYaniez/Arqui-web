import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.events.domain.UserPreference;
import com.arquiweb.catalog.events.domain.enums.EventStatus;
import com.arquiweb.catalog.events.domain.enums.EventType;
import com.arquiweb.catalog.events.domain.exceptions.IllegalEventStateException;
import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.dto.request.EventCriteriaQuery;
import com.arquiweb.catalog.events.dto.response.EventResponse;
import com.arquiweb.catalog.events.dto.response.EventStateResponse;
import com.arquiweb.catalog.events.repository.EventCustomRepository;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.events.repository.UserPreferenceRepository;
import com.arquiweb.catalog.events.service.EventService;
import com.arquiweb.catalog.infrastructure.client.TicketingClient;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.catalog.infrastructure.exception.UnauthorizedException;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.service.VenueService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class EventServiceTest {

    private static final String VALID_EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";
    private static final String VALID_VENUE_ID = "64b1f2c3d4e5f6a7b8c9d0e2";
    private static final UUID ORGANIZER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");


    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventCustomRepository eventCustomRepository;

    @Mock
    private TicketingClient ticketingClient;

    @Mock
    private VenueService venueService;

    @Mock
    private UserPreferenceRepository upr;

    private AutoCloseable closeable;

    @BeforeMethod
    void setup() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(eventRepository, eventCustomRepository, ticketingClient, venueService, upr);
        eventService = new EventService(eventRepository,eventCustomRepository,venueService, ticketingClient, upr);
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
        when(request.toDomain(eq(ORGANIZER_ID), eq(venueResp), anyList())).thenReturn(mockSavedEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(mockSavedEvent);

        String idGenerado = eventService.createEvent(ORGANIZER_ID, request);

        Assert.assertNotNull(idGenerado);
        Assert.assertEquals(idGenerado, "event-gen-999");
        verify(eventRepository, times(1)).save(mockSavedEvent);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testCreateEvent_DuplicatedSector() {
        String sectorId = "sec-01";

        CreateEventSectorRequest sec1 = mock(CreateEventSectorRequest.class);
        when(sec1.sectorId()).thenReturn(sectorId);

        CreateEventSectorRequest sec2 = mock(CreateEventSectorRequest.class);
        when(sec2.sectorId()).thenReturn(sectorId);

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(VALID_VENUE_ID);
        when(request.sectors()).thenReturn(List.of(sec1, sec2));

        SectorResponse sectorResp = mock(SectorResponse.class);
        when(sectorResp.id()).thenReturn(sectorId);

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorResp));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResp);

        eventService.createEvent(ORGANIZER_ID, request);
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

        eventService.createEvent(ORGANIZER_ID, request);
    }

    @Test
    public void testAddSector_Successful() {
        String sectorId = "sec-01";

        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(request.sectorId()).thenReturn(sectorId);
        when(request.price()).thenReturn(BigDecimal.valueOf(12000));

        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(mockEvent.getVenueId()).thenReturn(VALID_VENUE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        SectorResponse sectorResponse = mock(SectorResponse.class);
        when(sectorResponse.id()).thenReturn(sectorId);

        VenueResponse venueResponse = mock(VenueResponse.class);
        when(venueResponse.sectors()).thenReturn(List.of(sectorResponse));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResponse);

        String returnedSectorId = eventService.addSector(ORGANIZER_ID, VALID_EVENT_ID, request);

        Assert.assertNotNull(returnedSectorId);
        Assert.assertEquals(returnedSectorId, sectorId);
        verify(mockEvent, times(1)).addSector(any(EventSector.class));
        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testAddSector_WrongOrganizer_ThrowsUnauthorizedException() {
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        eventService.addSector(OTHER_USER_ID, VALID_EVENT_ID, request);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAddSector_SectorNoVenue() {
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(request.sectorId()).thenReturn("sec-inexistente");

        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(mockEvent.getVenueId()).thenReturn(VALID_VENUE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        SectorResponse sectorValido = mock(SectorResponse.class);
        when(sectorValido.id()).thenReturn("sec-valido");

        VenueResponse venueResponse = mock(VenueResponse.class);
        when(venueResponse.sectors()).thenReturn(List.of(sectorValido));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResponse);

        eventService.addSector(ORGANIZER_ID, VALID_EVENT_ID, request);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAddSector_NoExistsEvent() {
        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        eventService.addSector(ORGANIZER_ID, VALID_EVENT_ID, request);
    }

    @Test(expectedExceptions = IllegalEventStateException.class)
    public void testAddSector_StateInvalid() {
        String sectorId = "sec-01";

        CreateEventSectorRequest request = mock(CreateEventSectorRequest.class);
        when(request.sectorId()).thenReturn(sectorId);
        when(request.price()).thenReturn(BigDecimal.valueOf(10000));

        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(mockEvent.getVenueId()).thenReturn(VALID_VENUE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        SectorResponse sectorResponse = mock(SectorResponse.class);
        when(sectorResponse.id()).thenReturn(sectorId);

        VenueResponse venueResponse = mock(VenueResponse.class);
        when(venueResponse.sectors()).thenReturn(List.of(sectorResponse));

        when(venueService.findVenueById(VALID_VENUE_ID)).thenReturn(venueResponse);

        doThrow(new IllegalEventStateException("Event is already published"))
                .when(mockEvent).addSector(any());

        eventService.addSector(ORGANIZER_ID, VALID_EVENT_ID, request);
    }

    @Test
    public void testPublishEvent_WithCapacities() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(mockEvent.getId()).thenReturn(VALID_EVENT_ID);

        EventSector mockSector = mock(EventSector.class);
        when(mockEvent.getSectors()).thenReturn(List.of(mockSector));

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        eventService.publishEvent(ORGANIZER_ID, VALID_EVENT_ID);

        verify(mockEvent, times(1)).publish();
        verify(ticketingClient, times(1)).initializeSectors(any());
        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test
    public void testPublishEvent_NoCapacities() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(mockEvent.getId()).thenReturn(VALID_EVENT_ID);
        when(mockEvent.getSectors()).thenReturn(Collections.emptyList());

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        eventService.publishEvent(ORGANIZER_ID, VALID_EVENT_ID);

        verify(mockEvent, times(1)).publish();
        verify(ticketingClient, never()).initializeSectors(any());
        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testPublishEvent_WrongOrganizer_ThrowsUnauthorizedException() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        eventService.publishEvent(OTHER_USER_ID, VALID_EVENT_ID);
    }

    @Test(expectedExceptions = IllegalEventStateException.class)
    public void testPublishEvent_StateInvalid() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        doThrow(new IllegalEventStateException("Event is already published"))
                .when(mockEvent).publish();

        eventService.publishEvent(ORGANIZER_ID, VALID_EVENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testPublishEvent_NoExistsEvent() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        eventService.publishEvent(ORGANIZER_ID, VALID_EVENT_ID);
    }

    @Test
    public void testCancelEvent_Successful() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        eventService.cancelEvent(ORGANIZER_ID, VALID_EVENT_ID);

        verify(mockEvent, times(1)).cancel();
        verify(eventRepository, times(1)).save(mockEvent);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testCancelEvent_WrongOrganizer_ThrowsUnauthorizedException() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getOrganizerId()).thenReturn(ORGANIZER_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        eventService.cancelEvent(OTHER_USER_ID, VALID_EVENT_ID);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testGetEventById_NullId_ThrowsInvalidInputException() {
        eventService.getEventById(null);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testGetEventById_InvalidObjectIdFormat_ThrowsInvalidInputException() {
        eventService.getEventById("id-no-valido-mongo");
    }

    @Test
    public void testGetEventById_Valid_ReturnsEventResponse() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getSectors()).thenReturn(Collections.emptyList());
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        EventResponse response = eventService.getEventById(VALID_EVENT_ID);

        Assert.assertNotNull(response);
    }

    @Test
    public void testGetById_Found_ReturnsEventStateResponse() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getId()).thenReturn(VALID_EVENT_ID);
        when(mockEvent.getType()).thenReturn(EventType.values()[0]);
        when(mockEvent.getStatus()).thenReturn(EventStatus.DRAFT);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(mockEvent));

        EventStateResponse response = eventService.getById(VALID_EVENT_ID);

        Assert.assertNotNull(response);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetById_NotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.empty());

        eventService.getById(VALID_EVENT_ID);
    }

    @Test
    public void testGetAll_WithResult() {
        EventCriteriaQuery criteria = mock(EventCriteriaQuery.class);
        Pageable pageable = PageRequest.of(0, 5);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getSectors()).thenReturn(Collections.emptyList());

        Page<Event> mockPage = new PageImpl<>(List.of(mockEvent), pageable, 1);
        when(eventCustomRepository.findEventsWithDynamicFilters(criteria, pageable))
                .thenReturn(mockPage);

        Page<EventResponse> result = eventService.getAll(criteria, pageable);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTotalElements(), 1);
        verify(eventCustomRepository, times(1)).findEventsWithDynamicFilters(criteria, pageable);
    }

    @Test
    public void testGetAll_NoResult() {
        EventCriteriaQuery criteria = mock(EventCriteriaQuery.class);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(eventCustomRepository.findEventsWithDynamicFilters(criteria, pageable))
                .thenReturn(emptyPage);

        Page<EventResponse> result = eventService.getAll(criteria, pageable);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetRecommended_NoPreference_QueriesByDate() {
        Pageable pageable = PageRequest.of(0, 10);
        when(upr.findById(ORGANIZER_ID)).thenReturn(Optional.empty());

        Event mockEvent = mock(Event.class);
        when(mockEvent.getSectors()).thenReturn(Collections.emptyList());
        Page<Event> mockPage = new PageImpl<>(List.of(mockEvent), pageable, 1);

        when(eventRepository.findByDateTimeGreaterThan(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<EventResponse> result = eventService.getRecommended(ORGANIZER_ID, pageable);

        Assert.assertEquals(result.getTotalElements(), 1);
        verify(eventRepository, times(1)).findByDateTimeGreaterThan(any(LocalDateTime.class), any(Pageable.class));
        verifyNoInteractions(eventCustomRepository);
    }

    @Test
    public void testGetRecommended_WithPreference_QueriesCustomRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        UserPreference mockPreference = mock(UserPreference.class);
        when(upr.findById(ORGANIZER_ID)).thenReturn(Optional.of(mockPreference));

        Event mockEvent = mock(Event.class);
        when(mockEvent.getSectors()).thenReturn(Collections.emptyList());
        Page<Event> mockPage = new PageImpl<>(List.of(mockEvent), pageable, 1);

        when(eventCustomRepository.findRecommended(mockPreference, pageable)).thenReturn(mockPage);

        Page<EventResponse> result = eventService.getRecommended(ORGANIZER_ID, pageable);

        Assert.assertEquals(result.getTotalElements(), 1);
        verify(eventCustomRepository, times(1)).findRecommended(mockPreference, pageable);
        verify(eventRepository, never()).findByDateTimeGreaterThan(any(), any());
    }
}