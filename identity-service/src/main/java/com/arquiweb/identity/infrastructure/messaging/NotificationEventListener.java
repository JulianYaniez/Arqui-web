package com.arquiweb.identity.infrastructure.messaging;

import com.arquiweb.identity.infrastructure.messaging.events.EventChangedNotification;
import com.arquiweb.identity.infrastructure.messaging.events.FavoriteArtistNotification;
import com.arquiweb.identity.infrastructure.messaging.events.PurchaseConfirmedNotification;
import com.arquiweb.identity.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "purchase-confirmed",
        groupId = "identity-notification-group",
        properties = "spring.json.value.default.type=com.arquiweb.identity.infrastructure.messaging.events.PurchaseConfirmedNotification"
    )
    public void onPurchaseConfirmed(PurchaseConfirmedNotification event) {
        notificationService.notifyPurchaseConfirmation(event);
    }


    @KafkaListener(
            topics = "event-changed",
            groupId = "identity-notification-group",
            properties = "spring.json.value.default.type=com.arquiweb.identity.infrastructure.messaging.events.EventChangedNotification"
    )
    public void onEventChanged(EventChangedNotification event) {
        notificationService.notifyEventChanged(event);
    }



    @KafkaListener(
            topics = "favorite-artist-published",
            groupId = "identity-notification-group",
            properties = "spring.json.value.default.type=com.arquiweb.identity.infrastructure.messaging.events.FavoriteArtistNotification"
    )
    public void onFavoriteArtistPublished(FavoriteArtistNotification event) {
        notificationService.notifyFavoriteArtistPublish(event);
    }
}
