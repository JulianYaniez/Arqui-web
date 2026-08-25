package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.domain.models.Client;

import java.util.UUID;

public class InvoiceService {
    private InvoiceRepository invoiceRepository;

    public InvoiceService() {
        // TODO - Get repository depending on wanted DB
    }


    public static void insert(Client client) {
        // TODO
    }

    public static void get(UUID id) {
        // TODO
    }

    public static void getAll() {
        // TODO
    }
}
