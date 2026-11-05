import com.arquiweb.payment.dtos.GatewayResponse;
import com.arquiweb.payment.dtos.SavePaymentDTO;
import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.TransactionStatus;
import com.arquiweb.payment.infrastructure.client.TicketingClient;
import com.arquiweb.payment.infrastructure.exception.InvalidInputException;
import com.arquiweb.payment.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.payment.infrastructure.gateway.PaymentGateway;
import com.arquiweb.payment.repositori.interfaces.PaymentRepository;
import com.arquiweb.payment.service.PaymentTransactionService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
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
}