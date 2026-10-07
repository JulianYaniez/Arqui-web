package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.dtos.queries.YearDTO;
import org.arqui.tpe_3.entities.Degree;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DegreeRepository {

    void save(Degree degree);
    void saveAll(List<Degree> degrees);
    Optional<Degree> findById(UUID id);

    List<DegreeReportDTO> getReports();

    List<DegreeEnrollmentsDTO> getEnrollments();

}
