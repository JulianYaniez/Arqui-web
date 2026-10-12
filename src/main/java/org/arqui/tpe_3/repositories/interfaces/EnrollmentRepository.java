package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.entities.Enrollment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository {

    Enrollment save(Enrollment enrollment);

    List<Enrollment> getAllByStudent_RecordNumber(String recordNumber);

    Optional<Enrollment> getByDegreeIdAndStudent_RecordNumber(UUID degreeId, String recordNumber);
}
