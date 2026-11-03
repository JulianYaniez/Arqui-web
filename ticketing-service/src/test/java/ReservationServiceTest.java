import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.domain.enums.ReservationStatus;
import com.arquiweb.ticketing.reservation.domain.exceptions.IllegalReservationStateChangeException;
import com.arquiweb.ticketing.reservation.domain.exceptions.OccupiedSpotException;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveNumberedRequest;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveUnnumberedRequest;
import com.arquiweb.ticketing.reservation.dto.responses.ReservationResponse;
import com.arquiweb.ticketing.reservation.dto.responses.TicketQrResponse;
import com.arquiweb.ticketing.reservation.repository.ReservationRepository;
import com.arquiweb.ticketing.reservation.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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

        // Arrange
        int quantity = 2;
        ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, quantity);

        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(0);

        SectorCapacity sector = mock(SectorCapacity.class);
        when(sector.getEventId()).thenReturn(EVENT_ID);
        when(sector.getSectorId()).thenReturn(SECTOR_ID);
        when(sector.getPrice()).thenReturn(BigDecimal.valueOf(5000));

        when(inventoryService.getSectorCapacity(EVENT_ID, SECTOR_ID)).thenReturn(sector);

        // Act
        ReservationResponse response = reservationService.reserveUnnumbered(request, CLIENT_ID);

        // Assert
        Assert.assertNotNull(response);
        verify(sector, times(1)).reduceCapacity(quantity);
        verify(inventoryService, times(1)).saveSectorCapacity(sector);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testReserveUnnumbered_ExceedsMaxBatchLimit() {

        // Arrange
        ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, MAX_TICKETS + 1);

        // Act
        reservationService.reserveUnnumbered(request, CLIENT_ID);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testReserveUnnumbered_ExceedsAccumulatedLimit() {

        // Arrange
        ReserveUnnumberedRequest request = new ReserveUnnumberedRequest(EVENT_ID, SECTOR_ID, 2);
        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(3);

        // Act
        reservationService.reserveUnnumbered(request, CLIENT_ID);
    }

    @Test
    public void testReserveNumbered_Successful() {

        // Arrange
        List<UUID> ticketIds = List.of(TICKET_ID);
        ReserveNumberedRequest request = new ReserveNumberedRequest(EVENT_ID, ticketIds);

        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(0);

        Ticket mockTicket = mock(Ticket.class);
        when(mockTicket.getStatus()).thenReturn(TicketStatus.AVAILABLE);
        when(mockTicket.getEventId()).thenReturn(EVENT_ID);
        when(mockTicket.getPrice()).thenReturn(BigDecimal.valueOf(10000));

        when(inventoryService.findTicketWithIdIn(ticketIds)).thenReturn(List.of(mockTicket));

        // Act
        ReservationResponse response = reservationService.reserveNumbered(request, CLIENT_ID);

        // Assert
        Assert.assertNotNull(response);
        verify(mockTicket, times(1)).lockTemporary();
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testReserveNumbered_TicketCountMismatch() {

        // Arrange
        List<UUID> ticketIds = List.of(TICKET_ID, UUID.randomUUID());
        ReserveNumberedRequest request = new ReserveNumberedRequest(EVENT_ID, ticketIds);

        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(0);
        when(inventoryService.findTicketWithIdIn(ticketIds)).thenReturn(List.of(mock(Ticket.class)));

        // Act
        reservationService.reserveNumbered(request, CLIENT_ID);
    }

    @Test(expectedExceptions = OccupiedSpotException.class)
    public void testReserveNumbered_SeatUnavailable() {

        // Arrange
        List<UUID> ticketIds = List.of(TICKET_ID);
        ReserveNumberedRequest request = new ReserveNumberedRequest(EVENT_ID, ticketIds);

        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(0);

        Ticket occupiedTicket = mock(Ticket.class);
        when(occupiedTicket.getStatus()).thenReturn(TicketStatus.LOCKED_TEMPORARY);
        when(occupiedTicket.getEventId()).thenReturn(EVENT_ID);

        when(inventoryService.findTicketWithIdIn(ticketIds)).thenReturn(List.of(occupiedTicket));

        // Act
        reservationService.reserveNumbered(request, CLIENT_ID);
    }

    @Test(expectedExceptions = OccupiedSpotException.class)
    public void testReserveNumbered_WrongEvent() {

        // Arrange
        List<UUID> ticketIds = List.of(TICKET_ID);
        ReserveNumberedRequest request = new ReserveNumberedRequest(EVENT_ID, ticketIds);

        when(reservationRepository.countTicketsByClientIdAndEventId(CLIENT_ID, EVENT_ID)).thenReturn(0);

        Ticket wrongEventTicket = mock(Ticket.class);
        when(wrongEventTicket.getStatus()).thenReturn(TicketStatus.AVAILABLE);
        when(wrongEventTicket.getEventId()).thenReturn("other-event-id");

        when(inventoryService.findTicketWithIdIn(ticketIds)).thenReturn(List.of(wrongEventTicket));

        // Act
        reservationService.reserveNumbered(request, CLIENT_ID);
    }

    @Test
    public void testCancelReservations_Successful() {

        // Arrange
        List<UUID> expiredIds = List.of(RESERVATION_ID);
        CapacityRefundDto refundDto = mock(CapacityRefundDto.class);

        when(inventoryService.countCapacities(expiredIds)).thenReturn(List.of(refundDto));

        // Act
        reservationService.cancelReservations(expiredIds);

        // Assert
        verify(inventoryService, times(1)).releaseNumberedTickets(expiredIds);
        verify(inventoryService, times(1)).refundCapacity(refundDto);
        verify(inventoryService, times(1)).deleteUnnumberedTickets(expiredIds);
        verify(reservationRepository, times(1)).expire(expiredIds, ReservationStatus.EXPIRED);
    }

    @Test
    public void testCancelReservations_EmptyList_DoesNothing() {

        // Act
        reservationService.cancelReservations(Collections.emptyList());

        //Assert
        verify(inventoryService, never()).releaseNumberedTickets(any());
        verify(reservationRepository, never()).expire(any(), any());
    }

    @Test
    public void testHandlePaymentFailure_DelegatesToCancelReservations() {

        //Act
        when(inventoryService.countCapacities(anyList())).thenReturn(Collections.emptyList());

        reservationService.handlePaymentFailure(RESERVATION_ID);

        // Assert
        verify(inventoryService, times(1)).releaseNumberedTickets(List.of(RESERVATION_ID));
        verify(reservationRepository, times(1)).expire(List.of(RESERVATION_ID), ReservationStatus.EXPIRED);
    }

    @Test
    public void testConfirmReservation_Successful() {

        // Arrange
        Reservation reservation = mock(Reservation.class);
        Set<Ticket> tickets = Set.of(mock(Ticket.class));

        when(reservation.getStatus()).thenReturn(ReservationStatus.PENDING);
        when(reservation.getTickets()).thenReturn(tickets);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // Act
        reservationService.confirmReservation(RESERVATION_ID, PAYMENT_ID);

        // Assert
        verify(inventoryService, times(1)).confirmTickets(tickets);
        verify(reservation, times(1)).setStatus(ReservationStatus.PAID);
        verify(reservation, times(1)).setPaymentId(PAYMENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testConfirmReservation_NotFound() {

        when(reservationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        reservationService.confirmReservation(RESERVATION_ID, PAYMENT_ID);
    }

    @Test(expectedExceptions = IllegalReservationStateChangeException.class)
    public void testConfirmReservation_NotPending() {

        // Arrange
        Reservation reservation = mock(Reservation.class);
        when(reservation.getStatus()).thenReturn(ReservationStatus.PAID);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // Act
        reservationService.confirmReservation(RESERVATION_ID, PAYMENT_ID);
    }

    @Test
    public void testExpire_DelegatesToRepository() {

        // Arrange
        List<UUID> ids = List.of(RESERVATION_ID);

        // Act
        reservationService.expire(ids);

        // Assert
        verify(reservationRepository, times(1)).expire(ids, ReservationStatus.EXPIRED);
    }

    @Test
    public void testFindExpired_DelegatesToRepository() {

        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(reservationRepository.findExpired(eq(now), eq(ReservationStatus.PENDING)))
                .thenReturn(List.of(RESERVATION_ID));

        // Act
        List<UUID> result = reservationService.findExpired(now);

        // Assert
        Assert.assertEquals(result.size(), 1);
        verify(reservationRepository, times(1)).findExpired(now, ReservationStatus.PENDING);
    }

    @Test
    public void testSpendTicket_DelegatesToInventoryService() {

        // Act
        reservationService.spendTicket(TICKET_ID);

        // Assert
        verify(inventoryService, times(1)).useTicket(TICKET_ID);
    }

    @Test
    public void testGetTicketQr_Successful() {

        // Arrange
        Reservation reservation = mock(Reservation.class);
        when(reservation.getStatus()).thenReturn(ReservationStatus.PAID);

        Ticket ticket = mock(Ticket.class);
        when(ticket.getId()).thenReturn(TICKET_ID);
        when(ticket.getQrToken()).thenReturn("qr-token-abc");

        when(reservation.getTickets()).thenReturn(Set.of(ticket));
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // Act
        TicketQrResponse response = reservationService.getTicketQr(RESERVATION_ID, TICKET_ID);

        // Assert
        Assert.assertNotNull(response);
        Assert.assertEquals(response.ticketId(), TICKET_ID);
        Assert.assertTrue(response.qrImage().contains("qr-token-abc"));
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetTicketQr_ReservationNotFound() {

        when(reservationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        reservationService.getTicketQr(RESERVATION_ID, TICKET_ID);
    }

    @Test(expectedExceptions = IllegalReservationStateChangeException.class)
    public void testGetTicketQr_ReservationNotPaid() {

        // Arrange
        Reservation reservation = mock(Reservation.class);
        when(reservation.getStatus()).thenReturn(ReservationStatus.PENDING);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // Act
        reservationService.getTicketQr(RESERVATION_ID, TICKET_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetTicketQr_TicketNotInReservation() {

        // Arrange
        Reservation reservation = mock(Reservation.class);
        when(reservation.getStatus()).thenReturn(ReservationStatus.PAID);
        when(reservation.getTickets()).thenReturn(Set.of());
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // Act
        reservationService.getTicketQr(RESERVATION_ID, TICKET_ID);
    }
}
