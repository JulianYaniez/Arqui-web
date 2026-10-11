package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.dtos.queries.StudentFilterDTO;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository {

    Student save(Student student);

    Optional<Student>  findById(UUID id);

    List<Student> filterStudents(StudentFilterDTO filter, Pageable pageable);

    List<Student> getStudents(String column, String order);

    List<Student> getByDegreeAndCity(UUID degreeId, String city);

    Optional<Student> getByRecordBook(String RecordBook);

    List<Student> getByGenre(Genre genre);
}
