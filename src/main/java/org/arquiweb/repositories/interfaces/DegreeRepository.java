package org.arquiweb.repositories.interfaces;

import org.arquiweb.dto.DegreeReportDTO;
import org.arquiweb.dto.DegreeEnrollmentsDTO;
import org.arquiweb.dto.FullReportDTO;
import org.arquiweb.entities.Degree;

import java.util.List;
import java.util.UUID;

public interface DegreeRepository {
    UUID save(Degree degree);
    void saveAll(List<Degree> degrees);
    List<DegreeEnrollmentsDTO> getEnrollments();

    FullReportDTO getReports();
}
