package org.arqui.tpe_3.repositories.interfaces.customs;

import org.arqui.tpe_3.entities.Student;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface StudentRepositoryCustom {

    List<Student> getAll(String column, String order);
    List<Student> getByDegreeAndCity(UUID degreeId, String city);
}
