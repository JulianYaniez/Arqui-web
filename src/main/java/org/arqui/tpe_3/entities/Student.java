package org.arqui.tpe_3.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arqui.tpe_3.enums.Genre;

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

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "age")
    private int age;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "DNI")
    private String dni;

    @Column(name = "city")
    private String city;

    @Column(name = "universityRecordBook")
    private String universityRecordBook;
}
