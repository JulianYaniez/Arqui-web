package com.arquiweb.identity.infrastructure.messaging.events;

import java.util.List;
import java.util.UUID;

public record EventChangedNotification(
        List<UUID> userIds,
        String eventId,
        String changeType
) {
}
