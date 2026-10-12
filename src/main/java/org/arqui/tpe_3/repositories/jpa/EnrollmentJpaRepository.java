package org.arqui.tpe_3.repositories.jpa;

import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.entities.composites.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, EnrollmentId> {

    public List<Enrollment> getAllByStudent_RecordNumber(String recordNumber);

    public Optional<Enrollment> getByDegreeIdAndStudent_RecordNumber(UUID degreeId, String recordNumber);
}
