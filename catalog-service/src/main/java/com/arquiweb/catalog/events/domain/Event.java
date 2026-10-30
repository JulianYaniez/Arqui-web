package com.arquiweb.catalog.events.domain;

import com.arquiweb.catalog.events.domain.enums.EventStatus;
import com.arquiweb.catalog.events.domain.enums.EventType;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.domain.Venue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    private String id;

    private String name;
    private String description;
    private EventType type;
    private EventStatus status;
    private LocalDateTime dateTime;
    private String organizerId;
    private List<String> participants;
    private String venueId;
    private String city;
    private List<EventSector> sectors;

    public Event(
            String name,
            String description,
            EventType type,
            LocalDateTime dateTime,
            String organizerId,
            Venue venue,
            List<EventSector> sectors
    ) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.isBlank()) errors.put("name", "Event name cannot be null or blank.");
        if (description == null || description.isBlank()) errors.put("description", "Event description cannot be null or blank.");
        if (type == null) errors.put("type", "Event type cannot be null.");
        if (dateTime == null) errors.put("dateTime", "Event date cannot be null.");
        if (dateTime != null && dateTime.isBefore(LocalDateTime.now())) errors.put("dateTime", "Event date cannot be before current time.");
        if (organizerId == null) errors.put("organizerId", "Event organizer cannot be null.");
        // Venue comes from DB -> Trustful
        if (venue == null) errors.put("venue", "Event venue cannot be null.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given event has invalid field/s.", errors);

        this.name = name;
        this.description = description;
        this.type = type;
        this.dateTime = dateTime;
        this.organizerId = organizerId;
        this.venueId = venue.getId();
        this.city = venue.getCity();
        this.sectors = sectors != null ? sectors : Collections.emptyList();
    }
}
