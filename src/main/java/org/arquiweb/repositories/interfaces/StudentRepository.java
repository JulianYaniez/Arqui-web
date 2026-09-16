package org.arquiweb.repositories.interfaces;

import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;

import java.util.List;
import java.util.UUID;

public interface StudentRepository {
    UUID save(Student student);
    void saveAll(List<Student> students);
    List<Student> getAll(String column, String order);
    Student getByRecordNumber(String recordNumber);
    List<Student> getByGenre(Genre genre);
    List<Student> getByDegreeAndCity(UUID degreeId, String city);

}
