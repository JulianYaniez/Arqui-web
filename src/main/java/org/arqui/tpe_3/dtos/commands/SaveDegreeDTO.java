package org.arqui.tpe_3.dtos.commands;

import org.arqui.tpe_3.entities.Degree;

public record SaveDegreeDTO(
        String name
) {
    public SaveDegreeDTO {
        if(name == null) throw new IllegalArgumentException("Name is required");
    }

    public Degree toEntity() {
        return  new Degree(name);
    }
}