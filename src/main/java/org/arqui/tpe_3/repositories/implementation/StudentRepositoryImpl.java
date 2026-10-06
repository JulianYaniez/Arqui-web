package org.arqui.tpe_3.repositories.implementation;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.repositories.interfaces.StudentRepository;
import org.arqui.tpe_3.repositories.jpa.StudentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository studentRepository;

    @Override
    public List<Student> getAll(String column, String order) {
        return null;
    }

    @Override
    public List<Student> getByDegreeAndCity(UUID degreeId, String city) {
        return null;
    }


}