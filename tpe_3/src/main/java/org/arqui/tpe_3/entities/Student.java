package org.arqui.tpe_3.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Student {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
}
