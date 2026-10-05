package org.arqui.tpe_3.repositories.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.arqui.tpe_3.dtos.DegreeReportDTO;
import org.arqui.tpe_3.dtos.DegreeYearlyStatsDTO;
import org.arqui.tpe_3.repositories.interfaces.customs.DegreeRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class JpaDegreeRepository implements DegreeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;



    @Override
    public List<DegreeReportDTO> getReports() {

        try {

            record Year(String degree, Integer year, Long count) {}

            String enrollmentsJpql = """
                        SELECT d.name, YEAR(e.startedAt), COUNT(e)
                        FROM Degree d
                        JOIN d.enrollments e
                        GROUP BY d.name, YEAR(e.startedAt)
                        ORDER BY d.name, YEAR(e.startedAt) ASC
                    """;
            List<Year> enrollments = em.createQuery(enrollmentsJpql, Year.class).getResultList();

            String graduatesJpql = """
                        SELECT d.name, YEAR(e.finishedAt), COUNT(e)
                        FROM Degree d
                        JOIN d.enrollments e
                        WHERE e.finishedAt IS NOT NULL AND e.status = FINISHED
                        GROUP BY d.name, YEAR(e.finishedAt)
                        ORDER BY d.name, YEAR(e.finishedAt) ASC
                    """;
            List<Year> graduates = em.createQuery(graduatesJpql, Year.class).getResultList();

    //Check
}
