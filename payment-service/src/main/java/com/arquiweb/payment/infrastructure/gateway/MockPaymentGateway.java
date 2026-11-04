package com.arquiweb.payment.infrastructure.gateway;

import com.arquiweb.payment.dtos.GatewayResponse;
import com.arquiweb.payment.enums.TransactionStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
public class MockPaymentGateway implements PaymentGateway{
    @Override
    public GatewayResponse processPayment(String cardToken, BigDecimal amount) throws TimeoutException {
        GatewayResponse gr;
        String RUUID = UUID.randomUUID().toString();
        switch (cardToken){
            case "APPROVED": gr = createGatewayResponse(RUUID, TransactionStatus.APPROVED, "Payment Approved");
                break;
            case "DECLINED": gr = createGatewayResponse(RUUID, TransactionStatus.REJECTED, "Payment Declined");
                break;
            case "TIMEOUT": throw new TimeoutException("Timeout de la pasarela");
            default: gr = createGatewayResponse(RUUID, TransactionStatus.REJECTED, "Invalid card token.");

        }
        return gr;
    }

    private GatewayResponse createGatewayResponse(String transactionId, TransactionStatus status, String message){
        return new GatewayResponse(status, transactionId, message);
    }

}
