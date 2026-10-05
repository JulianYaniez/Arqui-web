package org.arqui.tpe_3.repositories.interfaces;

import org.arqui.tpe_3.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
}
