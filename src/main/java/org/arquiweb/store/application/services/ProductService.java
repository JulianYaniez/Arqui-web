package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.dtos.ProductRevenueDTO;
import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.daos.ProductDao;
import org.arquiweb.store.domain.models.Client;

import java.util.UUID;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(DaoFactory factory) {
        this.productDao = factory.getProductRepository();
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
        var entity = productDao.findTopRevenueProduct();

        if (entity.isEmpty()) {
            throw new RuntimeException("No product found");
        }
        return entity.get();
    }

}
