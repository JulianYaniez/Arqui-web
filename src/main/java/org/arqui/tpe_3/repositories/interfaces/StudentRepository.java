package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository {

    List<Student> getAll(String column, String order);

    List<Student> getByDegreeAndCity(UUID degreeId, String city);

    Optional<Student> getByRecordBook(String RecordBook);

    List<Student> getByGenre(Genre genre);
}
