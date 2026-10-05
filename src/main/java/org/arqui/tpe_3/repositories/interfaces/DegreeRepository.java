package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.dtos.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.DegreeReportDTO;
import org.arqui.tpe_3.entities.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DegreeRepository extends JpaRepository<Degree, UUID> {

    List<DegreeEnrollmentsDTO> getEnrollments();

    List<DegreeReportDTO> getReports();
}
