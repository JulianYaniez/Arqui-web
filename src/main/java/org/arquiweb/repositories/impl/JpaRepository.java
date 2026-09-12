package org.arquiweb.repositories.impl;

import jakarta.persistence.EntityManager;
import org.arquiweb.util.JpaUtil;

public abstract class JpaRepository {

    protected EntityManager em;

    public JpaRepository() {
        this.em = JpaUtil.getEntityManager();
    }
}
