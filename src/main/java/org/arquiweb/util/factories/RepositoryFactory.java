package org.arquiweb.util.factories;

import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.repositories.interfaces.EnrollmentRepository;
import org.arquiweb.repositories.interfaces.StudentRepository;

public interface RepositoryFactory {
    DegreeRepository getDegreeRepository();
    StudentRepository getStudentRepository();
    EnrollmentRepository getEnrollmentRepository();
}
