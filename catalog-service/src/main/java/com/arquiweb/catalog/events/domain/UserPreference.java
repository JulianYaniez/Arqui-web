package com.arquiweb.catalog.events.domain;

import com.arquiweb.catalog.events.domain.enums.EventType;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "user_preferences")
public class UserPreference {

    @Id
    @Getter
    private UUID id;

    public UserPreference(UUID id) {
        this.id = id;
    }

    private Set<EventType> eventTypes = new HashSet<>();
    private Set<String> cities = new HashSet<>();
    private Set<String> participants = new HashSet<>();
    private Set<String> purchasedEventsIds = new HashSet<>();

    public void addFromEvent(Event event) {
        purchasedEventsIds.add(event.getId());

        if (event.getType() != null) eventTypes.add(event.getType());

        if (event.getCity() != null) cities.add(event.getCity());

        if (event.getParticipants() != null) participants.addAll(event.getParticipants());
    }

    public Set<EventType> getEventTypes() {
        return Collections.unmodifiableSet(eventTypes);
    }

    public Set<String> getCities() {
        return Collections.unmodifiableSet(cities);
    }

    public Set<String> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public Set<String> getPurchasedEventsIds() {
        return Collections.unmodifiableSet(purchasedEventsIds);
    }


}
