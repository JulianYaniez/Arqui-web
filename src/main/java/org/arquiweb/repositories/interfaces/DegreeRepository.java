package org.arquiweb.repositories.interfaces;

import org.arquiweb.dto.queries.DegreeReportDTO;
import org.arquiweb.dto.queries.DegreeEnrollmentsDTO;
import org.arquiweb.entities.Degree;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DegreeRepository {
    UUID save(Degree degree);
    void saveAll(List<Degree> degrees);

    boolean exists(UUID degreeId);

    Optional<Degree> getById(UUID degreeId);

    List<DegreeEnrollmentsDTO> getEnrollments();
    List<DegreeReportDTO> getReports();
}
