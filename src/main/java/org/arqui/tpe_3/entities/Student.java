package org.arqui.tpe_3.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arqui.tpe_3.enums.Genre;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "DNI", nullable = false)
    private String dni;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "recordNumber", nullable = false)
    private String recordNumber;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student(String name, LocalDate dob, Genre genre, String dni, String city, String recordNumber) {
        this.name = name;
        this.dob = dob;
        this.genre = genre;
        this.dni = dni;
        this.city = city;
        this.recordNumber = recordNumber;
    }
}
