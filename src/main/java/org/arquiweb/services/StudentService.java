package org.arquiweb.services;

import org.arquiweb.repositories.interfaces.EnrollmentRepository;
import org.arquiweb.repositories.interfaces.StudentRepository;
import org.arquiweb.util.RepositoryProvider;

public class StudentService {

    private static StudentService instance;

    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService(
                    RepositoryProvider.getStudentRepository(),
                    RepositoryProvider.getEnrollmentRepository()
            );
        }
        return instance;
    }

    private StudentRepository studentRepository;
    private EnrollmentRepository enrollmentRepository;

    private StudentService() {}

    private StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }
}
