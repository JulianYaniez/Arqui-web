package com.arquiweb.management.infrastructure.client.dto.requests;

import java.time.LocalDateTime;

public record EventStateResponse(
        String eventId,
        String name,
        String type,
        LocalDateTime dateTime,
        String organizerId,
        String city
) {
}
