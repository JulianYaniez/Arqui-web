package org.arquiweb.repositories.impl.jpa;

import org.arquiweb.entities.Enrollment;
import org.arquiweb.repositories.interfaces.EnrollmentRepository;

import java.util.List;

public class JpaEnrollmentRepository extends JpaRepository implements EnrollmentRepository {

    public void save(Enrollment enrollment) {
        try {
            em.getTransaction().begin();
            em.persist(enrollment);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Could not save enrollment " + enrollment);
        } finally {
            em.close();
        }
    }

    public void saveAll(List<Enrollment> enrollments) {
        try  {
            em.getTransaction().begin();
            int i  = 0;
            for (Enrollment enrollment : enrollments) {
                em.persist(enrollment);

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
            throw new RuntimeException("Could not save enrollments " + enrollments.toString());
        } finally {
            em.close();
        }
    }
}
