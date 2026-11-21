package com.arquiweb.identity.infrastructure.messaging.events;

import java.util.List;
import java.util.UUID;

public record FavoriteArtistNotification(
        List<UUID> usersId,
        String eventId,
        String eventName,
        List<String> artists,
        String city
) {
}
