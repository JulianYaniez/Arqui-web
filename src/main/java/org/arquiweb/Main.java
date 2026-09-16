package org.arquiweb;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.arquiweb.dto.DegreeEnrollmentsDTO;
import org.arquiweb.repositories.impl.JpaDegreeRepository;

import java.util.List;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

        var jdr = new JpaDegreeRepository();
        List<DegreeEnrollmentsDTO> testList = jdr.getEnrollments();

        System.out.println(testList);
        emf.close();
    }
}
