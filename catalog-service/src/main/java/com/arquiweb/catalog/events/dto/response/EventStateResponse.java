package com.arquiweb.catalog.events.dto.response;

import com.arquiweb.catalog.events.domain.Event;

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

    public static EventStateResponse fromDomain(Event event) {
        return new EventStateResponse(
                event.getId(),
                event.getName(),
                event.getType().toString(),
                event.getDateTime(),
                event.getOrganizerId(),
                event.getCity()
        );
    }

}
