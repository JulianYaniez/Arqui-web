package com.arquiweb.payment.infrastructure.gateway;

import com.arquiweb.payment.dtos.GatewayResponse;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

public interface PaymentGateway {
    GatewayResponse processPayment(String cardToken, BigDecimal amount) throws TimeoutException;

}
