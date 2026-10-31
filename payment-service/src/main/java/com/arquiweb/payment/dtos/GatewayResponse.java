package com.arquiweb.payment.dtos;

import com.arquiweb.payment.enums.TransactionStatus;

public record GatewayResponse(
        TransactionStatus ts,
        String externalTransactionId,
        String message

) {
}
