package org.arquiweb.store.application.ports.dtos;

import java.util.UUID;

public record ProductRevenueDTO(
        UUID id,
        String name,
        int value,
        int totalRevenue
) {

}
