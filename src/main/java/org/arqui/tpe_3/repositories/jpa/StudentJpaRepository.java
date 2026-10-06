package org.arqui.tpe_3.repositories.jpa;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentJpaRepository extends JpaRepository<Student, UUID> {


    @Query("SELECT s FROM Student s WHERE s.recordNumber = :universityRecordBook")
    Optional<Student> getByRecordBook(String RecordBook);


    @Query("SELECT s FROM Student s WHERE s.genre = :genre")
    List<Student> getByGenre(Genre genre);

    @Query("""
            SELECT s
            FROM Student s
            JOIN s.enrollments e
            WHERE e.degree.id = :degreeId
            AND s.city = :city
           """)
    List<Student> getByDegreeAndCity(UUID degreeId, String city);
}
