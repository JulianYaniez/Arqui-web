package org.arqui.tpe_3.repositories.interfaces.customs;

import org.arqui.tpe_3.dtos.DegreeReportDTO;

import java.util.List;

public interface DegreeRepositoryCustom {

    List<DegreeReportDTO> getReports();
}
