package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.dtos.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.DegreeReportDTO;
import org.arqui.tpe_3.entities.Degree;
import org.arqui.tpe_3.repositories.interfaces.customs.DegreeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DegreeRepository extends JpaRepository<Degree, UUID>, DegreeRepositoryCustom {

    @Query("""
        SELECT new org.arqui.tpe_3.dtos.DegreeEnrollmentsDTO(d.name, COUNT(e))
        FROM Degree d JOIN d.enrollments e
        WHERE e.finishedAt IS NULL
        GROUP BY d.name
        ORDER BY COUNT(e)
    """)
    List<DegreeEnrollmentsDTO> getEnrollments();
}