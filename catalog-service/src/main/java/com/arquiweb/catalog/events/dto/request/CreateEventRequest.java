package com.arquiweb.catalog.events.dto.request;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.events.domain.enums.EventType;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CreateEventRequest(
    String name,
    String description,
    EventType type,
    LocalDateTime dateTime,
    // Remove later. Should be acquired through JWT
    String organizerId,
    List<String> participants,
    String venueId,
    List<CreateEventSectorRequest> sectors
) {

    public CreateEventRequest {
        Map<String, String> errors = new HashMap<>();

        if (venueId == null || venueId.isBlank()) errors.put("venueId", "Venue ID cannot be null.");
        if (venueId != null && ObjectId.isValid(venueId)) errors.put("venueId", "Venue ID must have a valid format.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given venue has invalid field/s.", errors);
    }

    // Add organizerId later
    public Event toDomain(VenueResponse venue, List<EventSector> sectors) {
        return new Event(
                this.name,
                this.description,
                this.type,
                this.dateTime,
                // Replace for method param later
                this.organizerId,
                venue,
                sectors
        );
    }

}
