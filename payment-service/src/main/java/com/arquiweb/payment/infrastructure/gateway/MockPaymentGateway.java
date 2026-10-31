package com.arquiweb.payment.infrastructure.gateway;

import com.arquiweb.payment.dtos.GatewayResponse;
import com.arquiweb.payment.enums.TransactionStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway{
    @Override
    public GatewayResponse processPayment(String cardToken, BigDecimal amount) {
        GatewayResponse gr;
        String RUUID = UUID.randomUUID().toString();
        switch (cardToken){
            case "APPROVED": gr = createGatewayResponse(RUUID, TransactionStatus.APPROVED, "Payment Approved");
            break;
            default: gr = createGatewayResponse(RUUID, TransactionStatus.REJECTED, "Payment Declined");

        }
        return gr;
    }

    private GatewayResponse createGatewayResponse(String transactionId, TransactionStatus status, String message){
        return new GatewayResponse(status, transactionId, message);
    }

}
