package org.arqui.tpe_3.repositories.jpa;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentJpaRepository extends JpaRepository<Student, UUID> {



@Query("SELECT s FROM Student s WHERE s.recordNumber = :RecordBook")
Optional<Student> getByRecordNumber(@Param("RecordBook") String RecordBook);


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


    @Query("""
        SELECT s FROM Student s WHERE
        (:genre IS NULL OR s.genre = :genre) AND
        (:city IS NULL OR s.city = :city) AND
        (:degree IS NULL OR EXISTS (
            SELECT 1 FROM Student s2 JOIN s2.enrollments e
            WHERE s2 = s AND e.degree.id = :degree
        ))
    """)
    Page<Student> filterStudents(
            @Param("genre") Genre genre,
            @Param("city") String city,
            @Param("degree") UUID degree,
            Pageable pageable
    );

    boolean existsByRecordNumber(String recordNumber);
}
