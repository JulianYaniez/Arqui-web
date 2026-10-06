package org.arqui.tpe_3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "degrees")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Degree {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "degree")
    private List<Enrollment> enrollments = new ArrayList<>();

    public Degree(String name) {
        this.name = name;
    }
}
