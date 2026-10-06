package org.arqui.tpe_3.repositories.jpa;

import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.entities.composites.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, EnrollmentId> {

    // This interface uses basic methods such as 'save' and 'saveAll', which are already provided by 'JpaRepository'.
}
