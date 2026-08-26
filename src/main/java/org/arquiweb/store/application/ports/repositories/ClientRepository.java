package org.arquiweb.store.application.ports.repositories;

import org.arquiweb.store.domain.models.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Optional<Client> findById(UUID id);
    List<Client> findAll();
    UUID save(Client client);
    void saveAll(List<Client> clients);
}
