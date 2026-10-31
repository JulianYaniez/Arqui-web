package com.arquiweb.catalog.events.dto.response;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.enums.EventStatus;
import com.arquiweb.catalog.events.domain.enums.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
    String id,
    String name,
    String description,
    EventType type,
    LocalDateTime dateTime,
    List<String> participants,
    EventStatus status,
    String city,
    List<EventSectorResponse> sectors
) {

    public static EventResponse fromDomain(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getType(),
                event.getDateTime(),
                event.getParticipants(),
                event.getStatus(),
                event.getCity(),
                event.getSectors().stream()
                        .map(EventSectorResponse::fromDomain)
                        .toList()
        );
    }

}
