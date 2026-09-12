package org.arquiweb.repositories.impl;

import org.arquiweb.entities.Student;
import org.arquiweb.repositories.interfaces.StudentRepository;

import java.util.List;
import java.util.UUID;

public class JpaStudentRepository extends JpaRepository implements StudentRepository {

    public UUID save(Student student) {

        return student.getId();
    }

    public void saveAll(List<Student> students) {

    }
}
