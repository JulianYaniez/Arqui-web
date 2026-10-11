package org.arqui.tpe_3.dtos.queries;

import org.arqui.tpe_3.entities.Degree;

import java.util.UUID;

public record DegreeDTO(
        UUID id,
        String name
) {

    public static DegreeDTO from(Degree degree) {
        return new DegreeDTO(degree.getId(), degree.getName());
    }

}
