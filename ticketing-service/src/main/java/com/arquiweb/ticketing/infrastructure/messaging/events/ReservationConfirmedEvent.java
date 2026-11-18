package com.arquiweb.ticketing.infrastructure.messaging.events;

import java.util.UUID;

public record ReservationConfirmedEvent(
        UUID userId,
        String eventId
) {
}
