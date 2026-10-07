package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.dtos.queries.YearDTO;
import org.arqui.tpe_3.entities.Degree;

import java.util.List;

public interface DegreeRepository {

    void save(Degree degree);
    void saveAll(List<Degree> degrees);

    List<DegreeReportDTO> getReports();

    List<DegreeEnrollmentsDTO> getEnrollments();

}
