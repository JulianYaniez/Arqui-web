package org.arquiweb.repositories.impl;

import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;
import org.arquiweb.repositories.interfaces.StudentRepository;

import java.util.List;
import java.util.UUID;

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

    @Override
    public List<Student> getAll(String column, String order) {
        return List.of();
    }

    @Override
    public Student getByRecordNumber(String recordNumber) {
        return null;
    }

    @Override
    public List<Student> getByGenre(Genre genre) {
        return List.of();
    }

    @Override
    public List<Student> getByDegreeAndCity(UUID degreeId, String city) {
        return List.of();
    }
}
