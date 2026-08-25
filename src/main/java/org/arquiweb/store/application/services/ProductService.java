package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.domain.models.Client;

import java.util.UUID;

public class ProductService {
    private ProductRepository productRepository;

    public ProductService() {
        // TODO - Get repository depending on wanted DB
    }


    public static void insert(Client client) {
        // TODO
    }

    public static void get(UUID id) {
        // TODO
    }

    public static void getAll() {
        // TODO
    }
}
