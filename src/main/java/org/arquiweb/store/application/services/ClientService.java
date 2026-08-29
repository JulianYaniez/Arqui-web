package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.application.ports.factories.DaoFactory;

import java.util.List;
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

    public Client get(UUID id) {
        var entity = clientDao.findById(id);

        if (entity.isEmpty()) {
            throw new RuntimeException("Client with id " + id + " not found");
        }

        return entity.get();
    }

    public List<Client> getAll() {
        return clientDao.findAll();
    }
}
