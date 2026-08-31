package org.arquiweb.store.application.ports.daos;

import org.arquiweb.store.domain.models.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceDao {
    Optional<Invoice> findById(UUID id);
    List<Invoice> findAll();
    UUID save(Invoice invoice);
    void saveAll(List<Invoice> invoices);
}
