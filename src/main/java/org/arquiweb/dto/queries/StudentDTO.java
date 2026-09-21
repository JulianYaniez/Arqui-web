package org.arquiweb.dto.queries;

import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;

public record StudentDTO(
        String recordNumber,
        String name,
        String dni,
        Genre genre
) {
    @Override
    public String toString() {
        return """
        {
            "recordNumber":  %s,
            "name":  %s,
            "dni":  %s,
            "genre": %s
        }""".formatted(recordNumber, name, dni, genre);
    }

    public static StudentDTO from(Student s) {
        return new StudentDTO(
               s.getRecordNumber(),
               s.getName(),
               s.getDni(),
               s.getGenre()
        );
    }
}
