package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> getByuniversityRecordBook(String universityRecordBook);

    List<Student> getAll(Sort sort);

    List<Student> getByGenre(Genre genre);

    List<Student> getByDegreeAndCity(UUID degreeId, String city);
}
