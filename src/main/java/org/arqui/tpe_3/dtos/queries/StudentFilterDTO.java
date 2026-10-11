package org.arqui.tpe_3.dtos.queries;

import org.arqui.tpe_3.enums.Genre;

import java.util.UUID;

public record StudentFilterDTO(
        Genre genre,
        String city,
        UUID degreeId
) {
}
