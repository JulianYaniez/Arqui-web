package org.arquiweb.repositories.impl;

import org.arquiweb.entities.Student;
import org.arquiweb.repositories.interfaces.StudentRepository;

import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

public class JpaStudentRepository extends JpaRepository implements StudentRepository {

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

        final List<String> allowedColums = List.of("name", "dob", "genre", "city");

        if(
            !order.equalsIgnoreCase("ASC") && 
            !order.equalsIgnoreCase("DESC")
        ){
            throw new IllegalArgumentException("Invalid order");
        }

        if(!allowedColums.contains(column)){
            throw new IllegalArgumentException("Invalid column for entity Student");
        }

        try {

            String jpql = "SELECT s FROM Student s ORDER BY :column :order";

            return em.createQuery(jpql, Student.class)
                    .setParameter("column", column)
                    .setParameter("order", order)
                    .getResultList();

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
