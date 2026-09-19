package org.arquiweb.util.factories;

import org.arquiweb.repositories.impl.jpa.JpaDegreeRepository;
import org.arquiweb.repositories.impl.jpa.JpaEnrollmentRepository;
import org.arquiweb.repositories.impl.jpa.JpaStudentRepository;
import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.repositories.interfaces.EnrollmentRepository;
import org.arquiweb.repositories.interfaces.StudentRepository;

public class JpaRepositoryFactory implements RepositoryFactory {

    private static RepositoryFactory instance;

    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new JpaRepositoryFactory();
        }
        return instance;
    }

    private JpaRepositoryFactory() {}

    private DegreeRepository degreeRepository;
    private StudentRepository studentRepository;
    private EnrollmentRepository enrollmentRepository;

    public DegreeRepository getDegreeRepository() {
        if (degreeRepository == null) {
            degreeRepository = new JpaDegreeRepository();
        }
        return degreeRepository;
    }

    public StudentRepository getStudentRepository() {
        if (studentRepository == null) {
            studentRepository = new JpaStudentRepository();
        }
        return studentRepository;
    }

    public EnrollmentRepository getEnrollmentRepository() {
        if (enrollmentRepository == null) {
            enrollmentRepository = new JpaEnrollmentRepository();
        }
        return enrollmentRepository;
    }
}
