package org.arquiweb.entities.composites;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class EnrollmentId implements Serializable {
    private UUID studentId;
    private UUID degreeId;
}
