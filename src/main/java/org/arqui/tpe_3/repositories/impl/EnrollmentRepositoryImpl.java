package org.arqui.tpe_3.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.repositories.interfaces.EnrollmentRepository;
import org.arqui.tpe_3.repositories.jpa.EnrollmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentJpaRepository enrollmentRepository;

    @Override
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> getAllByStudent_RecordNumber(String recordNumber) {
        return enrollmentRepository.getAllByStudent_RecordNumber(recordNumber);
    }

    @Override
    public Optional<Enrollment> getByDegreeIdAndStudent_RecordNumber(UUID degreeId, String recordNumber) {
        return enrollmentRepository.getByDegreeIdAndStudent_RecordNumber(degreeId, recordNumber);
    }


}
