package org.arqui.tpe_3.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.repositories.interfaces.DegreeRepository;
import org.arqui.tpe_3.repositories.jpa.DegreeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DegreeRepositoryImpl implements DegreeRepository {

    private final DegreeJpaRepository degreeRepository;

    @Override
    public List<DegreeReportDTO> getReports() {
        return null;
    }
}
