package org.arquiweb.store.application.ports.daos;

import org.arquiweb.store.application.ports.dtos.ProductRevenueDTO;
import org.arquiweb.store.domain.models.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDao {
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    UUID save(Product product);
    void saveAll(List<Product> products);

    Optional<ProductRevenueDTO> findTopRevenueProduct();
}
