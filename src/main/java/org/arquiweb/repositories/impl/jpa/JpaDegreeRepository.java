package org.arquiweb.repositories.impl.jpa;

import org.arquiweb.dto.queries.DegreeEnrollmentsDTO;
import org.arquiweb.dto.queries.DegreeReportDTO;
import org.arquiweb.dto.queries.DegreeYearlyStatsDTO;
import org.arquiweb.entities.Degree;
import org.arquiweb.repositories.interfaces.DegreeRepository;

import java.util.*;

public class JpaDegreeRepository extends JpaRepository implements DegreeRepository {

    public UUID save(Degree degree) {
        try {
            em.getTransaction().begin();
            em.persist(degree);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Could not save degree " + degree);
        } finally {
            em.close();
        }
        return degree.getId();
    }

    public void saveAll(List<Degree> degrees) {

        try  {
            em.getTransaction().begin();
            int i  = 0;
            for (Degree degree : degrees) {
                em.persist(degree);

                if (++i % this.batchSize == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
        }  catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Could not save degrees " + degrees.toString());
        } finally {
            em.close();
        }
    }

    @Override
    public boolean exists(UUID degreeId) {
        try  {
            Degree degree = em.find(Degree.class, degreeId);
            return degree != null;
        } catch (Exception e) {
            throw new RuntimeException("Could not find degree " + degreeId);
        }
    }

    @Override
    public Optional<Degree> getById(UUID degreeId) {
        try {
            String jpql = "SELECT d FROM Degree d WHERE d.id = :id";
            return em.createQuery(jpql, Degree.class).setParameter("id", degreeId)
                    .getResultList().stream()
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Could not find degree " + degreeId);
        }
    }

    @Override
    public List<DegreeEnrollmentsDTO> getEnrollments() {
        try {
            String jpql = """
                            SELECT new org.arquiweb.dto.queries.DegreeEnrollmentsDTO(d.name, COUNT(e))
                            FROM Degree d JOIN d.enrollments e
                            WHERE e.finishedAt IS NULL
                            GROUP BY d.name
                        """;
            return em.createQuery(jpql, DegreeEnrollmentsDTO.class).getResultList();

        } catch(Exception e ) {
            return List.of();
        }
    }


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