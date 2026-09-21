package org.arquiweb.repositories.interfaces;

import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository {
    UUID save(Student student);
    void saveAll(List<Student> students);

    boolean exists(UUID studentId);

    Optional<Student> getByRecordNumber(String recordNumber);
    Optional<Student> getById(UUID studentId);

    List<Student> getAll(String column, String order);
    List<Student> getByGenre(Genre genre);
    List<Student> getByDegreeAndCity(UUID degreeId, String city);

}
