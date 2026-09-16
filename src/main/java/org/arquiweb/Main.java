package org.arquiweb;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.arquiweb.dto.DegreeEnrollmentsDTO;
import org.arquiweb.entities.Student;
import org.arquiweb.repositories.impl.JpaDegreeRepository;
import org.arquiweb.repositories.impl.JpaEnrollmentRepository;
import org.arquiweb.repositories.impl.JpaStudentRepository;

import java.util.List;
import java.util.UUID;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

        var jpaDegreeRepository = new JpaDegreeRepository();
        var jpaStudentRepository = new JpaStudentRepository();
        var jpaEnrollmentRepository = new JpaEnrollmentRepository();


        emf.close();
    }
}
