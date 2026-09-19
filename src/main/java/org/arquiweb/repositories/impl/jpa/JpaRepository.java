package org.arquiweb.repositories.impl.jpa;

import jakarta.persistence.EntityManager;
import org.arquiweb.util.JpaUtil;

public abstract class JpaRepository {

    protected EntityManager em;

    protected final int batchSize = 50;

    public JpaRepository() {
        this.em = JpaUtil.getEntityManager();
    }
}
