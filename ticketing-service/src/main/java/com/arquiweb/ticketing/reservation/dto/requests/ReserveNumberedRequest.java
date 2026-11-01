package com.arquiweb.ticketing.reservation.dto.requests;

import java.util.List;
import java.util.UUID;

public record ReserveNumberedRequest (
        String eventId,
        List<UUID> ticketIds
){
}
