package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.dtos.ProductRevenueDTO;
import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.daos.ProductDao;
import org.arquiweb.store.domain.models.Product;

import java.util.List;
import java.util.UUID;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(DaoFactory factory) {
        this.productDao = factory.getProductRepository();
    }


    public void insert(Product product) {
        productDao.save(product);
    }

    public Product get(UUID id) {
        var entity = productDao.findById(id);

        if(entity.isEmpty()) {
            throw new RuntimeException("Invoice with id  " + id + " not found");
        }

        return entity.get();
    }

    public List<Product> getAll() {
        return productDao.findAll();
    }

    public ProductRevenueDTO getTopRevenue() {
        var entity = productDao.findTopRevenueProduct();

        if (entity.isEmpty()) {
            throw new RuntimeException("No product found ");
        }
        return entity.get();
    }

}
