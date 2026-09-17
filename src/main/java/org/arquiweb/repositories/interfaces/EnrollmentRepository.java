package org.arquiweb.repositories.interfaces;

import org.arquiweb.entities.Enrollment;
import org.arquiweb.entities.Student;

import java.util.List;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    void saveAll(List<Enrollment> enrollments);
}
