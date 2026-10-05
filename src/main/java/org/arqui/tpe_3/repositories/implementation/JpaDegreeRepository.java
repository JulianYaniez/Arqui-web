package org.arqui.tpe_3.repositories.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.arqui.tpe_3.dtos.DegreeReportDTO;
import org.arqui.tpe_3.repositories.interfaces.customs.DegreeRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaDegreeRepository implements DegreeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;



    @Override
    public List<DegreeReportDTO> getReports() {
        return List.of();
    }
}
