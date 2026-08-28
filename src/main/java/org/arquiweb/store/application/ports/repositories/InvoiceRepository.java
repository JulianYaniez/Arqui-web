package org.arquiweb.store.application.ports.repositories;

import org.arquiweb.store.domain.models.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Optional<Invoice> findById(UUID id);
    List<Invoice> findAll();
    UUID save(Invoice invoice);
    void saveAll(List<Invoice> invoices);
}
