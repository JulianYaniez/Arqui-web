package org.arqui.tpe_3.dtos.commands;

import org.arqui.tpe_3.entities.Degree;
import org.arqui.tpe_3.exceptions.InvalidInputException;

public record SaveDegreeDTO(
        String name
) {
    public SaveDegreeDTO {
        if(name == null) throw new InvalidInputException("Name is required");
    }

    public Degree toEntity() {
        return  new Degree(name);
    }
}