package com.arquiweb.payment.infrastructure.gateway.dto.response;

import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;

public record GatewayResponse(
        TransactionStatus ts,
        String externalTransactionId,
        String message

) {
}
