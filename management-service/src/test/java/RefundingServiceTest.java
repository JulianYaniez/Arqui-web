import com.arquiweb.management.infrastructure.client.CatalogClient;
import com.arquiweb.management.infrastructure.client.PaymentClient;
import com.arquiweb.management.infrastructure.client.TicketingClient;
import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;
import com.arquiweb.management.infrastructure.client.dto.responses.PaymentRefundRequest;
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
import feign.Request;
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

    private RefundingService service;
    private AutoCloseable closeable;

    private static final UUID CLIENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_CLIENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID RESERVATION_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID PAYMENT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID REFUND_REQUEST_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ADMIN_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final String EVENT_ID = "event-test-123";

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(ticketingClient, refundRequestRepository, paymentClient, catalogClient);
            service = new RefundingService(
                                refundRequestRepository,
                                ticketingClient,
                                catalogClient,
                                paymentClient
                            );
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if(closeable != null)
            closeable.close();
    }

    private FeignException.NotFound buildFeignNotFound() {
        return new FeignException.NotFound(
                "Not Found",
                Request.create(Request.HttpMethod.GET, "/test", Collections.emptyMap(), null, null, null),
                null,
                Collections.emptyMap()
        );
    }

    @Test
    public void testCreateRefundRequest_Successful() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo válido");

        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.clientId()).thenReturn(CLIENT_ID);
        when(mockReservation.status()).thenReturn("PAID");
        when(mockReservation.eventId()).thenReturn(EVENT_ID);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);

        EventStateResponse mockEvent = mock(EventStateResponse.class);
        when(mockEvent.dateTime()).thenReturn(LocalDateTime.now().plusDays(5)); // Más de 72 hs

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);
        when(catalogClient.getEventById(EVENT_ID)).thenReturn(mockEvent);

        RefundRequest savedEntity = mock(RefundRequest.class);
        when(savedEntity.getId()).thenReturn(REFUND_REQUEST_ID);
        when(refundRequestRepository.save(any(RefundRequest.class))).thenReturn(savedEntity);

        UUID result = service.createRefundRequest(request, CLIENT_ID);

        Assert.assertEquals(result, REFUND_REQUEST_ID);
        verify(ticketingClient, times(1)).setRequestRefund(RESERVATION_ID);
        verify(refundRequestRepository, times(1)).save(any(RefundRequest.class));
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateRefundRequest_ReservationNotFound_ThrowsResourceNotFoundException() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        when(ticketingClient.getReservationById(RESERVATION_ID)).thenThrow(buildFeignNotFound());

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCreateRefundRequest_TicketingError_ThrowsServiceUnavailableException() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        when(ticketingClient.getReservationById(RESERVATION_ID)).thenThrow(new RuntimeException("Ticketing Down"));

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test(expectedExceptions = UnauthorizedRefundRequestException.class)
    public void testCreateRefundRequest_ReservationNotBelongingToClient_ThrowsUnauthorized() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.clientId()).thenReturn(OTHER_CLIENT_ID);

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test(expectedExceptions = InvalidRefundStatusException.class)
    public void testCreateRefundRequest_ReservationNotPaid_ThrowsInvalidRefundStatus() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.clientId()).thenReturn(CLIENT_ID);
        when(mockReservation.status()).thenReturn("PENDING");

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateRefundRequest_EventNotFound_ThrowsResourceNotFoundException() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.clientId()).thenReturn(CLIENT_ID);
        when(mockReservation.status()).thenReturn("PAID");
        when(mockReservation.eventId()).thenReturn(EVENT_ID);

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);
        when(catalogClient.getEventById(EVENT_ID)).thenThrow(buildFeignNotFound());

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCreateRefundRequest_CatalogError_ThrowsServiceUnavailableException() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.clientId()).thenReturn(CLIENT_ID);
        when(mockReservation.status()).thenReturn("PAID");
        when(mockReservation.eventId()).thenReturn(EVENT_ID);

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);
        when(catalogClient.getEventById(EVENT_ID)).thenThrow(new RuntimeException("Catalog Down"));

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test(expectedExceptions = UnprocessableEntityException.class)
    public void testCreateRefundRequest_Under72Hours_ThrowsUnprocessableEntityException() {
        CreateRefundRequest request = new CreateRefundRequest(RESERVATION_ID, "Motivo");
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.clientId()).thenReturn(CLIENT_ID);
        when(mockReservation.status()).thenReturn("PAID");
        when(mockReservation.eventId()).thenReturn(EVENT_ID);

        EventStateResponse mockEvent = mock(EventStateResponse.class);
        when(mockEvent.dateTime()).thenReturn(LocalDateTime.now().plusHours(24)); // Menos de 72 hs

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);
        when(catalogClient.getEventById(EVENT_ID)).thenReturn(mockEvent);

        service.createRefundRequest(request, CLIENT_ID);
    }

    @Test
    public void testAcceptRefund_Successful() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);
        when(mockReservation.paymentId()).thenReturn(PAYMENT_ID);
        when(mockReservation.totalPrice()).thenReturn(BigDecimal.valueOf(10000));

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);

        service.acceptRefund(REFUND_REQUEST_ID, ADMIN_ID);

        verify(mockRefund, times(1)).accept(ADMIN_ID);
        verify(ticketingClient, times(1)).acceptRefund(RESERVATION_ID);
        verify(paymentClient, times(1)).refundPayment(any(UUID.class));
        verify(refundRequestRepository, times(1)).save(mockRefund);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testAcceptRefund_PaymentOrTicketingFails_ThrowsServiceUnavailableException() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);
        when(mockReservation.paymentId()).thenReturn(PAYMENT_ID);
        when(mockReservation.totalPrice()).thenReturn(BigDecimal.valueOf(10000));

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);
        doThrow(new RuntimeException("Payment service down")).when(paymentClient).refundPayment(any());

        service.acceptRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }

    @Test
    public void testRejectRefund_Successful() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);

        service.rejectRefund(REFUND_REQUEST_ID, ADMIN_ID);

        verify(mockRefund, times(1)).reject(ADMIN_ID);
        verify(ticketingClient, times(1)).rejectRefund(RESERVATION_ID);
        verify(refundRequestRepository, times(1)).save(mockRefund);
        verify(paymentClient, never()).refundPayment(any());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testRejectRefund_TicketingFails_ThrowsServiceUnavailableException() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenReturn(mockReservation);
        doThrow(new RuntimeException("Ticketing down")).when(ticketingClient).rejectRefund(RESERVATION_ID);

        service.rejectRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }

    @Test
    public void testCancelEvent_Successful() {
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);
        when(mockReservation.paymentId()).thenReturn(PAYMENT_ID);
        when(mockReservation.totalPrice()).thenReturn(BigDecimal.valueOf(5000));

        when(ticketingClient.getReservationsByEventId(EVENT_ID, "PAID"))
                .thenReturn(List.of(mockReservation));

        service.cancelEvent(EVENT_ID);

        verify(catalogClient, times(1)).cancelEvent(EVENT_ID);
        verify(paymentClient, times(1)).bulkRefund(anyList());
        verify(ticketingClient, times(1)).bulkCancelReservations(EVENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCancelEvent_EventNotFound_ThrowsResourceNotFoundException() {
        doThrow(buildFeignNotFound()).when(catalogClient).cancelEvent(EVENT_ID);

        service.cancelEvent(EVENT_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_PaymentBulkRefundFails_ThrowsServiceUnavailableException() {
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);

        when(ticketingClient.getReservationsByEventId(EVENT_ID, "PAID"))
                .thenReturn(List.of(mockReservation));
        doThrow(new RuntimeException("Payment bulk refund error")).when(paymentClient).bulkRefund(anyList());

        service.cancelEvent(EVENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAcceptRefund_ReservationNotFound_ThrowsResourceNotFoundException() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenThrow(buildFeignNotFound());

        service.acceptRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testAcceptRefund_TicketingGenericError_ThrowsServiceUnavailableException() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenThrow(new RuntimeException("Ticketing service down"));

        service.acceptRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testRejectRefund_ReservationNotFound_ThrowsResourceNotFoundException() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenThrow(buildFeignNotFound());

        service.rejectRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testRejectRefund_TicketingGenericError_ThrowsServiceUnavailableException() {
        RefundRequest mockRefund = mock(RefundRequest.class);
        when(mockRefund.getReservationId()).thenReturn(RESERVATION_ID);
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.of(mockRefund));

        when(ticketingClient.getReservationById(RESERVATION_ID)).thenThrow(new RuntimeException("Ticketing service down"));

        service.rejectRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_CatalogGenericError_ThrowsServiceUnavailableException() {
        doThrow(new RuntimeException("Catalog error")).when(catalogClient).cancelEvent(EVENT_ID);

        service.cancelEvent(EVENT_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_TicketingGetReservationsFails_ThrowsServiceUnavailableException() {
        when(ticketingClient.getReservationsByEventId(EVENT_ID, "PAID"))
                .thenThrow(new RuntimeException("Ticketing error"));

        service.cancelEvent(EVENT_ID);
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void testCancelEvent_TicketingBulkCancelFails_ThrowsServiceUnavailableException() {
        ReservationStateResponse mockReservation = mock(ReservationStateResponse.class);
        when(mockReservation.reservationId()).thenReturn(RESERVATION_ID);

        when(ticketingClient.getReservationsByEventId(EVENT_ID, "PAID"))
                .thenReturn(List.of(mockReservation));
        doThrow(new RuntimeException("Ticketing bulk cancel error"))
                .when(ticketingClient).bulkCancelReservations(EVENT_ID);

        service.cancelEvent(EVENT_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testAcceptRefund_RefundRequestNotFound_ThrowsResourceNotFoundException() {
        when(refundRequestRepository.findById(REFUND_REQUEST_ID)).thenReturn(Optional.empty());

        service.acceptRefund(REFUND_REQUEST_ID, ADMIN_ID);
    }
}
