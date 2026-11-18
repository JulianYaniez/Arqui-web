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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.PessimisticLockingFailureException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PaymentTransactionServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private TicketingClient ticketingClient;

    private PaymentTransactionService paymentService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(paymentRepository, paymentGateway, ticketingClient);
        paymentService = new PaymentTransactionService(paymentRepository, paymentGateway, ticketingClient);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testProcessPayment_CreditCardApproved_Successful() throws Exception {
        UUID reservationId = UUID.randomUUID();
        String validToken = "4111222233334444";
        BigDecimal amount = BigDecimal.valueOf(5000);

        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn(validToken);
        when(request.amount()).thenReturn(amount);

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);

        PaymentTransaction domainTx = new PaymentTransaction();
        domainTx.setId(UUID.randomUUID());
        domainTx.setReservationId(reservationId);
        when(request.toDomain()).thenReturn(domainTx);

        GatewayResponse gatewayResponse = new GatewayResponse(TransactionStatus.APPROVED, "ext-tx-123", "aproved");
        when(paymentGateway.processPayment(validToken, amount, PaymentMethod.CREDIT_CARD)).thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentTransaction result = paymentService.processPayment(request);

        Assert.assertEquals(result.getStatus(), TransactionStatus.APPROVED);
        Assert.assertEquals(result.getExternalGatewayTransactionId(), "ext-tx-123");
        verify(ticketingClient, never()).releaseReservations(any());
        verify(paymentRepository, times(1)).save(domainTx);
    }

    @Test
    public void testProcessPayment_NonCreditCardApproved_SkipsPrefixCheck() throws Exception {
        UUID reservationId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(3000);

        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.MERCADO_PAGO_BITE);
        when(request.cardToken()).thenReturn("0000");
        when(request.amount()).thenReturn(amount);

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);

        PaymentTransaction domainTx = new PaymentTransaction();
        domainTx.setReservationId(reservationId);
        when(request.toDomain()).thenReturn(domainTx);

        GatewayResponse gatewayResponse = new GatewayResponse(TransactionStatus.APPROVED, "ext-tx-456", "aproved");
        when(paymentGateway.processPayment("0000", amount, PaymentMethod.MERCADO_PAGO_BITE)).thenReturn(gatewayResponse);
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentTransaction result = paymentService.processPayment(request);

        Assert.assertEquals(result.getStatus(), TransactionStatus.APPROVED);
        verify(ticketingClient, never()).releaseReservations(any());
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_AlreadyApproved_ThrowsInvalidInputException() {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(true);

        paymentService.processPayment(request);
    }

    @Test(expectedExceptions = InvalidCardPrefixException.class)
    public void testProcessPayment_InvalidCreditCardPrefix_ThrowsInvalidCardPrefixException() {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("9999111122223333");

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);

        paymentService.processPayment(request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_NullGatewayResponse_ThrowsInvalidInputException() throws Exception {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4242111122223333");
        when(request.amount()).thenReturn(BigDecimal.TEN);
        when(request.toDomain()).thenReturn(new PaymentTransaction());

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);
        when(paymentGateway.processPayment(any(), any(), any())).thenReturn(null);

        paymentService.processPayment(request);
    }

    @Test(expectedExceptions = InvalidInputException.class)
    public void testProcessPayment_NullTransactionStatusInGatewayResponse_ThrowsInvalidInputException() throws Exception {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("5555111122223333");
        when(request.amount()).thenReturn(BigDecimal.TEN);
        when(request.toDomain()).thenReturn(new PaymentTransaction());

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);
        when(paymentGateway.processPayment(any(), any(), any())).thenReturn(new GatewayResponse(null, "ext-1", "null"));

        paymentService.processPayment(request);
    }

    @Test
    public void testProcessPayment_GatewayRejected_SavesRejectedAndReleasesReservation() throws Exception {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("3782111122223333");
        when(request.amount()).thenReturn(BigDecimal.TEN);

        PaymentTransaction domainTx = new PaymentTransaction();
        domainTx.setReservationId(reservationId);
        when(request.toDomain()).thenReturn(domainTx);

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new GatewayResponse(TransactionStatus.REJECTED, "ext-rej-1", "rejected"));
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentTransaction result = paymentService.processPayment(request);

        Assert.assertEquals(result.getStatus(), TransactionStatus.REJECTED);
        verify(ticketingClient, times(1)).releaseReservations(reservationId);
    }

    @Test
    public void testProcessPayment_GatewayTimeout_SavesRejectedAndReleasesReservation() throws Exception {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111111122223333");
        when(request.amount()).thenReturn(BigDecimal.TEN);

        PaymentTransaction domainTx = new PaymentTransaction();
        domainTx.setReservationId(reservationId);
        when(request.toDomain()).thenReturn(domainTx);

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);
        when(paymentGateway.processPayment(any(), any(), any())).thenThrow(new TimeoutException("Gateway timeout"));
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentTransaction result = paymentService.processPayment(request);

        Assert.assertEquals(result.getStatus(), TransactionStatus.REJECTED);
        Assert.assertNull(result.getExternalGatewayTransactionId());
        verify(ticketingClient, times(1)).releaseReservations(reservationId);
    }

    @Test(expectedExceptions = ReservationReleaseException.class)
    public void testProcessPayment_RejectedAndTicketingFeignFails_ThrowsReservationReleaseException() throws Exception {
        UUID reservationId = UUID.randomUUID();
        RequestPaymentDTO request = mock(RequestPaymentDTO.class);
        when(request.reservationId()).thenReturn(reservationId);
        when(request.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(request.cardToken()).thenReturn("4111111122223333");
        when(request.amount()).thenReturn(BigDecimal.TEN);
        when(request.toDomain()).thenReturn(new PaymentTransaction());

        when(paymentRepository.isPaymentProcessedAndStatus(reservationId, TransactionStatus.APPROVED)).thenReturn(false);
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new GatewayResponse(TransactionStatus.REJECTED, "ext-rej", "rejected"));
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(mock(FeignException.class)).when(ticketingClient).releaseReservations(reservationId);

        paymentService.processPayment(request);
    }

    @Test
    public void testGetById_Found_ReturnsTransaction() {
        UUID id = UUID.randomUUID();
        PaymentTransaction tx = new PaymentTransaction();
        tx.setId(id);
        when(paymentRepository.getPaymentTransactionById(id)).thenReturn(Optional.of(tx));

        PaymentTransaction result = paymentService.getById(id);

        Assert.assertEquals(result.getId(), id);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetById_NotFound_ThrowsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(paymentRepository.getPaymentTransactionById(id)).thenReturn(Optional.empty());

        paymentService.getById(id);
    }

    @Test
    public void testValidCardToken_AllBranches() {
        Assert.assertTrue(paymentService.validCardToken("  4111000011112222  "));
        Assert.assertTrue(paymentService.validCardToken("4242000011112222"));
        Assert.assertTrue(paymentService.validCardToken("5555000011112222"));
        Assert.assertTrue(paymentService.validCardToken("3782000011112222"));
        Assert.assertFalse(paymentService.validCardToken("4111222"));
        Assert.assertFalse(paymentService.validCardToken("9999000011112222"));
    }

    @Test
    public void testRefundTransaction_Successful() {
        UUID paymentId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        PaymentTransaction tx = new PaymentTransaction();
        tx.setId(paymentId);
        tx.setReservationId(reservationId);
        tx.setExternalGatewayTransactionId("ext-123");
        tx.setAmount(BigDecimal.valueOf(1000));

        when(paymentRepository.getPaymentTransactionByIdWithLock(paymentId)).thenReturn(Optional.of(tx));
        when(paymentGateway.refund("ext-123", BigDecimal.valueOf(1000), false)).thenReturn(true);

        paymentService.refundTransaction(paymentId, false);

        Assert.assertEquals(tx.getStatus(), TransactionStatus.REFUNDED);
        verify(paymentRepository, times(1)).save(tx);
        verify(ticketingClient, times(1)).releaseReservations(reservationId);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testRefundTransaction_NotFound_ThrowsResourceNotFoundException() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.getPaymentTransactionByIdWithLock(paymentId)).thenReturn(Optional.empty());

        paymentService.refundTransaction(paymentId, false);
    }

    @Test(expectedExceptions = PaymentGatewayException.class)
    public void testRefundTransaction_GatewayReturnsFalse_ThrowsPaymentGatewayException() {
        UUID paymentId = UUID.randomUUID();
        PaymentTransaction tx = new PaymentTransaction();
        tx.setExternalGatewayTransactionId("ext-123");
        tx.setAmount(BigDecimal.valueOf(1000));

        when(paymentRepository.getPaymentTransactionByIdWithLock(paymentId)).thenReturn(Optional.of(tx));
        when(paymentGateway.refund("ext-123", BigDecimal.valueOf(1000), true)).thenReturn(false);

        paymentService.refundTransaction(paymentId, true);
    }

    @Test(expectedExceptions = ConflictException.class)
    public void testRefundTransaction_LockFailure_ThrowsConflictException() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.getPaymentTransactionByIdWithLock(paymentId))
                .thenThrow(new PessimisticLockingFailureException("Locked"));

        paymentService.refundTransaction(paymentId, false);
    }

    @Test
    public void testProcessBulkRefund_SuccessfulAndSkipsAlreadyRefunded() {
        UUID payId1 = UUID.randomUUID();
        UUID resId1 = UUID.randomUUID();
        PaymentRefundRequest req1 = mock(PaymentRefundRequest.class);
        when(req1.paymentId()).thenReturn(payId1);
        when(req1.reservationId()).thenReturn(resId1);
        when(req1.totalAmound()).thenReturn(BigDecimal.valueOf(2000));

        PaymentTransaction tx1 = mock(PaymentTransaction.class);
        when(tx1.isRefunded()).thenReturn(false);
        when(tx1.getExternalGatewayTransactionId()).thenReturn("ext-1");
        when(tx1.getReservationId()).thenReturn(resId1);
        when(paymentRepository.getPaymentTransactionByIdWithLock(payId1)).thenReturn(Optional.of(tx1));
        when(paymentGateway.refund("ext-1", BigDecimal.valueOf(2000), false)).thenReturn(true);

        UUID payId2 = UUID.randomUUID();
        PaymentRefundRequest req2 = mock(PaymentRefundRequest.class);
        when(req2.paymentId()).thenReturn(payId2);

        PaymentTransaction tx2 = mock(PaymentTransaction.class);
        when(tx2.isRefunded()).thenReturn(true);
        when(paymentRepository.getPaymentTransactionByIdWithLock(payId2)).thenReturn(Optional.of(tx2));

        paymentService.processBulkRefund(List.of(req1, req2));

        verify(tx1, times(1)).setStatus(TransactionStatus.REFUNDED);
        verify(paymentRepository, times(1)).save(tx1);
        verify(ticketingClient, times(1)).releaseReservations(resId1);
        verify(paymentGateway, never()).refund(eq(null), any(), anyBoolean());
    }

    @Test(expectedExceptions = ConflictException.class)
    public void testProcessBulkRefund_AnyExceptionWrappedAsConflictException() {
        UUID payId = UUID.randomUUID();
        PaymentRefundRequest req = mock(PaymentRefundRequest.class);
        when(req.paymentId()).thenReturn(payId);

        when(paymentRepository.getPaymentTransactionByIdWithLock(payId))
                .thenThrow(new PessimisticLockingFailureException("Locked"));

        paymentService.processBulkRefund(List.of(req));
    }

    @Test(expectedExceptions = ConflictException.class)
    public void testProcessBulkRefund_GatewayReturnsFalseWrappedAsConflictException() {
        UUID payId = UUID.randomUUID();
        UUID resId = UUID.randomUUID();
        PaymentRefundRequest req = mock(PaymentRefundRequest.class);
        when(req.paymentId()).thenReturn(payId);
        when(req.reservationId()).thenReturn(resId);
        when(req.totalAmound()).thenReturn(BigDecimal.valueOf(2000));

        PaymentTransaction tx = mock(PaymentTransaction.class);
        when(tx.isRefunded()).thenReturn(false);
        when(tx.getExternalGatewayTransactionId()).thenReturn("ext-1");
        when(paymentRepository.getPaymentTransactionByIdWithLock(payId)).thenReturn(Optional.of(tx));
        when(paymentGateway.refund("ext-1", BigDecimal.valueOf(2000), false)).thenReturn(false);

        paymentService.processBulkRefund(List.of(req));
    }
}