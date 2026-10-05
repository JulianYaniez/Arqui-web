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
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "dob")
    private LocalDate dob;

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

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student(
            UUID id,
            String name,
            LocalDate dob,
            int age,
            Genre genre,
            String dni,
            String city,
            String universityRecordBook
    ) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.age = age;
        this.genre = genre;
        this.dni = dni;
        this.city = city;
        this.universityRecordBook = universityRecordBook;
    }
}
