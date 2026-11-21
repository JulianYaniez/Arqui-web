package com.arquiweb.ticketing.infrastructure.messaging;

import com.arquiweb.ticketing.infrastructure.messaging.events.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private static final String TOPIC = "reservation-events";
    private final KafkaTemplate<String, ReservationConfirmedEvent> kafkaTemplate;

    public void publishReservationConfirmed(UUID userId, String eventId) {
        ReservationConfirmedEvent payload = new ReservationConfirmedEvent(userId, eventId);
        kafkaTemplate.send(TOPIC, userId.toString(), payload);
    }

}
