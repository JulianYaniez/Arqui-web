package com.arquiweb.payment.infrastructure.gateway;

import com.arquiweb.payment.infrastructure.gateway.dto.response.GatewayResponse;
import com.arquiweb.payment.transaction.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

public interface PaymentGateway {
    GatewayResponse processPayment(String cardToken, BigDecimal amount, PaymentMethod paymentMethod) throws TimeoutException;
    boolean refund(String transactionId, BigDecimal amount, boolean gatewaySuccess);

}
