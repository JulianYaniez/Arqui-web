package com.arquiweb.catalog.infrastructure.messaging.events;

import java.util.List;
import java.util.UUID;

public record FavoriteArtistNotification (
        List<UUID> userIds,
        String eventId,
        String eventName,
        List<String> artists,
        String city
) {
}
