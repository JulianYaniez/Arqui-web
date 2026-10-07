package org.arqui.tpe_3.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.dtos.queries.DegreeYearlyStatsDTO;
import org.arqui.tpe_3.dtos.queries.YearDTO;
import org.arqui.tpe_3.entities.Degree;
import org.arqui.tpe_3.repositories.interfaces.DegreeRepository;
import org.arqui.tpe_3.repositories.jpa.DegreeJpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class DegreeRepositoryImpl implements DegreeRepository {

    private final DegreeJpaRepository degreeRepository;

    @Override
    public void save(Degree degree) {
        degreeRepository.save(degree);
    }

    @Override
    public void saveAll(List<Degree> degrees) {
        degreeRepository.saveAll(degrees);
    }

    @Override
    public List<DegreeReportDTO> getReports() {
        try {


            List<YearDTO> enrollments = degreeRepository.enrollmentsYear();

            List<YearDTO> graduates = degreeRepository.graduatesYear();

            Map<String, Map<Integer, DegreeYearlyStatsDTO>> degrees = new TreeMap<>();

            for (YearDTO year : enrollments) {
                DegreeYearlyStatsDTO yearEnrollments = new DegreeYearlyStatsDTO(year.year(), year.count(), 0L);
                Map<Integer, DegreeYearlyStatsDTO> degree = degrees.getOrDefault(year.degree(), new TreeMap<>());

                degree.put(year.year(), yearEnrollments);
                degrees.put(year.degree(), degree);
            }

            for (YearDTO year : graduates) {
                DegreeYearlyStatsDTO yearGraduates = new DegreeYearlyStatsDTO(year.year(), 0L, year.count());
                Map<Integer, DegreeYearlyStatsDTO> degree = degrees.getOrDefault(year.degree(), new TreeMap<>());

                DegreeYearlyStatsDTO completeYear = degree.getOrDefault(year.year(), null);

                if (completeYear == null) {
                    completeYear = yearGraduates;
                } else {
                    completeYear = new DegreeYearlyStatsDTO(
                            completeYear.year(),
                            completeYear.enrollments(),
                            yearGraduates.graduates()
                    );
                }

                degree.put(year.year(), completeYear);
                degrees.put(year.degree(), degree);
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

    @Override
    public List<DegreeEnrollmentsDTO> getEnrollments() {
        return degreeRepository.getEnrollments();
    }
}
