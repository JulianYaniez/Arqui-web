package org.arquiweb.store.application.ports.repositories;

import org.arquiweb.store.domain.models.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Optional<InvoiceRepository> findById(String id);
    List<InvoiceRepository> findAll();
    UUID save(Invoice invoice);
}
