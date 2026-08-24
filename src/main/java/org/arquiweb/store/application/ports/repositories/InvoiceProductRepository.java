package org.arquiweb.store.application.ports.repositories;

import org.arquiweb.store.domain.models.InvoiceProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceProductRepository {
    Optional<InvoiceProduct> findById(String id);
    List<InvoiceProduct> findAll();
    UUID save(InvoiceProduct product);
}
