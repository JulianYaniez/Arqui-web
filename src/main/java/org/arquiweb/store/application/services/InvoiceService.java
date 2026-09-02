package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.daos.InvoiceDao;
import org.arquiweb.store.domain.models.Invoice;

import java.util.List;
import java.util.UUID;

public class InvoiceService {
    private final InvoiceDao invoiceDao;

    public InvoiceService(DaoFactory factory) {
        this.invoiceDao = factory.getInvoiceRepository();
    }


    public void insert(Invoice invoice) {
        invoiceDao.save(invoice);
    }

    public Invoice get(UUID id) {
        var entity = invoiceDao.findById(id);
        
        if(entity.isEmpty()) {
            throw new RuntimeException("Invoice with id  " + id + " not found ");
        }

        return entity.get();
    }

    public List<Invoice> getAll() {
        return invoiceDao.findAll();
    }
}
