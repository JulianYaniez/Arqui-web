package com.arquiweb.ticketing.queue.dto.response;

import com.arquiweb.ticketing.queue.domain.enums.QueueStatus;

public record QueueStatusResponse(
        QueueStatus status,
        Long position,
        Long expiresInSeconds
) {
}
