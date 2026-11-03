import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveUnnumberedRequest;
import com.arquiweb.ticketing.reservation.dto.responses.ReservationResponse;
import com.arquiweb.ticketing.reservation.repository.ReservationRepository;
import com.arquiweb.ticketing.reservation.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    private static final int MAX_TICKETS = 4;
    private static final int TTL_MINUTES = 15;
    private static final String APP_URL = "http://localhost:8080";
    private static final String EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";
    private static final String CLIENT_ID = "client-user-123";
    private static final String SECTOR_ID = "sector-general";
    private static final UUID RESERVATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PAYMENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID TICKET_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private InventoryService inventoryService;

    private ReservationService reservationService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        this.reservationService = new ReservationService(
                                    MAX_TICKETS,
                                    TTL_MINUTES,
                                    APP_URL,
                                    reservationRepository,
                                    inventoryService
                                );
    }
    @AfterEach
    void tearDown() throws Exception {
        if(closeable != null)
            closeable.close();
    }

    @Test
    public void testReserveUnnumbered_Successful() {
        int quantity = 2;
        ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, quantity);

        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(0);

        SectorCapacity sector = mock(SectorCapacity.class);
        when(sector.getEventId()).thenReturn(EVENT_ID);
        when(sector.getSectorId()).thenReturn(SECTOR_ID);
        when(sector.getPrice()).thenReturn(BigDecimal.valueOf(5000));

        when(inventoryService.getSectorCapacity(EVENT_ID, SECTOR_ID)).thenReturn(sector);

        ReservationResponse response = reservationService.reserveUnnumbered(request, CLIENT_ID);

        Assert.assertNotNull(response);
        verify(sector, times(1)).reduceCapacity(quantity);
        verify(inventoryService, times(1)).saveSectorCapacity(sector);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testReserveUnnumbered_ExceedsMaxBatchLimit() {
        ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, MAX_TICKETS + 1);

        reservationService.reserveUnnumbered(request, CLIENT_ID);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testReserveUnnumbered_ExceedsAccumulatedLimit() {
        ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, 2);
        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(3);

        reservationService.reserveUnnumbered(request, CLIENT_ID);
    }
}
