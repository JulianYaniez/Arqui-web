package org.arquiweb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
