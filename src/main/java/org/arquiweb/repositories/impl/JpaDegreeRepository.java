package org.arquiweb.repositories.impl;

import org.arquiweb.entities.Degree;
import org.arquiweb.repositories.interfaces.DegreeRepository;

import java.util.List;
import java.util.UUID;

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
}
