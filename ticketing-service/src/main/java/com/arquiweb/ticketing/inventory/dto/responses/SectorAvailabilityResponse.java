package com.arquiweb.ticketing.inventory.dto.responses;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SectorAvailabilityResponse(
        String eventId,
        String sectorId,
        BigDecimal price,
        boolean isNumbered,

        Integer capacity,

        Map<Integer, SeatResponse> rows
) {

    public static SectorAvailabilityResponse fromCapacity(SectorCapacity capacity) {
        return new SectorAvailabilityResponse(
                capacity.getEventId(),
                capacity.getSectorId(),
                capacity.getPrice(),
                false,
                capacity.getAvailableCapacity(),
                null
        );
    }

    public static SectorAvailabilityResponse fromSeat(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) return null;
        Ticket seat = tickets.get(0);

        return new SectorAvailabilityResponse(
                seat.getEventId(),
                seat.getSectorId(),
                seat.getPrice(),
                true,
                null,
                tickets.stream().
                        collect(Collectors.toMap(
                                Ticket::getRowNumber,
                                SeatResponse::fromDomain
                        ))
        );
    }

}
