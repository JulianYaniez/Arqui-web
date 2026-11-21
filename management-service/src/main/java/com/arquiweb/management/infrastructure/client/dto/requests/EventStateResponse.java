package com.arquiweb.management.infrastructure.client.dto.requests;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventStateResponse(
        String eventId,
        String name,
        String type,
        LocalDateTime dateTime,
        UUID organizerId,
        String city
) {
}
