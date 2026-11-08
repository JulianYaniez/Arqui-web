import com.arquiweb.payment.dtos.GatewayResponse;
import com.arquiweb.payment.dtos.SavePaymentDTO;
import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.TransactionStatus;
import com.arquiweb.payment.infrastructure.client.TicketingClient;
import com.arquiweb.payment.infrastructure.exception.InvalidInputException;
import com.arquiweb.payment.infrastructure.exception.PaymentGatewayException;
import com.arquiweb.payment.infrastructure.exception.ReservationReleaseException;
import com.arquiweb.payment.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.payment.infrastructure.gateway.PaymentGateway;
import com.arquiweb.payment.repositori.interfaces.PaymentRepository;
import com.arquiweb.payment.service.PaymentTransactionService;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

public class PaymentTransactionServiceTest {

    private final UUID VALID_RESERVATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID VALID_TRANSACTION_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private PaymentTransactionService paymentTransactionService;

    @Mock
    private PaymentRepository  paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private TicketingClient ticketingClient;

    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(paymentRepository, paymentGateway, ticketingClient);
        paymentTransactionService = new PaymentTransactionService(paymentRepository, paymentGateway, ticketingClient);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    private FeignException buildFeignInternalServerError() {
        return new FeignException.InternalServerError(
                "Internal Server Error",
                Request.create(Request.HttpMethod.POST, "/release", Collections.emptyMap(), null, null, null),
                null,
                Collections.emptyMap()
        );
    }

    @Test
    public void testProcessPayment_Successful() throws TimeoutException {
        // Arrange
        String cardToken = "token-xyz-123";
        BigDecimal amount = BigDecimal.valueOf(5000);
        String externalTxId = "ext-gateway-999";

        SavePaymentDTO request = mock(SavePaymentDTO.class);

        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.cardToken()).thenReturn(cardToken);
        when(request.amount()).thenReturn(amount);

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(any(UUID.class), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);

        when(gatewayResponse.ts()).thenReturn(TransactionStatus.APPROVED);
        when(gatewayResponse.externalTransactionId()).thenReturn(externalTxId);
        when(paymentGateway.processPayment(eq(cardToken), eq(amount))).thenReturn(gatewayResponse);

        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        // Act
        PaymentTransaction result = paymentTransactionService.processPayment(request);

        // 3. Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result, mockPt);

        verify(paymentRepository, times(1)).isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED));
        verify(paymentRepository, times(1)).save(mockPt);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_AlreadyPaid_ThrowsInvalidInputException() throws TimeoutException {
        // Arrange
        SavePaymentDTO request = mock(SavePaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);

        when(paymentRepository.isPaymentProcessedAndStatus(any(UUID.class), eq(TransactionStatus.APPROVED)))
                .thenReturn(true);

        // Act
        paymentTransactionService.processPayment(request);

        // Assert
        verify(paymentGateway, never()).processPayment(any(), any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    public void testIsPaymentProcessedAndStatus_ReturnsTrue() {
        // Arrange
        when(paymentRepository.isPaymentProcessedAndStatus(any(UUID.class), eq(TransactionStatus.APPROVED)))
                .thenReturn(true);

        // Act
        boolean result = paymentTransactionService.isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED);

        // Assert
        Assert.assertTrue(result);
        verify(paymentRepository, times(1)).isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED);
    }

    @Test
    public void testIsPaymentProcessedAndStatus_ReturnsFalse() {
        // Arrange
        when(paymentRepository.isPaymentProcessedAndStatus(any(UUID.class), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        // Act
        boolean result = paymentTransactionService.isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED);

        // Assert
        Assert.assertFalse(result);
        verify(paymentRepository, times(1)).isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED);
    }

    @Test
    public void testGetById_Exists_ReturnsTransaction() {
        // Arrange
        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(paymentRepository.getPaymentTransactionById(any(UUID.class))).thenReturn(Optional.of(mockPt));

        // Act
        PaymentTransaction result = paymentTransactionService.getById(VALID_TRANSACTION_ID);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result, mockPt);
        verify(paymentRepository, times(1)).getPaymentTransactionById(VALID_TRANSACTION_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetById_NoExists_ThrowsResourceNotFoundException() {
        // Arrange
        when(paymentRepository.getPaymentTransactionById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        paymentTransactionService.getById(VALID_TRANSACTION_ID);

    }

    @Test
    public void testProcessPayment_GatewayRejection() throws TimeoutException {

        // Arrange
        String cardToken = "token-xyz-123";
        BigDecimal amount = BigDecimal.valueOf(5000);

        SavePaymentDTO request = mock(SavePaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.cardToken()).thenReturn(cardToken);
        when(request.amount()).thenReturn(amount);

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(any(UUID.class), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(TransactionStatus.REJECTED);
        when(gatewayResponse.externalTransactionId()).thenReturn("ext-rejected-123");
        when(paymentGateway.processPayment(eq(cardToken), eq(amount))).thenReturn(gatewayResponse);

        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        // Act
        paymentTransactionService.processPayment(request);

        // Assert
        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test
    public void testProcessPayment_GatewayTimeout() throws TimeoutException {

        // Arrange
        String cardToken = "token-xyz-123";
        BigDecimal amount = BigDecimal.valueOf(5000);

        SavePaymentDTO request = mock(SavePaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.cardToken()).thenReturn(cardToken);
        when(request.amount()).thenReturn(amount);

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(any(UUID.class), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        when(paymentGateway.processPayment(eq(cardToken), eq(amount)))
                .thenThrow(new TimeoutException("Gateway connection timed out"));

        // Act
        try {
            paymentTransactionService.processPayment(request);
        } catch (Exception ignored) {
        }

        // Assert
        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test
    public void testRefundTransaction_Successful() {

        // Arrange
        String externalTxId = "ext-gateway-999";
        BigDecimal amount = BigDecimal.valueOf(5000);
        boolean simulateFail = false;

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.getExternalGateweyTransactionId()).thenReturn(externalTxId);
        when(mockPt.getAmount()).thenReturn(amount);
        when(mockPt.getReservationId()).thenReturn(VALID_RESERVATION_ID);

        when(paymentRepository.getPaymentTransactionById(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));
        when(paymentGateway.refund(externalTxId, amount, simulateFail))
                .thenReturn(true);
        when(paymentRepository.save(mockPt)).thenReturn(mockPt);

        // Act
        paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, simulateFail);

        // Assert
        verify(paymentRepository, times(1)).getPaymentTransactionById(VALID_TRANSACTION_ID);
        verify(paymentGateway, times(1)).refund(externalTxId, amount, simulateFail);
        verify(mockPt, times(1)).setStatus(TransactionStatus.REFUNDED);
        verify(paymentRepository, times(1)).save(mockPt);
        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test(expectedExceptions = PaymentGatewayException.class)
    public void testRefundTransaction_GatewayFails_() {

        // Arrange
        String externalTxId = "ext-gateway-999";
        BigDecimal amount = BigDecimal.valueOf(5000);
        Boolean simulateFail = true;

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.getExternalGateweyTransactionId()).thenReturn(externalTxId);
        when(mockPt.getAmount()).thenReturn(amount);

        when(paymentRepository.getPaymentTransactionById(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));
        when(paymentGateway.refund(externalTxId, amount, simulateFail))
                .thenReturn(false);

        // Act
        try {
            paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, simulateFail);
        } finally {
            // Assert: It should not remain in the REFUNDED state, nor should the reservation be released, if the gateway failed.
            verify(mockPt, never()).setStatus(TransactionStatus.REFUNDED);
            verify(paymentRepository, never()).save(any());
            verify(ticketingClient, never()).releaseReservations(any());
        }
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testRefundTransaction_NotFound() {
        // Arrange
        when(paymentRepository.getPaymentTransactionById(VALID_TRANSACTION_ID))
                .thenReturn(Optional.empty());

        // Act
        paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, false);

        // Assert
        verify(paymentGateway, never()).refund(any(), any(), any());
        verify(ticketingClient, never()).releaseReservations(any());
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_GatewayReturnsNull_ThrowsInvalidInputException() throws TimeoutException {
        SavePaymentDTO request = mock(SavePaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.cardToken()).thenReturn("token-123");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));
        when(request.toDomain()).thenReturn(mock(PaymentTransaction.class));

        when(paymentRepository.isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED))
                .thenReturn(false);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class)))
                .thenReturn(null);

        paymentTransactionService.processPayment(request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_GatewayStatusNull_ThrowsInvalidInputException() throws TimeoutException {
        SavePaymentDTO request = mock(SavePaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.cardToken()).thenReturn("token-123");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));
        when(request.toDomain()).thenReturn(mock(PaymentTransaction.class));

        when(paymentRepository.isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(null);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class)))
                .thenReturn(gatewayResponse);

        paymentTransactionService.processPayment(request);
    }

    @Test(expectedExceptions = ReservationReleaseException.class)
    public void testProcessPayment_GatewayRejection_TicketingThrowsFeignException() throws TimeoutException {
        SavePaymentDTO request = mock(SavePaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.cardToken()).thenReturn("token-123");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(TransactionStatus.REJECTED);
        when(gatewayResponse.externalTransactionId()).thenReturn("ext-123");
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class)))
                .thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        doThrow(buildFeignInternalServerError()).when(ticketingClient).releaseReservations(VALID_RESERVATION_ID);

        paymentTransactionService.processPayment(request);
    }

    @Test
    public void testFillAndSave_Successful() {
        PaymentTransaction pt = mock(PaymentTransaction.class);
        LocalDateTime now = LocalDateTime.now();
        String externalTxId = "ext-tx-123";

        when(paymentRepository.save(pt)).thenReturn(pt);

        PaymentTransaction result = paymentTransactionService.fillAndSave(
                TransactionStatus.APPROVED,
                externalTxId,
                now,
                pt
        );

        Assert.assertNotNull(result);
        verify(pt, times(1)).setStatus(TransactionStatus.APPROVED);
        verify(pt, times(1)).setExternalGateweyTransactionId(externalTxId);
        verify(pt, times(1)).setProcessedAt(now);
        verify(paymentRepository, times(1)).save(pt);
    }
}