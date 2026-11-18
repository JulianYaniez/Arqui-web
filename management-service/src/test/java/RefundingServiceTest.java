import com.arquiweb.management.infrastructure.client.CatalogClient;
import com.arquiweb.management.infrastructure.client.PaymentClient;
import com.arquiweb.management.infrastructure.client.TicketingClient;
import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;
import com.arquiweb.management.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.management.infrastructure.exception.ServiceUnavailableException;
import com.arquiweb.management.infrastructure.exception.UnprocessableEntityException;
import com.arquiweb.management.refunding.domain.RefundRequest;
import com.arquiweb.management.refunding.domain.exceptions.InvalidRefundStatusException;
import com.arquiweb.management.refunding.domain.exceptions.UnauthorizedRefundRequestException;
import com.arquiweb.management.refunding.dto.requests.CreateRefundRequest;
import com.arquiweb.management.refunding.repository.RefundRequestRepository;
import com.arquiweb.management.refunding.service.RefundingService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class RefundingServiceTest {

    @Mock
    private RefundRequestRepository refundRequestRepository;

    @Mock
    private TicketingClient ticketingClient;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private PaymentClient paymentClient;

    private RefundingService refundingService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(refundRequestRepository, ticketingClient, catalogClient, paymentClient);
        refundingService = new RefundingService(
                refundRequestRepository,
                ticketingClient,
                catalogClient,
                paymentClient
        );
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testCreateRefundRequest_Successful() {
        UUID clientId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        String eventId = "64b1f2c3d4e5f6a7b8c9d0e1";

        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.reason()).thenReturn("No puedo asistir");

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.reservationId()).thenReturn(reservationId);
        when(reservation.clientId()).thenReturn(clientId);
        when(reservation.status()).thenReturn("PAID");
        when(reservation.eventId()).thenReturn(eventId);
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        EventStateResponse event = mock(EventStateResponse.class);
        when(event.dateTime()).thenReturn(LocalDateTime.now().plusHours(100)); // > 72hs
        when(catalogClient.getEventById(eventId)).thenReturn(event);

        RefundRequest savedRefund = mock(RefundRequest.class);
        UUID expectedId = UUID.randomUUID();
        when(savedRefund.getId()).thenReturn(expectedId);
        when(refundRequestRepository.save(any(RefundRequest.class))).thenReturn(savedRefund);

        UUID resultId = refundingService.createRefundRequest(request, clientId);

        Assert.assertEquals(resultId, expectedId);
        verify(ticketingClient, times(1)).setRequestRefund(reservationId);
        verify(refundRequestRepository, times(1)).save(any(RefundRequest.class));
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateRefundRequest_ReservationNotFound() {
        UUID reservationId = UUID.randomUUID();
        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        when(ticketingClient.getReservationById(reservationId)).thenThrow(mock(FeignException.NotFound.class));

        refundingService.createRefundRequest(request, UUID.randomUUID());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCreateRefundRequest_TicketingUnavailable() {
        UUID reservationId = UUID.randomUUID();
        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        when(ticketingClient.getReservationById(reservationId)).thenThrow(new RuntimeException("Timeout"));

        refundingService.createRefundRequest(request, UUID.randomUUID());
    }

    @Test(expectedExceptions = UnauthorizedRefundRequestException.class)
    public void testCreateRefundRequest_WrongClient() {
        UUID reservationId = UUID.randomUUID();
        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.clientId()).thenReturn(UUID.randomUUID()); // Other client
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        refundingService.createRefundRequest(request, UUID.randomUUID());
    }

    @Test(expectedExceptions = InvalidRefundStatusException.class)
    public void testCreateRefundRequest_InvalidStatus() {
        UUID clientId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.clientId()).thenReturn(clientId);
        when(reservation.status()).thenReturn("CANCELLED");
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        refundingService.createRefundRequest(request, clientId);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateRefundRequest_EventNotFound() {
        UUID clientId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        String eventId = "event-1";

        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.clientId()).thenReturn(clientId);
        when(reservation.status()).thenReturn("PAID");
        when(reservation.eventId()).thenReturn(eventId);
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        when(catalogClient.getEventById(eventId)).thenThrow(mock(FeignException.NotFound.class));

        refundingService.createRefundRequest(request, clientId);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCreateRefundRequest_CatalogUnavailable() {
        UUID clientId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        String eventId = "event-1";

        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.clientId()).thenReturn(clientId);
        when(reservation.status()).thenReturn("PAID");
        when(reservation.eventId()).thenReturn(eventId);
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        when(catalogClient.getEventById(eventId)).thenThrow(new RuntimeException("Catalog down"));

        refundingService.createRefundRequest(request, clientId);
    }

    @Test(expectedExceptions = UnprocessableEntityException.class)
    public void testCreateRefundRequest_Under72Hours() {
        UUID clientId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        String eventId = "event-1";

        CreateRefundRequest request = mock(CreateRefundRequest.class);
        when(request.reservationId()).thenReturn(reservationId);

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.clientId()).thenReturn(clientId);
        when(reservation.status()).thenReturn("PAID");
        when(reservation.eventId()).thenReturn(eventId);
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        EventStateResponse event = mock(EventStateResponse.class);
        when(event.dateTime()).thenReturn(LocalDateTime.now().plusHours(48)); // < 72hs
        when(catalogClient.getEventById(eventId)).thenReturn(event);

        refundingService.createRefundRequest(request, clientId);
    }

    // =========================================================================
    // 2. acceptRefund
    // =========================================================================

    @Test
    public void testAcceptRefund_Successful() {
        UUID refundId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.reservationId()).thenReturn(reservationId);
        when(reservation.paymentId()).thenReturn(paymentId);
        when(reservation.totalPrice()).thenReturn(BigDecimal.valueOf(5000));
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        refundingService.acceptRefund(refundId, adminId);

        verify(refundRequest, times(1)).accept(adminId);
        verify(ticketingClient, times(1)).acceptRefund(reservationId);
        verify(paymentClient, times(1)).refundPayment(paymentId);
        verify(refundRequestRepository, times(1)).save(refundRequest);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAcceptRefund_RefundRequestNotFound() {
        UUID refundId = UUID.randomUUID();
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.empty());

        refundingService.acceptRefund(refundId, UUID.randomUUID());
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAcceptRefund_ReservationNotFound() {
        UUID refundId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        when(ticketingClient.getReservationById(reservationId)).thenThrow(mock(FeignException.NotFound.class));

        refundingService.acceptRefund(refundId, UUID.randomUUID());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testAcceptRefund_GetReservationUnavailable() {
        UUID refundId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        when(ticketingClient.getReservationById(reservationId)).thenThrow(new RuntimeException("Error"));

        refundingService.acceptRefund(refundId, UUID.randomUUID());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testAcceptRefund_PaymentOrTicketingFails() {
        UUID refundId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.reservationId()).thenReturn(reservationId);
        when(reservation.paymentId()).thenReturn(paymentId);
        when(reservation.totalPrice()).thenReturn(BigDecimal.valueOf(5000));
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        doThrow(new RuntimeException("Payment down")).when(paymentClient).refundPayment(paymentId);

        refundingService.acceptRefund(refundId, adminId);
    }

    // =========================================================================
    // 3. rejectRefund
    // =========================================================================

    @Test
    public void testRejectRefund_Successful() {
        UUID refundId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.reservationId()).thenReturn(reservationId);
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        refundingService.rejectRefund(refundId, adminId);

        verify(refundRequest, times(1)).reject(adminId);
        verify(ticketingClient, times(1)).rejectRefund(reservationId);
        verify(refundRequestRepository, times(1)).save(refundRequest);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testRejectRefund_ReservationNotFound() {
        UUID refundId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        when(ticketingClient.getReservationById(reservationId)).thenThrow(mock(FeignException.NotFound.class));

        refundingService.rejectRefund(refundId, UUID.randomUUID());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testRejectRefund_GetReservationUnavailable() {
        UUID refundId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        when(ticketingClient.getReservationById(reservationId)).thenThrow(new RuntimeException("Error"));

        refundingService.rejectRefund(refundId, UUID.randomUUID());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testRejectRefund_TicketingRejectFails() {
        UUID refundId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        RefundRequest refundRequest = mock(RefundRequest.class);
        when(refundRequest.getReservationId()).thenReturn(reservationId);
        when(refundRequestRepository.findById(refundId)).thenReturn(Optional.of(refundRequest));

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.reservationId()).thenReturn(reservationId);
        when(ticketingClient.getReservationById(reservationId)).thenReturn(reservation);

        doThrow(new RuntimeException("Ticketing down")).when(ticketingClient).rejectRefund(reservationId);

        refundingService.rejectRefund(refundId, adminId);
    }

    @Test
    public void testCancelEvent_Successful() {
        String eventId = "event-123";

        ReservationStateResponse reservation = mock(ReservationStateResponse.class);
        when(reservation.reservationId()).thenReturn(UUID.randomUUID());
        when(reservation.paymentId()).thenReturn(UUID.randomUUID());
        when(reservation.totalPrice()).thenReturn(BigDecimal.valueOf(2500));

        when(ticketingClient.getReservationsByEventId(eventId, "PAID")).thenReturn(List.of(reservation));

        refundingService.cancelEvent(eventId);

        verify(catalogClient, times(1)).cancelEvent(eventId);
        verify(paymentClient, times(1)).bulkRefund(anyList());
        verify(ticketingClient, times(1)).bulkCancelReservations(eventId);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCancelEvent_EventNotFound() {
        String eventId = "event-123";
        doThrow(mock(FeignException.NotFound.class)).when(catalogClient).cancelEvent(eventId);

        refundingService.cancelEvent(eventId);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_CatalogUnavailable() {
        String eventId = "event-123";
        doThrow(new RuntimeException("Catalog down")).when(catalogClient).cancelEvent(eventId);

        refundingService.cancelEvent(eventId);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_GetReservationsUnavailable() {
        String eventId = "event-123";
        when(ticketingClient.getReservationsByEventId(eventId, "PAID")).thenThrow(new RuntimeException("Ticketing down"));

        refundingService.cancelEvent(eventId);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_BulkRefundUnavailable() {
        String eventId = "event-123";
        when(ticketingClient.getReservationsByEventId(eventId, "PAID")).thenReturn(List.of());
        doThrow(new RuntimeException("Payment down")).when(paymentClient).bulkRefund(anyList());

        refundingService.cancelEvent(eventId);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_BulkCancelUnavailable() {
        String eventId = "event-123";
        when(ticketingClient.getReservationsByEventId(eventId, "PAID")).thenReturn(List.of());
        doThrow(new RuntimeException("Ticketing down")).when(ticketingClient).bulkCancelReservations(eventId);

        refundingService.cancelEvent(eventId);
    }
}