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

            Map<String, Map<Integer, DegreeYearlyStatsDTO>> degrees = new TreeMap<>();

            for (Year year : enrollments) {
                DegreeYearlyStatsDTO yearEnrollments = new DegreeYearlyStatsDTO(year.year, year.count, 0L);
                Map<Integer, DegreeYearlyStatsDTO> degree = degrees.getOrDefault(year.degree, new TreeMap<>());

                degree.put(year.year, yearEnrollments);
                degrees.put(year.degree, degree);
            }

            for (Year year : graduates) {
                DegreeYearlyStatsDTO yearGraduates = new DegreeYearlyStatsDTO(year.year, 0L, year.count);
                Map<Integer, DegreeYearlyStatsDTO> degree = degrees.getOrDefault(year.degree, new TreeMap<>());

                DegreeYearlyStatsDTO completeYear = degree.getOrDefault(year.year, null);

                if (completeYear == null) {
                    completeYear = yearGraduates;
                } else {
                    completeYear = new DegreeYearlyStatsDTO(
                            completeYear.year(),
                            completeYear.enrollments(),
                            yearGraduates.graduates()
                    );
                }

                degree.put(year.year, completeYear);
                degrees.put(year.degree, degree);
            }

            return degrees.entrySet().stream().map(degree -> {
                        String degreeName = degree.getKey();
                        List<DegreeYearlyStatsDTO> stats = degree.getValue().values().stream().toList();
                        return new DegreeReportDTO(degreeName, stats);
                    })
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong fetching the report data ", e);
        }
    }
}
