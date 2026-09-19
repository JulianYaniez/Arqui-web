package org.arquiweb.util;

import org.arquiweb.util.factories.JpaRepositoryFactory;
import org.arquiweb.util.factories.RepositoryFactory;

public class RepositoryProvider {

    public enum PersistenceTech {
        JPA,
        MONGO
    }

    private static RepositoryFactory factory = new JpaRepositoryFactory();

    public static void setPersistenceTech(PersistenceTech tech) {
        switch (tech) {
            case MONGO -> {}
            case JPA -> {
                factory = JpaRepositoryFactory.getInstance();
            }
        }
    }

}
