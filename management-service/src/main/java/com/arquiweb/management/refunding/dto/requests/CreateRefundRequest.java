package com.arquiweb.management.refunding.dto.requests;

import java.util.UUID;

public record CreateRefundRequest(
        UUID reservationId,
        String reason
) {
}
