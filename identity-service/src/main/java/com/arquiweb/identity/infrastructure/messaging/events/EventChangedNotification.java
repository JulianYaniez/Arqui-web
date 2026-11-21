package com.arquiweb.identity.infrastructure.messaging.events;

import java.util.List;
import java.util.UUID;

public record EventChangedNotification(
        List<UUID> usersId,
        String eventId,
        String changeType
) {
}
