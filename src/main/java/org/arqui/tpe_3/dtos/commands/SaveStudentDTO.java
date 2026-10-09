package org.arqui.tpe_3.dtos.commands;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.exceptions.InvalidInputException;

import java.time.LocalDate;

public record SaveStudentDTO(
        String name,
        String dni,
        String recordNumber,
        String city,
        Genre genre,
        LocalDate dob
) {

    public SaveStudentDTO {
        if (name == null)
            throw new InvalidInputException("Name is required");
        if (dni == null)
            throw new InvalidInputException("DNI is required");
        if (recordNumber == null)
            throw new InvalidInputException("Record Number is required");
        if (city == null)
            throw new InvalidInputException("City is required");
        if (genre == null)
            throw new InvalidInputException("Genre is required");
        if (dob == null)
            throw new InvalidInputException("Date of Birth is required");
    }

    public Student toEntity() {
        return new Student(
                name,
                dob,
                genre,
                dni,
                recordNumber,
                city
        );
    }
}