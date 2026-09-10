package org.arquiweb.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.arquiweb.entities.composites.EnrollmentId;
import org.arquiweb.enums.EnrollmentStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Enrollment {

    @EmbeddedId
    private EnrollmentId id =  new EnrollmentId();

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @MapsId("degreeId")
    @JoinColumn(name = "degree_id")
    private Degree degree;

    @Column(name = "started_at", nullable = false)
    private LocalDate startedAt;

    @Column(name = "finished_at")
    private LocalDate finishedAt;

    @Column(name = "status", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    public Enrollment(
            Student student,
            Degree degree
    ) {
        this.student = student;
        this.degree = degree;
        this.startedAt = LocalDate.now();
        this.status = EnrollmentStatus.ONGOING;
    }
}
