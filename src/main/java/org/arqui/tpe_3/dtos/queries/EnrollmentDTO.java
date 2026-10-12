package org.arqui.tpe_3.dtos.queries;

import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.enums.EnrollmentStatus;

import java.time.LocalDate;

public record EnrollmentDTO(
        String degreeName,
        LocalDate startedAt,
        LocalDate finishedAt,
        EnrollmentStatus status
) {

    public static EnrollmentDTO from(Enrollment enrollment) {
        return new EnrollmentDTO(
                enrollment.getDegree().getName(),
                enrollment.getStartedAt(),
                enrollment.getFinishedAt(),
                enrollment.getStatus()
        );
    }
}
