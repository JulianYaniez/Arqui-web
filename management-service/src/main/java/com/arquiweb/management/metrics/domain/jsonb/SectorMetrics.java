package com.arquiweb.management.metrics.domain.jsonb;

import com.arquiweb.management.infrastructure.exception.InvalidInputException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public record SectorMetrics(
        String sectorId,
        long capacity,
        long soldTickets,
        long refundedTickets,
        BigDecimal revenue
) {

    public SectorMetrics {
        Map<String, String> errors = new HashMap<>();

        if (sectorId == null || sectorId.isBlank()) errors.put("sectorId", "Sector ID cannot be null or blank.");
        if (revenue == null) errors.put("revenue", "Revenue cannot be null.");
        if (capacity == 0) errors.put("capacity", "Capacity cannot be 0.");

        if (!errors.isEmpty()) throw new InvalidInputException("Sector metrics has invalid field/s.", errors);
    }

}
