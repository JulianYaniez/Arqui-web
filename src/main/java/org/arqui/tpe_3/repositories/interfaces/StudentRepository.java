package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.repositories.interfaces.customs.StudentRepositoryCustom;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID>, StudentRepositoryCustom {


    @Query("SELECT s FROM Student s WHERE s.universityRecordBook = :universityRecordBook")
    Optional<Student> getByUniversityRecordBook(String universityRecordBook);


    @Query("SELECT s FROM Student s WHERE s.genre = :genre")
    List<Student> getByGenre(Genre genre);
}
