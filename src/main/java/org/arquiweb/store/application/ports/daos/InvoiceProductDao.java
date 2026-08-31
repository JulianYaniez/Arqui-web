package org.arquiweb.store.application.ports.daos;

import org.arquiweb.store.domain.models.InvoiceProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceProductDao {
    Optional<InvoiceProduct> findById(UUID id);
    List<InvoiceProduct> findAll();
    UUID save(InvoiceProduct product);
    void saveAll(List<InvoiceProduct> invoicesProducts);
}
