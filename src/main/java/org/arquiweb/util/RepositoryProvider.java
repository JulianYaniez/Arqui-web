package org.arquiweb.util;

import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.repositories.interfaces.EnrollmentRepository;
import org.arquiweb.repositories.interfaces.StudentRepository;
import org.arquiweb.util.factories.JpaRepositoryFactory;
import org.arquiweb.util.factories.RepositoryFactory;

public class RepositoryProvider {

    public enum PersistenceTech {
        JPA,
        MONGO
    }

    private static RepositoryFactory factory = JpaRepositoryFactory.getInstance();

    public static void setPersistenceTech(PersistenceTech tech) {
        switch (tech) {
            case MONGO -> {}
            case JPA -> {
                factory = JpaRepositoryFactory.getInstance();
            }
        }
    }

    public static DegreeRepository getDegreeRepository() {
        return factory.getDegreeRepository();
    }

    public static StudentRepository getStudentRepository() {
        return factory.getStudentRepository();
    }

    public static EnrollmentRepository getEnrollmentRepository() {
        return factory.getEnrollmentRepository();
    }

}
