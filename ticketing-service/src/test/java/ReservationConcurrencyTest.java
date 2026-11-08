import com.arquiweb.ticketing.event.service.EventService;
import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.domain.exceptions.OccupiedSpotException;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveUnnumberedRequest;
import com.arquiweb.ticketing.reservation.repository.ReservationRepository;
import com.arquiweb.ticketing.reservation.service.ReservationService;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

public class ReservationConcurrencyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private EventService eventService;

    private ReservationService reservationService;
    private AutoCloseable closeable;

    private static final String EVENT_ID = UUID.randomUUID().toString();
    private static final String SECTOR_ID = UUID.randomUUID().toString();

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(inventoryService, reservationRepository);
        this.reservationService = new ReservationService(
                4,
                15,
                "http://localhost:8080",
                reservationRepository,
                inventoryService,
                eventService
        );
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testConcurrentReservations_OnlyOneSucceedsForLastSpot() throws InterruptedException {

        // Arrange
        int threadsCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadsCount);

        AtomicInteger availableCapacity = new AtomicInteger(1);

        SectorCapacity sector = mock(SectorCapacity.class);
        when(sector.getEventId()).thenReturn(EVENT_ID);
        when(sector.getSectorId()).thenReturn(SECTOR_ID);
        when(sector.getPrice()).thenReturn(BigDecimal.valueOf(5000));

        // Act
        doAnswer(invocation -> {
            int qty = invocation.getArgument(0);
            if (availableCapacity.addAndGet(-qty) < 0) {
                throw new OccupiedSpotException("No capacity available");
            }
            return null;
        }).when(sector).reduceCapacity(anyInt());

        when(inventoryService.getSectorCapacity(anyString(), anyString())).thenReturn(sector);
        when(reservationRepository.countTicketsByClientIdAndEventId(any(), any())).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        for (int i = 0; i < threadsCount; i++) {
            final String clientId = UUID.randomUUID().toString();
            executor.submit(() -> {
                try {
                    startLatch.await();
                    ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, 1);
                    reservationService.reserveUnnumbered(request, UUID.fromString(clientId));
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    failureCounter.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }


        startLatch.countDown();

        boolean finishedInTime = finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert
        Assert.assertTrue(finishedInTime, "The concurrency test took too long");
        Assert.assertEquals(successCounter.get(), 1, "Exactly one reservation must have been successful.");
        Assert.assertEquals(failureCounter.get(), threadsCount - 1, "The other threads must have been rejected.");

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
