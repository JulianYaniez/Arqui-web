package org.arquiweb;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

        emf.close();
    }
}
