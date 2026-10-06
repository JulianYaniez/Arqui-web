package org.arqui.tpe_3.repositories.implementation;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.repositories.interfaces.EnrollmentRepository;
import org.arqui.tpe_3.repositories.jpa.DegreeJpaRepository;
import org.arqui.tpe_3.repositories.jpa.EnrollmenJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmenJpaRepository enrollmenRepository;

}
