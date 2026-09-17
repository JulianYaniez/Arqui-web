package org.arquiweb;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.arquiweb.repositories.impl.JpaDegreeRepository;
import org.arquiweb.repositories.impl.JpaEnrollmentRepository;
import org.arquiweb.repositories.impl.JpaStudentRepository;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

        var jpaDegreeRepository = new JpaDegreeRepository();
        var jpaStudentRepository = new JpaStudentRepository();
        var jpaEnrollmentRepository = new JpaEnrollmentRepository();

        var reports = jpaDegreeRepository.getReports();

        System.out.println(reports);

        emf.close();
    }
}
