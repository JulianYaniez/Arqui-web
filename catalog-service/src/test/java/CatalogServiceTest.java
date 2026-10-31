import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.events.service.EventService;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.repository.VenueRepository;
import com.arquiweb.catalog.venues.service.VenueService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class CatalogServiceTest {
    /**
     * String createEvent(CreateEventRequest request)
     * String addSector(String eventId, CreateEventSectorRequest request)
     * void publishEvent(String eventId)
     * Page<EventResponse> getAll(EventCriteriaQuery criteria, Pageable pag)
     * */

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    /**
     * String createdVenue(CreateVenueRequest request)
     * VenueResponse findVenueById(String venueId)
     */

    private VenueService venueService;

    @Mock
    private VenueRepository venueRepository;

    private AutoCloseable closeable;

    @BeforeMethod
    void setup() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        venueService = new VenueService(venueRepository);
    }

    @AfterMethod
    void tearDown() throws Exception {
        if(closeable != null)
            closeable.close();
    }

    @Test
    public void testCreateEvent_Exitoso() {
        String venueId = "venue-100";
        String sectorId = "sec-01";

        CreateEventSectorRequest sectorReq = mock(CreateEventSectorRequest.class);
        when(sectorReq.sectorId()).thenReturn(sectorId);
        when(sectorReq.price()).thenReturn(BigDecimal.valueOf(15000));

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(venueId);
        when(request.sectors()).thenReturn(List.of(sectorReq));

        SectorResponse sectorResp = mock(SectorResponse.class);
        when(sectorResp.id()).thenReturn(sectorId);

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorResp));

        when(venueService.findVenueById(venueId)).thenReturn(venueResp);

        Event mockSavedEvent = mock(Event.class);
        when(mockSavedEvent.getId()).thenReturn("event-gen-999");
        when(request.toDomain(eq(venueResp), anyList())).thenReturn(mockSavedEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(mockSavedEvent);

        String idGenerado = eventService.createEvent(request);

        Assert.assertNotNull(idGenerado);
        Assert.assertEquals(idGenerado, "event-gen-999");
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testCreateEvent_SectorDuplicado_LanzaExcepcion() {
        String venueId = "venue-100";
        String sectorId = "sec-01";

        CreateEventSectorRequest sec_1 = mock(CreateEventSectorRequest.class);
        when(sec_1.sectorId()).thenReturn(sectorId);

        CreateEventSectorRequest sec_2 = mock(CreateEventSectorRequest.class);
        when(sec_2.sectorId()).thenReturn(sectorId);

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(venueId);
        when(request.sectors()).thenReturn(List.of(sec_1, sec_2));

        SectorResponse sectorResp = mock(SectorResponse.class);
        when(sectorResp.id()).thenReturn(sectorId);

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorResp));

        when(venueService.findVenueById(venueId)).thenReturn(venueResp);

        eventService.createEvent(request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testCreateEvent_SectorNoPerteneceAVenue_LanzaExcepcion() {
        String venueId = "venue-100";

        CreateEventSectorRequest sectorInvalido = mock(CreateEventSectorRequest.class);
        when(sectorInvalido.sectorId()).thenReturn("sec-no-existe");

        CreateEventRequest request = mock(CreateEventRequest.class);
        when(request.venueId()).thenReturn(venueId);
        when(request.sectors()).thenReturn(List.of(sectorInvalido));

        SectorResponse sectorValido = mock(SectorResponse.class);
        when(sectorValido.id()).thenReturn("sec-valido");

        VenueResponse venueResp = mock(VenueResponse.class);
        when(venueResp.sectors()).thenReturn(List.of(sectorValido));

        when(venueService.findVenueById(venueId)).thenReturn(venueResp);

        eventService.createEvent(request);
    }

    void addSector(String eventId, CreateEventSectorRequest request) {

    }
}