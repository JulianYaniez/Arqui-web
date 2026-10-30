package com.arquiweb.catalog.events.domain;

import com.arquiweb.catalog.events.domain.enums.EventStatus;
import com.arquiweb.catalog.events.domain.enums.EventType;
import com.arquiweb.catalog.events.domain.exceptions.IllegalEventStateException;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
            List<String> participants,
            VenueResponse venue,
            List<EventSector> sectors
    ) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.isBlank()) errors.put("name", "Event name cannot be null or blank.");
        if (description == null || description.isBlank()) errors.put("description", "Event description cannot be null or blank.");
        if (type == null) errors.put("type", "Event type cannot be null.");
        if (dateTime == null) errors.put("dateTime", "Event date cannot be null.");
        if (dateTime != null && dateTime.isBefore(LocalDateTime.now())) errors.put("dateTime", "Event date cannot be before current time.");
        if (organizerId == null) errors.put("organizerId", "Event organizer cannot be null.");
        if (participants == null) errors.put("participants", "Event participants cannot be null.");
        if (participants != null) {
            if (participants.isEmpty()) errors.put("participants", "Event participants cannot be empty.");

            Set<String> processedParticipants = new HashSet<>();
            for (String participant : participants) {
                if (!processedParticipants.add(participant)) errors.put("participants", "Event should not have duplicated participants:  " + participant);
            }
        }

        // Venue comes from DB -> Trustful
        if (venue == null) errors.put("venue", "Event venue cannot be null.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given event has invalid field/s.", errors);

        this.name = name;
        this.description = description;
        this.type = type;
        this.status = EventStatus.DRAFT;
        this.dateTime = dateTime;
        this.organizerId = organizerId;
        this.participants = participants;
        this.venueId = venue.id();
        this.city = venue.city();
        this.sectors = sectors != null ? sectors : Collections.emptyList();
    }

    public boolean addSector(EventSector sector) {

        if (this.status != EventStatus.DRAFT) throw new IllegalEventStateException("Only DRAFT events can add new sectors.");

        if (this.sectors.contains(sector)) return false;
        this.sectors.add(sector);
        return true;
    }


    public void publish() {
        if (this.status != EventStatus.DRAFT) throw new IllegalEventStateException("Only DRAFT events can be published.");

        if (this.sectors.isEmpty()) throw new IllegalEventStateException("Events cannot be published without assigned sector/s.");

        this.status = EventStatus.PUBLISHED;
    }
}
