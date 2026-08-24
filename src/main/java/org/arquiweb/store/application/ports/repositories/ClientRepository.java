package org.arquiweb.store.application.ports.repositories;

import org.arquiweb.store.domain.models.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Optional<Client> findById(String id);
    List<Client> findAll();
    UUID save(Client client);
}
