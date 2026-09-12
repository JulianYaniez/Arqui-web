package org.arquiweb.repositories.interfaces;

import org.arquiweb.entities.Student;

import java.util.List;
import java.util.UUID;

public interface StudentRepository {
    UUID save(Student student);
    void saveAll(List<Student> students);
}
