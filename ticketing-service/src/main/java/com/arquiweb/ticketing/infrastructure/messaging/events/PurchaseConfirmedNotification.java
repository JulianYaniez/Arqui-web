package com.arquiweb.ticketing.infrastructure.messaging.events;

import java.util.List;
import java.util.UUID;

public record PurchaseConfirmedNotification (
        UUID userId,
        UUID paymentId,
        List<String> qrCodes
){
}
