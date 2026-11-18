package com.arquiweb.payment.infrastructure.gateway;

import com.arquiweb.payment.infrastructure.gateway.dto.response.GatewayResponse;
import com.arquiweb.payment.transaction.domain.enums.PaymentMethod;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


@Component
public class MockPaymentGateway implements PaymentGateway{

    private final List<String> validPrefixes = List.of("4111", "4242", "5555", "3782");
    @Override
    public GatewayResponse processPayment(String cardToken, BigDecimal amount, PaymentMethod paymentMethod) throws TimeoutException {
        GatewayResponse gr;
        String RUUID = UUID.randomUUID().toString();
        if(paymentMethod == PaymentMethod.CREDIT_CARD){
            String Prefix = cardToken.substring(0, 4);
            if(validPrefixes.contains(Prefix)) gr = createGatewayResponse(RUUID, TransactionStatus.APPROVED, "Payment Approved");
            else gr = createGatewayResponse(RUUID, TransactionStatus.REJECTED, "Invalid card token.");
        }else {
            gr = switch (cardToken) {
                case "APPROVED" -> createGatewayResponse(RUUID, TransactionStatus.APPROVED, "Payment Approved");
                case "DECLINED" -> createGatewayResponse(RUUID, TransactionStatus.REJECTED, "Payment Declined");
                case "TIMEOUT" -> throw new TimeoutException("Gateway Timeout");
                default -> createGatewayResponse(RUUID, TransactionStatus.REJECTED, "Invalid card token.");
            };
        }

        return gr;
    }

    @Override
    public boolean refund(String transactionId, BigDecimal amount, boolean gatewaySuccess) {
        return !gatewaySuccess;
    }

    private GatewayResponse createGatewayResponse(String transactionId, TransactionStatus status, String message){
        return new GatewayResponse(status, transactionId, message);
    }

}
