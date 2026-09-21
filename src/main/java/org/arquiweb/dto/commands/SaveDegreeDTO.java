package org.arquiweb.dto.commands;

import org.arquiweb.entities.Degree;

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