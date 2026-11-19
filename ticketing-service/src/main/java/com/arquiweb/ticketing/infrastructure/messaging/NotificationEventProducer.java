package com.arquiweb.ticketing.infrastructure.messaging;

import com.arquiweb.ticketing.infrastructure.messaging.events.EventChangedNotification;
import com.arquiweb.ticketing.infrastructure.messaging.events.PurchaseConfirmedNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    public static final String TOPIC_PURCHASE_CONFIRMED = "purchase-confirmed";
    public static final String TOPIC_EVENT_CHANGED = "event-changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPurchaseConfirmed(UUID userId, UUID paymentId, List<String> qrCodes) {
        var payload = new PurchaseConfirmedNotification(userId, paymentId, qrCodes);
        kafkaTemplate.send(TOPIC_PURCHASE_CONFIRMED, userId.toString(), payload);
        log.info("Purchase confirmation notification sent -> userId : {}, paymentId : {}", userId, paymentId);
    }

    public void publishEventChanged(List<UUID> userIds, String eventId, String changeType) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        var payload = new EventChangedNotification(userIds, eventId, changeType);
        kafkaTemplate.send(TOPIC_EVENT_CHANGED, eventId, payload);
        log.info("Event change {} notification sent -> eventId : {}, affected : {}", changeType, eventId, userIds.size());

    }

}
