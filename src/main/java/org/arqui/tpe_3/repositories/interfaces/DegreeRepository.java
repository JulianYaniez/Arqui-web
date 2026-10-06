package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;

import java.util.List;

public interface DegreeRepository {

    List<DegreeReportDTO> getReports();

}
