
import com.arquiweb.payment.infrastructure.client.TicketingClient;
import com.arquiweb.payment.infrastructure.exception.*;
import com.arquiweb.payment.infrastructure.gateway.PaymentGateway;
import com.arquiweb.payment.infrastructure.gateway.dto.response.GatewayResponse;
import com.arquiweb.payment.transaction.domain.Exception.InvalidCardPrefixException;
import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.PaymentMethod;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import com.arquiweb.payment.transaction.dtos.request.PaymentRefundRequest;
import com.arquiweb.payment.transaction.dtos.request.RequestPaymentDTO;
import com.arquiweb.payment.transaction.repository.interfaces.PaymentRepository;
import com.arquiweb.payment.transaction.service.PaymentTransactionService;
import feign.FeignException;
import feign.Request;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.PessimisticLockingFailureException;
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
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

public class PaymentTransactionServiceTest {

    private final UUID VALID_RESERVATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID VALID_TRANSACTION_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private PaymentTransactionService paymentTransactionService;

    @Mock
    private PaymentRepository paymentRepository;

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
        // Arrange: Tarjeta de crédito con prefijo válido (4111) y longitud 16
        String cardToken = "4111222233334444";
        BigDecimal amount = BigDecimal.valueOf(5000);
        String externalTxId = "ext-gateway-999";

        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn(cardToken);
        when(request.amount()).thenReturn(amount);

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(TransactionStatus.APPROVED);
        when(gatewayResponse.externalTransactionId()).thenReturn(externalTxId);

        when(paymentGateway.processPayment(eq(cardToken), eq(amount), eq(PaymentMethod.CREDIT_CARD)))
                .thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        // Act
        PaymentTransaction result = paymentTransactionService.processPayment(request);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result, mockPt);
        verify(paymentRepository, times(1)).isPaymentProcessedAndStatus(VALID_RESERVATION_ID, TransactionStatus.APPROVED);
        verify(paymentRepository, times(1)).save(mockPt);
        verify(ticketingClient, never()).releaseReservations(any());
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_AlreadyPaid_ThrowsInvalidInputException() {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(true);

        paymentTransactionService.processPayment(request);
    }

    @Test(expectedExceptions = InvalidCardPrefixException.class)
    public void testProcessPayment_CreditCardInvalidPrefix_ThrowsInvalidCardPrefixException() {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("9999000011112222"); // Prefijo inválido

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        paymentTransactionService.processPayment(request);
    }

    @Test
    public void testProcessPayment_NotCreditCard_SkipsPrefixValidation() throws TimeoutException {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.MERCADO_PAGO_BITE);
        when(request.cardToken()).thenReturn("token-arbitrario");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(TransactionStatus.APPROVED);
        when(gatewayResponse.externalTransactionId()).thenReturn("ext-mp");

        when(paymentGateway.processPayment(eq("token-arbitrario"), eq(BigDecimal.valueOf(1000)), eq(PaymentMethod.MERCADO_PAGO_BITE)))
                .thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        PaymentTransaction result = paymentTransactionService.processPayment(request);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, mockPt);
        verify(paymentRepository, times(1)).save(mockPt);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_GatewayReturnsNull() throws TimeoutException {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111222233334444");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));
        when(request.toDomain()).thenReturn(mock(PaymentTransaction.class));

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class), any()))
                .thenReturn(null);

        paymentTransactionService.processPayment(request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_GatewayStatusNull() throws TimeoutException {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111222233334444");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));
        when(request.toDomain()).thenReturn(mock(PaymentTransaction.class));

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(null);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class), any()))
                .thenReturn(gatewayResponse);

        paymentTransactionService.processPayment(request);
    }

    @Test
    public void testProcessPayment_GatewayTimeout_ReleasesReservation() throws TimeoutException {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111222233334444");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class), any()))
                .thenThrow(new TimeoutException("Gateway connection timed out"));
        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        PaymentTransaction result = paymentTransactionService.processPayment(request);

        Assert.assertNotNull(result);
        verify(mockPt, times(1)).setStatus(TransactionStatus.REJECTED);
        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test
    public void testProcessPayment_GatewayRejection() throws TimeoutException {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111222233334444");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(TransactionStatus.REJECTED);
        when(gatewayResponse.externalTransactionId()).thenReturn("ext-rejected-123");
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class), any()))
                .thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        paymentTransactionService.processPayment(request);

        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test(expectedExceptions = ReservationReleaseException.class)
    public void testProcessPayment_GatewayRejection_TicketingThrowsFeignException() throws TimeoutException {
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111222233334444");
        when(request.amount()).thenReturn(BigDecimal.valueOf(1000));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(request.toDomain()).thenReturn(mockPt);

        when(paymentRepository.isPaymentProcessedAndStatus(eq(VALID_RESERVATION_ID), eq(TransactionStatus.APPROVED)))
                .thenReturn(false);

        GatewayResponse gatewayResponse = mock(GatewayResponse.class);
        when(gatewayResponse.ts()).thenReturn(TransactionStatus.REJECTED);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class), any()))
                .thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenReturn(mockPt);

        doThrow(buildFeignInternalServerError()).when(ticketingClient).releaseReservations(VALID_RESERVATION_ID);

        paymentTransactionService.processPayment(request);
    }

    @Test
    public void testGetById_Found() {
        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(paymentRepository.getPaymentTransactionById(VALID_TRANSACTION_ID)).thenReturn(Optional.of(mockPt));

        PaymentTransaction result = paymentTransactionService.getById(VALID_TRANSACTION_ID);

        Assert.assertEquals(result, mockPt);
        verify(paymentRepository, times(1)).getPaymentTransactionById(VALID_TRANSACTION_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetById_NoExists_ThrowsResourceNotFoundException() {
        when(paymentRepository.getPaymentTransactionById(VALID_TRANSACTION_ID)).thenReturn(Optional.empty());

        paymentTransactionService.getById(VALID_TRANSACTION_ID);
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
        verify(pt, times(1)).setExternalGatewayTransactionId(externalTxId);
        verify(pt, times(1)).setProcessedAt(now);
        verify(paymentRepository, times(1)).save(pt);
    }

    @Test
    public void testRefundTransaction_Successful() {
        String externalTxId = "ext-gateway-999";
        BigDecimal amount = BigDecimal.valueOf(5000);
        boolean simulateFail = false;

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.getExternalGatewayTransactionId()).thenReturn(externalTxId);
        when(mockPt.getAmount()).thenReturn(amount);
        when(mockPt.getReservationId()).thenReturn(VALID_RESERVATION_ID);

        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));
        when(paymentGateway.refund(externalTxId, amount, simulateFail))
                .thenReturn(true);
        when(paymentRepository.save(mockPt)).thenReturn(mockPt);

        paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, simulateFail);

        verify(paymentRepository, times(1)).getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID);
        verify(paymentGateway, times(1)).refund(externalTxId, amount, simulateFail);
        verify(mockPt, times(1)).setStatus(TransactionStatus.REFUNDED);
        verify(paymentRepository, times(1)).save(mockPt);
        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test(expectedExceptions = PaymentGatewayException.class)
    public void testRefundTransaction_GatewayFails_ThrowsPaymentGatewayException() {
        String externalTxId = "ext-gateway-999";
        BigDecimal amount = BigDecimal.valueOf(5000);
        boolean simulateFail = true;

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.getExternalGatewayTransactionId()).thenReturn(externalTxId);
        when(mockPt.getAmount()).thenReturn(amount);

        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));
        when(paymentGateway.refund(externalTxId, amount, simulateFail))
                .thenReturn(false);

        try {
            paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, simulateFail);
        } finally {
            verify(mockPt, never()).setStatus(TransactionStatus.REFUNDED);
            verify(paymentRepository, never()).save(any());
            verify(ticketingClient, never()).releaseReservations(any());
        }
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testRefundTransaction_NotFound() {
        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenReturn(Optional.empty());

        paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, false);
    }

    @Test(expectedExceptions = ConflictException.class)
    public void testRefundTransaction_PessimisticLockingFailure_ThrowsConflictException() {
        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenThrow(new PessimisticLockingFailureException("Database lock error"));

        paymentTransactionService.refundTransaction(VALID_TRANSACTION_ID, false);
    }

    @Test
    public void testValidCardToken_Branches() {
        Assert.assertTrue(paymentTransactionService.validCardToken("4111000011112222"));
        Assert.assertTrue(paymentTransactionService.validCardToken("4242000011112222"));
        Assert.assertTrue(paymentTransactionService.validCardToken("5555000011112222"));
        Assert.assertTrue(paymentTransactionService.validCardToken("3782000011112222"));
        Assert.assertTrue(paymentTransactionService.validCardToken("  4111000011112222  ")); // Prueba trim()

        Assert.assertFalse(paymentTransactionService.validCardToken("1234")); // Menos de 16 caracteres
        Assert.assertFalse(paymentTransactionService.validCardToken("9999000011112222")); // Prefijo inexistente
    }

    @Test
    public void testProcessBulkRefund_Successful() {
        PaymentRefundRequest req = mock(PaymentRefundRequest.class);
        when(req.paymentId()).thenReturn(VALID_TRANSACTION_ID);
        when(req.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(req.totalAmound()).thenReturn(BigDecimal.valueOf(2500));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.isRefunded()).thenReturn(false);
        when(mockPt.getExternalGatewayTransactionId()).thenReturn("ext-gateway-bulk");
        when(mockPt.getReservationId()).thenReturn(VALID_RESERVATION_ID);

        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));
        when(paymentGateway.refund("ext-gateway-bulk", BigDecimal.valueOf(2500), false))
                .thenReturn(true);

        paymentTransactionService.processBulkRefund(List.of(req));

        verify(mockPt, times(1)).setStatus(TransactionStatus.REFUNDED);
        verify(paymentRepository, times(1)).save(mockPt);
        verify(ticketingClient, times(1)).releaseReservations(VALID_RESERVATION_ID);
    }

    @Test
    public void testProcessBulkRefund_AlreadyRefunded_ContinuesWithoutProcessing() {
        PaymentRefundRequest req = mock(PaymentRefundRequest.class);
        when(req.paymentId()).thenReturn(VALID_TRANSACTION_ID);

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.isRefunded()).thenReturn(true);

        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));

        paymentTransactionService.processBulkRefund(List.of(req));

        verify(paymentGateway, never()).refund(any(), any(), anyBoolean());
        verify(paymentRepository, never()).save(any());
        verify(ticketingClient, never()).releaseReservations(any());
    }

    @Test(expectedExceptions = ConflictException.class)
    public void testProcessBulkRefund_GatewayFails_ThrowsConflictException() {
        PaymentRefundRequest req = mock(PaymentRefundRequest.class);
        when(req.paymentId()).thenReturn(VALID_TRANSACTION_ID);
        when(req.reservationId()).thenReturn(VALID_RESERVATION_ID);
        when(req.totalAmound()).thenReturn(BigDecimal.valueOf(2500));

        PaymentTransaction mockPt = mock(PaymentTransaction.class);
        when(mockPt.isRefunded()).thenReturn(false);
        when(mockPt.getExternalGatewayTransactionId()).thenReturn("ext-gateway-bulk");

        when(paymentRepository.getPaymentTransactionByIdWithLock(VALID_TRANSACTION_ID))
                .thenReturn(Optional.of(mockPt));
        when(paymentGateway.refund(anyString(), any(), eq(false)))
                .thenReturn(false);

        paymentTransactionService.processBulkRefund(List.of(req));
    }
}