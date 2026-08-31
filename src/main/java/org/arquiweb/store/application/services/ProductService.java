package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.dtos.ProductRevenueDTO;
import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.domain.models.Product;

import java.util.UUID;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(DaoFactory factory) {
        this.productRepository = factory.getProductRepository();
    }


    public void insert(Client client) {
        // TODO
    }

    public void get(UUID id) {
        // TODO
    }

    public void getAll() {
        // TODO
    }

    public ProductRevenueDTO getTopRevenue() {
        var entity = productRepository.findTopRevenueProduct();

        if (entity.isEmpty()) {
            throw new RuntimeException("No product found");
        }
        return entity.get();
    }

}
