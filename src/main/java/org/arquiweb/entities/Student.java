package org.arquiweb.entities;

import jakarta.persistence.*;
import lombok.*;
import org.arquiweb.enums.Genre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name ="id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "genre", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "city", nullable = false, length = 128)
    private String city;

    @Column(name = "record_number", unique = true, nullable = false, length = 16)
    private String recordNumber;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Enrollment> enrollments =  new ArrayList<>();

    public Student(
            String name,
            String recordNumber,
            String dni,
            Genre genre,
            String city,
            LocalDate dob
    ) {
        this.id = UUID.ofEpochMillis(System.currentTimeMillis());
        this.name = name;
        this.recordNumber = recordNumber;
        this.dni = dni;
        this.genre = genre;
        this.city = city;
        this.dob = dob;
    }
}
