package org.arqui.tpe_3.repositories.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.repositories.interfaces.customs.StudentRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaStudentRepository implements StudentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;



    @Override
    public List<Student> getAll(String column, String order) {
        return List.of();
    }

    @Override
    public List<Student> getByDegreeAndCity(UUID degreeId, String city) {
        return List.of();
    }


}