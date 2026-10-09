package org.arqui.tpe_3.dtos.queries;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;

public record StudentDTO(
        String recordNumber,
        String name,
        String dni,
        Genre genre
) {

    public static StudentDTO from(Student s) {
        return new StudentDTO(
                s.getRecordNumber(),
                s.getName(),
                s.getDni(),
                s.getGenre()
        );
    }
}
