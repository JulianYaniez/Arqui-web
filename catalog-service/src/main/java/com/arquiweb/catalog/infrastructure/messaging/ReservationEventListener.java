package com.arquiweb.catalog.infrastructure.messaging;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.UserPreference;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.events.repository.UserPreferenceRepository;
import com.arquiweb.catalog.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.catalog.infrastructure.messaging.events.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final EventRepository eventRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    @KafkaListener(topics = "reservation-events", groupId = "catalog-recommendation-group")
    public void onReservationConfirmed(ReservationConfirmedEvent message) {
        log.info("Event received on Catalog -> userId: {}, eventId: {}", message.userId(), message.eventId());

        Event event = eventRepository.findById(message.eventId())
                .orElse(null);

        if (event != null) {
            UserPreference preference = userPreferenceRepository.getById(message.userId())
                    .orElse(new UserPreference(message.userId()));

            preference.addFromEvent(event);
            userPreferenceRepository.save(preference);
        } else {
            log.warn("No event found with given ID.");
        }
    }

}
