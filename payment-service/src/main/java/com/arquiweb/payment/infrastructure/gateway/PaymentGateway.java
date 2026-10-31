package com.arquiweb.payment.infrastructure.gateway;

import com.arquiweb.payment.dtos.GatewayResponse;

import java.math.BigDecimal;

public interface PaymentGateway {
    GatewayResponse processPayment(String cardToken, BigDecimal amount);
}
