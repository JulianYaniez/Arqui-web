package org.arquiweb.repositories.impl;

import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;
import org.arquiweb.repositories.interfaces.StudentRepository;

import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

public class JpaStudentRepository extends JpaRepository implements StudentRepository {

    private static JpaStudentRepository instance;
    public static JpaStudentRepository getInstance() {
        if (instance == null) {
            instance = new JpaStudentRepository();
        }
        return instance;
    }

    public UUID save(Student student) {

        try {
            em.getTransaction().begin();
            em.persist(student);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Could not save student " + student);
        } finally {
            em.close();
        }

        return student.getId();
    }

    public void saveAll(List<Student> students) {
        try  {
            em.getTransaction().begin();
            int i  = 0;
            for (Student student : students) {
                em.persist(student);

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
            throw new RuntimeException("Could not save students " + students.toString());
        } finally {
            em.close();
        }
    }

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
    public Student getByRecordNumber(String recordNumber) {
        return null;
    }

    @Override
    public List<Student> getByGenre(Genre genre) {
        try {

            String jpql = "SELECT s FROM Student s WHERE s.genre = :genre";

            return em.createQuery(jpql, Student.class)
                    .setParameter("genre", genre).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Could not get genre", e);
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
