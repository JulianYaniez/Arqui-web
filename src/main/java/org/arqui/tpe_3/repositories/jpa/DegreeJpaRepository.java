package org.arqui.tpe_3.repositories.jpa;

import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.entities.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DegreeJpaRepository extends JpaRepository<Degree, UUID> {

    @Query("""
        SELECT new org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO(d.name, COUNT(e))
        FROM Degree d JOIN d.enrollments e
        WHERE e.finishedAt IS NULL
        GROUP BY d.name
        ORDER BY COUNT(e)
    """)
    List<DegreeEnrollmentsDTO> getEnrollments();

}