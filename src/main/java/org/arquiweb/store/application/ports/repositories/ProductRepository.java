package org.arquiweb.store.application.ports.repositories;

import org.arquiweb.store.domain.models.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> findById(String id);
    List<Product> findAll();
    UUID save(Product product);
}
