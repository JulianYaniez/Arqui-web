package com.arquiweb.catalog.infrastructure.messaging;

import com.arquiweb.catalog.infrastructure.messaging.events.FavoriteArtistNotification;
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

    public static final String TOPIC_FAVORITE_ARTIST = "favorite-artist-published";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishFavoriteArtistEvent(List<UUID> userIds, String eventId, String eventName, List<String> artists, String city) {
        var payload = new FavoriteArtistNotification(userIds, eventId, eventName, artists, city);
    }

}
