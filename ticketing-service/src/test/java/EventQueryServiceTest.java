import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.repository.TicketingEventRepository;
import com.arquiweb.ticketing.event.service.EventQueryService;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class EventQueryServiceTest {

    private static final String EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";

    @Mock
    private TicketingEventRepository ticketingEventRepository;

    private EventQueryService eventQueryService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(ticketingEventRepository);
        eventQueryService = new EventQueryService(ticketingEventRepository);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testGetById_Found_ReturnsEvent() {
        TicketingEvent event = new TicketingEvent(EVENT_ID, true, false);
        when(ticketingEventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        TicketingEvent result = eventQueryService.getById(EVENT_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, event);
        verify(ticketingEventRepository, times(1)).findById(EVENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetById_NotFound_ThrowsResourceNotFoundException() {
        when(ticketingEventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        eventQueryService.getById(EVENT_ID);
    }
}