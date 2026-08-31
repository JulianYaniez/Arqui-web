package org.arquiweb.store.application.ports.dtos;

import java.util.UUID;

public record ClientBillingDTO(
        UUID id,
        String name,
        String email,
        int billed
) {
}
