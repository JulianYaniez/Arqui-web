package org.arquiweb.dto.commands;

import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;

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
            throw new IllegalArgumentException("Name is required");
        if (dni == null)
            throw new IllegalArgumentException("DNI is required");
        if (recordNumber == null)
            throw new IllegalArgumentException("Record Number is required");
        if (city == null)
            throw new IllegalArgumentException("City is required");
        if (genre == null)
            throw new IllegalArgumentException("Genre is required");
        if (dob == null)
            throw new IllegalArgumentException("Date of Birth is required");
    }

    public Student toEntity() {
        return new Student(
                name,
                recordNumber,
                dni,
                genre,
                city,
                dob
        );
    }
}
