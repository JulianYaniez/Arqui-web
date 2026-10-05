package org.arqui.tpe_3.repositories.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.repositories.interfaces.customs.StudentRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaStudentRepository implements StudentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;



    @Override
    public List<Student> getAll(String column, String order) {

        final List<String> allowedColumns = List.of("name", "dob", "genre", "city");

        String direction = "ASC".equalsIgnoreCase(order) ? "ASC" : "DESC";

        String orderColumn = switch (column) {
            case "name" -> "s.name";
            case "dob" -> "s.dob";
            case "genre" -> "s.genre";
            case "city" -> "s.city";
            default -> "e.id";
        };


        if(!allowedColumns.contains(column)){
            throw new IllegalArgumentException("Invalid column for entity Student");
        }

        try {

            String jpql = "SELECT s FROM Student s ORDER BY " + orderColumn + " " + direction;

            return em.createQuery(jpql, Student.class).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Could not get students", e);
        }
    }

    @Override
    public List<Student> getByDegreeAndCity(UUID degreeId, String city) {

        try {

            String jpql = """
                SELECT s
                FROM Student s
                JOIN s.enrollments e
                WHERE e.degree.id = :degreeId
                AND s.city = :city
                """;

            return em.createQuery(jpql, Student.class)
                    .setParameter("degreeId", degreeId)
                    .setParameter("city", city)
                    .getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Could not get students", e);
        }
    }


}