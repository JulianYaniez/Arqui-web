package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.application.ports.factories.DaoFactory;

import java.util.UUID;

public class ClientService {
    private final ClientRepository clientDao;

    public ClientService(DaoFactory factory) {
        this.clientDao = factory.getClientRepository();
    }


    // Should be changed to receive DTO instead
    public void insert(Client client) {
        // Should parse DTO into a Business Entity here
        clientDao.save(client);
    }

    public void get(UUID id) {
        // TODO
    }

    public void getAll() {
        // TODO
    }
}
