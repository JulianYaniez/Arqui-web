package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.daos.InvoiceDao;
import org.arquiweb.store.domain.models.Client;

import java.util.UUID;

public class InvoiceService {
    private final InvoiceDao invoiceDao;

    public InvoiceService(DaoFactory factory) {
        this.invoiceDao = factory.getInvoiceRepository();
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
}
